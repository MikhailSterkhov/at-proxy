package net.advanceteam.proxy.netty.protocol.client.packet.game;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.chat.component.TextComponent;
import net.advanceteam.proxy.common.event.impl.PluginMessageEvent;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.Server;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketDecoder;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.Locale;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ClientPacketHandler(packetQuery = "GAME")
public class PluginMessagePacket implements ClientPacket {

    public static final Function<String, @Nullable String> MODERNISE = tag -> {
        // Transform as per Bukkit
        if (tag.equals("BungeeCord")) {
            return "bungeecord:main";
        }
        if (tag.equals("bungeecord:main")) {
            return "BungeeCord";
        }

        // Code that gets to here is UNLIKELY to be viable on the Bukkit side of side things,
        // but we keep it anyway. It will eventually be enforced API side.
        if (tag.indexOf(':') != -1) {
            return tag;
        }

        return "legacy:" + tag.toLowerCase(Locale.ROOT);
    };

    public static final Predicate<PluginMessagePacket> SHOULD_RELAY = input -> (input.getTag().equals("REGISTER") || input.getTag().equals("minecraft:register")
            || input.getTag().equals("MC|Brand") || input.getTag().equals("minecraft:brand"))

            && input.getData().length < Byte.MAX_VALUE;

    private String tag;
    private byte[] data;

    public PluginMessagePacket(String bound) {
        switch (bound.toLowerCase()) {
            case "client": {
                registerClientPacket(ClientVersion.V1_8, 0x3F);
                registerClientPacket(ClientVersion.V1_9, 0x18);
                registerClientPacket(ClientVersion.V1_9_1, 0x18);
                registerClientPacket(ClientVersion.V1_9_2, 0x18);
                registerClientPacket(ClientVersion.V1_9_3, 0x18);
                registerClientPacket(ClientVersion.V1_9_4, 0x18);
                registerClientPacket(ClientVersion.V1_10, 0x18);
                registerClientPacket(ClientVersion.V1_11, 0x18);
                registerClientPacket(ClientVersion.V1_11_1, 0x18);
                registerClientPacket(ClientVersion.V1_11_2, 0x18);
                registerClientPacket(ClientVersion.V1_12, 0x18);
                registerClientPacket(ClientVersion.V1_12_1, 0x18);
                registerClientPacket(ClientVersion.V1_12_2, 0x18);
                registerClientPacket(ClientVersion.V1_13, 0x18);
                registerClientPacket(ClientVersion.V1_13_1, 0x18);
                registerClientPacket(ClientVersion.V1_13_2, 0x18);
                registerClientPacket(ClientVersion.V1_14, 0x47);
                registerClientPacket(ClientVersion.V1_14_1, 0x47);
                registerClientPacket(ClientVersion.V1_14_2, 0x47);
                registerClientPacket(ClientVersion.V1_14_3, 0x47);
                registerClientPacket(ClientVersion.V1_14_4, 0x47);
                registerClientPacket(ClientVersion.V1_15, 0x47);
                registerClientPacket(ClientVersion.V1_15_1, 0x47);
                registerClientPacket(ClientVersion.V1_15_2, 0x47);
                break;
            }

            case "server": {
                registerClientPacket(ClientVersion.V1_8, 0x17);
                registerClientPacket(ClientVersion.V1_9, 0x09);
                registerClientPacket(ClientVersion.V1_9_1, 0x09);
                registerClientPacket(ClientVersion.V1_9_2, 0x09);
                registerClientPacket(ClientVersion.V1_9_3, 0x09);
                registerClientPacket(ClientVersion.V1_9_4, 0x09);
                registerClientPacket(ClientVersion.V1_10, 0x09);
                registerClientPacket(ClientVersion.V1_11, 0x09);
                registerClientPacket(ClientVersion.V1_11_1, 0x09);
                registerClientPacket(ClientVersion.V1_11_2, 0x09);
                registerClientPacket(ClientVersion.V1_12, 0x09);
                registerClientPacket(ClientVersion.V1_12_1, 0x09);
                registerClientPacket(ClientVersion.V1_12_2, 0x09);
                registerClientPacket(ClientVersion.V1_13, 0x09);
                registerClientPacket(ClientVersion.V1_13_1, 0x09);
                registerClientPacket(ClientVersion.V1_13_2, 0x09);
                registerClientPacket(ClientVersion.V1_14, 0x09);
                registerClientPacket(ClientVersion.V1_14_1, 0x09);
                registerClientPacket(ClientVersion.V1_14_2, 0x09);
                registerClientPacket(ClientVersion.V1_14_3, 0x09);
                registerClientPacket(ClientVersion.V1_14_4, 0x09);
                registerClientPacket(ClientVersion.V1_15, 0x09);
                registerClientPacket(ClientVersion.V1_15_1, 0x09);
                registerClientPacket(ClientVersion.V1_15_2, 0x09);
                break;
            }
        }
    }

