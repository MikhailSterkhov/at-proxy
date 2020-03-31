package net.advanceteam.proxy.connection.server.request;

import lombok.Builder;
import lombok.Getter;
import net.advanceteam.proxy.common.callback.Callback;
import net.advanceteam.proxy.common.event.impl.ServerConnectEvent;
import net.advanceteam.proxy.connection.server.Server;

import java.net.InetSocketAddress;

@Getter
@Builder(builderClassName = "Builder")
public class ServerConnectRequest {


    private final Server target;

    private final ServerConnectEvent.Reason reason;

    private final Callback<Result> callback;

    private final InetSocketAddress address;


    /**
     * Результат подключения игрока к серверу
     */
    public enum Result {

        /**
         * Ивент отменил подключение
         */
        EVENT_CANCEL, ALREADY_CONNECTED,

        /**
         * Игрок уже подключен к
         * данному серверу
         */
        ALREADY_CONNECTING,

        /**
         * Успешное подключение к
         * серверу {@param target}
         */
        SUCCESS,

        /**
         * Подключение было провалено
         * из-за какой-то ошибки
         */
        FAIL
    }

}
