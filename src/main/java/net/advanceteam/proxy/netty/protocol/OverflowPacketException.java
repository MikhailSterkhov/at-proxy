package net.advanceteam.proxy.netty.protocol;

public class OverflowPacketException extends RuntimeException {

    public OverflowPacketException(String message) {
        super(message);
    }
}