    /**
     * Allow this packet to be sent as an "extended" packet.
     */
    private boolean allowExtendedPacket = false;

    public DataInput getStream() {
        return new DataInputStream(new ByteArrayInputStream(data));
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString((clientVersion >= ClientVersion.V1_13.getVersion()) ? MODERNISE.apply(tag) : tag);
        channelPacketBuffer.writeBytes(data);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        tag = (clientVersion >= ClientVersion.V1_13.getVersion())
                ? MODERNISE.apply(channelPacketBuffer.readString()) : channelPacketBuffer.readString();

        //int maxSize = direction == ProtocolConstants.Direction.TO_SERVER ? Short.MAX_VALUE : 0x100000;

        Preconditions.checkArgument(channelPacketBuffer.readableBytes() < Short.MAX_VALUE); //maxSize);
        data = new byte[channelPacketBuffer.readableBytes()];

        channelPacketBuffer.readBytes(data);
    }

    @Override
    public void handle(Channel channel) {
        ClientPacketDecoder packetDecoder = channel.pipeline().get(ClientPacketDecoder.class);

        Player receiver = AdvanceProxy.getInstance().getPlayer(packetDecoder.getPlayerName());
        Server server = receiver.getServer();

        DataInput in = getStream();
        PluginMessageEvent event = new PluginMessageEvent(server, receiver, tag, data.clone());

        AdvanceProxy.getInstance().getEventManager().callEvent(event);

        if (event.isCancelled()) {
            throw new IllegalArgumentException();
        }

        try {
            if (tag.equals(receiver.getMinecraftVersion().getVersion() >= ClientVersion.V1_13.getVersion() ? "minecraft:brand" : "MC|Brand")) {
                ChannelPacketBuffer brand = new ChannelPacketBuffer(Unpooled.wrappedBuffer(data));
                String serverBrand = brand.readString();

                brand.release();

                Preconditions.checkState(!serverBrand.contains("AdvanceProxy"), "Cannot connect proxy to itself!");

                brand = new ChannelPacketBuffer(ByteBufAllocator.DEFAULT.heapBuffer());
                brand.writeString("AdvanceProxy <- " + serverBrand);

                setData(brand.toArray());

                brand.release();
                receiver.sendPacket(this);

                throw new IllegalArgumentException();
            }

            if (tag.equals("BungeeCord")) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                String subChannel = in.readUTF();

                if (subChannel.equals("ForwardToPlayer")) {
                    Player target = AdvanceProxy.getInstance().getPlayer(in.readUTF());
                    if (target != null) {
                        // Read data from server
                        String channelMessage = in.readUTF();
                        short len = in.readShort();
                        byte[] data = new byte[len];
                        in.readFully(data);

                        // Prepare new data to send
                        out.writeUTF(channelMessage);
                        out.writeShort(data.length);
                        out.write(data);
                        byte[] payload = out.toByteArray();

                        target.getServer().sendData("BungeeCord", payload);
                    }

                    // Null out stream, important as we don't want to send to ourselves
                    out = null;
                }
                if (subChannel.equals("Forward")) {
                    // Read data from server
                    String target = in.readUTF();
                    String channelMessage = in.readUTF();
                    short len = in.readShort();
                    byte[] data = new byte[len];
                    in.readFully(data);

                    // Prepare new data to send
                    out.writeUTF(channelMessage);
                    out.writeShort(data.length);
                    out.write(data);
                    byte[] payload = out.toByteArray();

                    // Null out stream, important as we don't want to send to ourselves
                    out = null;

                    if (target.equals("ALL")) {
                        for (Server server1 : AdvanceProxy.getInstance().getServers().values()) {
                            server1.sendData("BungeeCord", payload);
                        }
                    } else if (target.equals("ONLINE")) {
                        for (Server server1 : AdvanceProxy.getInstance().getServers().values()) {
                            server1.sendData("BungeeCord", payload);
                        }
                    } else {
                        Server server1 = AdvanceProxy.getInstance().getServer(target);
                        if (server1 != null) {
                            server1.sendData("BungeeCord", payload);
                        }
                    }
                }
                if (subChannel.equals("Connect")) {
                    Server server1 = AdvanceProxy.getInstance().getServer(in.readUTF());
                    if (server1 != null) {
                        receiver.connect(server);
                    }
                }
                if (subChannel.equals("ConnectOther")) {
                    Player player = AdvanceProxy.getInstance().getPlayer(in.readUTF());
                    if (player != null) {
                        Server server1 = AdvanceProxy.getInstance().getServer(in.readUTF());
                        if (server1 != null) {
                            player.connect(server1);
                        }
                    }
                }
                if (subChannel.equals("IP")) {
                    out.writeUTF("IP");
                    out.writeUTF(receiver.getAddress().getHostString());
                    out.writeInt(receiver.getAddress().getPort());
                }
                if (subChannel.equals("PlayerCount")) {
                    String target = in.readUTF();
                    out.writeUTF("PlayerCount");
                    if (target.equals("ALL")) {
                        out.writeUTF("ALL");
                        out.writeInt(AdvanceProxy.getInstance().getOnlineCount());
                    } else {
                        Server server1 = AdvanceProxy.getInstance().getServer(target);
                        if (server1 != null) {
                            out.writeUTF(server1.getName());
                            out.writeInt(server1.getOnlineCount());
                        }
                    }
                }
                if (subChannel.equals("PlayerList")) {
                    String target = in.readUTF();
                    out.writeUTF("PlayerList");
                    if (target.equals("ALL")) {
                        out.writeUTF("ALL");
                        out.writeUTF(Joiner.on(", ").join(AdvanceProxy.getInstance().getOnlinePlayers()));
                    } else {
                        Server server1 = AdvanceProxy.getInstance().getServer(target);
                        if (server1 != null) {
                            out.writeUTF(server1.getName());
                            out.writeUTF(Joiner.on(", ").join(server1.getOnlinePlayers()));
                        }
                    }
                }
                if (subChannel.equals("GetServers")) {
                    out.writeUTF("GetServers");
                    out.writeUTF(Joiner.on(", ").join(AdvanceProxy.getInstance().getServers().keySet()));
                }
                if (subChannel.equals("Message")) {
                    String target = in.readUTF();
                    String message = in.readUTF();
                    if (target.equals("ALL")) {
                        for (Player player : AdvanceProxy.getInstance().getOnlinePlayers()) {
                            player.sendMessage(message);
                        }
                    } else {
                        Player player = AdvanceProxy.getInstance().getPlayer(target);
                        if (player != null) {
                            player.sendMessage(message);
                        }
                    }
                }
                if (subChannel.equals("ServerIP")) {
                    Server server1 = AdvanceProxy.getInstance().getServer(in.readUTF());
                    if (server1 != null) {
                        out.writeUTF("ServerIP");
                        out.writeUTF(server1.getName());
                        out.writeUTF(server1.getHostAddress());
                        out.writeShort(server1.getPort());
                    }
                }
                if (subChannel.equals("KickPlayer")) {
                    Player player = AdvanceProxy.getInstance().getPlayer(in.readUTF());

                    if (player != null) {
                        String kickReason = in.readUTF();
                        player.disconnect(new TextComponent(kickReason));
                    }
                }

                // Check we haven't set out to null, and we have written data, if so reply back back along the BungeeCord channel
                if (out != null) {
                    byte[] b = out.toByteArray();
                    if (b.length != 0) {
                        server.sendData("BungeeCord", b);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        throw new IllegalArgumentException();
    }

}
