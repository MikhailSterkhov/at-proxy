package net.advanceteam.proxy.connection.player;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.callback.Callback;
import net.advanceteam.proxy.common.chat.ChatMessageType;
import net.advanceteam.proxy.common.chat.component.BaseComponent;
import net.advanceteam.proxy.common.chat.component.TextComponent;
import net.advanceteam.proxy.common.chat.serializer.ComponentSerializer;
import net.advanceteam.proxy.common.command.CommandSender;
import net.advanceteam.proxy.common.command.type.CommandSendingType;
import net.advanceteam.proxy.common.event.impl.ServerConnectEvent;
import net.advanceteam.proxy.connection.server.impl.Server;
import net.advanceteam.proxy.connection.server.request.ServerConnectRequest;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.game.ChatPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.game.DisconnectPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.game.PlayerListHeaderFooterPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.game.TitlePacket;
import net.advanceteam.proxy.netty.protocol.client.packet.handshake.HandshakePacket;
import net.advanceteam.proxy.netty.protocol.client.packet.login.LoginRequestPacket;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * В данном классе проводятся различные операции с
 * самим игроком, которые в большей своей части
 * выполняются благодаря отдельному {@link Channel}
 *
 * Данный объект наследует {@link CommandSender},
 * что позволяет ему отправлять сообщения, а также
 * выполнять различные команды от его лица.
 */
@AllArgsConstructor
@Getter
public class Player implements CommandSender {

    private final String name;
    private final UUID uniqueId;

    private Server server;

    private InetSocketAddress address;

    private final Channel channel;

    @Setter
    private List<String> permissions;

    @Setter
    private ClientVersion minecraftVersion;
    private boolean connected;

    private HandshakePacket lastHandshake;

    private final CommandSendingType commandSendingType = CommandSendingType.PLAYER;


    /**
     * Подключить игрока к другому серверу
     *
     * @param serverName - имя сервера
     */
    public void connect(String serverName) {
        connect( AdvanceProxy.getInstance().getServer(serverName) );
    }

    /**
     * Подключить игрока к другому серверу
     *
     * @param server - сервер
     */
    public void connect(Server server) {
        connect(server, null, ServerConnectEvent.Reason.UNKNOWN);
    }

    /**
     * Подключить игрока к другому серверу
     *
     * @param server - сервер
     * @param callback - callback, возвращающий булеан о том, успешен запрос или нет
     * @param reason - причина подключения
     */
    public void connect(Server server, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
        ServerConnectRequest.Builder builder = ServerConnectRequest.builder()
                .reason(reason)
                .target(server);

        if (callback != null) {
            builder.callback((result, error) -> callback.done(
                    (result == ServerConnectRequest.Result.SUCCESS) ? Boolean.TRUE : Boolean.FALSE, error));
        }

        connect(builder.build());
    }

    /**
     * Подключить игрока к другому серверу
     *
     * @param server - сервер
     * @param callback - callback, возвращающий булеан о том, успешен запрос или нет
     */
    public void connect(Server server, Callback<Boolean> callback) {
        connect(server, callback, ServerConnectEvent.Reason.UNKNOWN);
    }

    /**
     * Подключить игрока к другому серверу
     *
     * @param connectRequest - запрос на подключение к серверу
     */
    private void connect(ServerConnectRequest connectRequest) {
        Callback<ServerConnectRequest.Result> callback = connectRequest.getCallback();
        ServerConnectEvent event = new ServerConnectEvent(this, connectRequest.getTarget(), connectRequest.getReason());

        AdvanceProxy.getInstance().getEventManager().callEvent(event);

        if ( event.isCancelled() ) {
            if (callback != null) {
                callback.done(ServerConnectRequest.Result.EVENT_CANCEL, null);
            }

            if (getServer() == null && channel.isOpen()) {
                throw new IllegalStateException("Cancelled ServerConnectEvent with no server or disconnect.");
            }

            return;
        }

        Server target = event.getTarget();

        if ( server != null && server.equals(target) ) {
            if (callback != null) {
                callback.done(ServerConnectRequest.Result.ALREADY_CONNECTED, null);
            }

            sendMessage("§cОшибка, Вы уже подключены к данному серверу!");
            return;
        }

        this.server = target;

        AdvanceProxy.getInstance().getBootstrapStarter().connectServer(channel.eventLoop(), target.getInetAddress(), future -> {

            if (callback != null) {
                callback.done((future.isSuccess()) ? ServerConnectRequest.Result.SUCCESS : ServerConnectRequest.Result.FAIL, future.cause());
            }

            if ( !future.isSuccess() ) {
                future.channel().close();

                if ( !connected ) {
                    disconnect("§cГлавный Proxy сервер недоступен: " + future.cause().getCause());
                    return;
                }

                sendMessage("§cОтключено, невозможно подключиться к серверу!");
                return;
            }

            sendPacket(new HandshakePacket(minecraftVersion.getVersion(), lastHandshake.getHost(), lastHandshake.getPort(), 2));
            sendPacket(new LoginRequestPacket(name));

            this.connected = true;
        });
    }

