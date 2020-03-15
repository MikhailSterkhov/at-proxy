package net.advanceteam.proxy.netty.protocol.client.packet.game;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@AllArgsConstructor
@Getter
@Setter
@ClientPacketHandler(packetQuery = "GAME")
public class TitlePacket implements ClientPacket {

    private Action action;

    private String text;

    private int fadeIn;
    private int stay;
    private int fadeOut;


    public TitlePacket() {
        registerClientPacket(ClientVersion.V1_8, 0x45);
        registerClientPacket(ClientVersion.V1_9, 0x45);
        registerClientPacket(ClientVersion.V1_9_1, 0x45);
        registerClientPacket(ClientVersion.V1_9_2, 0x45);
        registerClientPacket(ClientVersion.V1_9_3, 0x45);
        registerClientPacket(ClientVersion.V1_9_4, 0x45);
        registerClientPacket(ClientVersion.V1_10, 0x45);
        registerClientPacket(ClientVersion.V1_11, 0x45);
        registerClientPacket(ClientVersion.V1_11_1, 0x45);
        registerClientPacket(ClientVersion.V1_11_2, 0x45);
        registerClientPacket(ClientVersion.V1_12, 0x47);
        registerClientPacket(ClientVersion.V1_12_1, 0x48);
        registerClientPacket(ClientVersion.V1_12_2, 0x48);
        registerClientPacket(ClientVersion.V1_13, 0x4B);
        registerClientPacket(ClientVersion.V1_13_1, 0x4B);
        registerClientPacket(ClientVersion.V1_13_2, 0x4B);
        registerClientPacket(ClientVersion.V1_14, 0x4F);
        registerClientPacket(ClientVersion.V1_14_1, 0x4F);
        registerClientPacket(ClientVersion.V1_14_2, 0x4F);
        registerClientPacket(ClientVersion.V1_14_3, 0x4F);
        registerClientPacket(ClientVersion.V1_14_4, 0x4F);
        registerClientPacket(ClientVersion.V1_15, 0x50);
        registerClientPacket(ClientVersion.V1_15_1, 0x50);
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        int index = action.ordinal();

        if (clientVersion <= ClientVersion.V1_10.getVersion() && index >= 2) {
            index--;
        }

        channelPacketBuffer.writeVarInt(index);

        switch (action) {

            case TITLE:
            case SUBTITLE:
            case ACTION_BAR:
                channelPacketBuffer.writeString(text);
                break;

            case TIMES:
                channelPacketBuffer.writeInt(fadeIn);
                channelPacketBuffer.writeInt(stay);
                channelPacketBuffer.writeInt(fadeOut);
                break;
        }
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        int index = channelPacketBuffer.readVarInt();

        // If we're working on 1.10 or lower, increment the value of the index so we pull out the correct value.
        if (clientVersion <= ClientVersion.V1_10.getVersion() && index >= 2) {
            index++;
        }

        action = Action.values()[index];

        switch (action) {

            case TITLE:
            case SUBTITLE:
            case ACTION_BAR:
                text = channelPacketBuffer.readString();
                break;

            case TIMES:
                fadeIn = channelPacketBuffer.readInt();
                stay = channelPacketBuffer.readInt();
                fadeOut = channelPacketBuffer.readInt();
                break;
        }
    }

    @Override
    public void handle(Channel channel) { }

    public enum Action {

        TITLE, SUBTITLE, ACTION_BAR, TIMES, CLEAR, RESET
    }

}