    /**
     * Кикнуть игрока с сервера
     *
     * @param reason - причина, которая будет писаться
     *               при кике с сервера.
     */
    public void disconnect(String reason) {
        disconnect(TextComponent.fromLegacyText(reason));
    }

    /**
     * Кикнуть игрока с сервера
     *
     * @param reason - причина, которая будет писаться
     *               при кике с сервера.
     */
    public void disconnect(BaseComponent... reason) {
        AdvanceProxy.getInstance().getPlayerManager().disconnectPlayer(name);

        sendPacket( new DisconnectPacket(ComponentSerializer.toString(reason)) );

        this.channel.close();
        this.connected = false;
    }

    /**
     * Отправить игроку сообщение на экран
     *
     * @param title - заголовок
     * @param subtitle - подзаголовок
     */
    public void sendTitle(String title, String subtitle) {
        sendTitle(title, subtitle, 1, 3, 1);
    }

    /**
     * Отправить игроку сообщение на экран
     *
     * @param title - заголовок
     * @param subtitle - подзаголовок
     * @param fadeIn - время появления
     * @param stay - время показа
     * @param fadeOut - время ухода
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendPacket( createTitle(TitlePacket.Action.CLEAR, fadeIn, stay, fadeOut) );
        sendPacket( createTitle(TitlePacket.Action.RESET, fadeIn, stay, fadeOut) );
        sendPacket( createTitle(TitlePacket.Action.TIMES, fadeIn, stay, fadeOut) );
        sendPacket( createTitle(TitlePacket.Action.TITLE, title) );
        sendPacket( createTitle(TitlePacket.Action.SUBTITLE, subtitle) );
    }

    private TitlePacket createTitle(TitlePacket.Action titleAction, int fadeIn, int stay, int fadeOut) {
        TitlePacket titlePacket = new TitlePacket();
        titlePacket.setAction( titleAction );

        if (titleAction == TitlePacket.Action.TIMES) {
            titlePacket.setFadeIn(fadeIn * 20);
            titlePacket.setStay(stay * 20);
            titlePacket.setFadeOut(fadeOut * 20);
        }

        return titlePacket;
    }

    private TitlePacket createTitle(TitlePacket.Action titleAction, String text) {
        TitlePacket titlePacket = new TitlePacket();
        titlePacket.setAction( titleAction );

        titlePacket.setText(text);

        return titlePacket;
    }

    /**
     * Украсить список игроков
     *
     * @param header - заголовок
     * @param footer - нижняя часть
     */
    public void setTab(String header, String footer) {
        PlayerListHeaderFooterPacket packet = new PlayerListHeaderFooterPacket(header, footer);

        sendPacket( packet );
    }

    /**
     * Украсить список игроков
     *
     * @param header - заголовок
     * @param footer - нижняя часть
     */
    public void setTab(BaseComponent header, BaseComponent footer) {
        setTab(TextComponent.toLegacyText(header), TextComponent.toLegacyText(footer));
    }

    /**
     * Украсить список игроков
     *
     * @param header - заголовок
     * @param footer - нижняя часть
     */
    public void setTab(BaseComponent[] header, BaseComponent[] footer) {
        setTab(TextComponent.toLegacyText(header), TextComponent.toLegacyText(footer));
    }

    /**
     * Добавить право в список прав
     *
     * @param permission - право
     */
    public void addPermission(String permission) {
        permissions.add(permission);
    }

    /**
     * Удалить право в список прав
     *
     * @param permission - право
     */
    public void removePermission(String permission) {
        permissions.remove(permission);
    }

    /**
     * Проверить игрока на право
     *
     * @param permission - право
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    /**
     * Очистить список прав
     */
    public void clearPermissions() {
        permissions.clear();
    }

    /**
     * Отправить сообщение игроку
     *
     * @param message - сообщение
     */
    @Override
    public void sendMessage(String message) {
        sendMessage(ChatMessageType.CHAT, message);
    }

    /**
     * Отправить список сообщений игроку
     *
     * @param messages - список сообщений
     */
    @Override
    public void sendMessage(String... messages) {
        sendMessage(ChatMessageType.CHAT, messages);
    }

    /**
     * Отправить игроку сообщение определенным типом
     *
     * @param messageType - тип сообщения
     * @param message - сообщение
     */
    @Override
    public void sendMessage(ChatMessageType messageType, String message) {
        sendPacket( new ChatPacket(message, (byte) ChatMessageType.CHAT.ordinal()) );
    }

    /**
     * Отправить игроку список сообщений определенного типа
     *
     * @param messageType - тип сообщения
     * @param messages - сообщение
     */
    @Override
    public void sendMessage(ChatMessageType messageType, String... messages) {
        Arrays.asList(messages).forEach(message -> sendMessage(messageType, message));
    }

    /**
     * Отправить пакет игроку
     *
     * @param clientPacket - пакет
     */
    public void sendPacket(ClientPacket clientPacket) {
        if ( !channel.isOpen() ) {
            return;
        }

        channel.writeAndFlush(clientPacket);
    }

}
