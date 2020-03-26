package net.advanceteam.proxy.netty.protocol.client.packet.game;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@AllArgsConstructor
@ClientPacketHandler(packetQuery = "LOGIN_SUCCESS_PACKET")
public class LoginPacket implements ClientPacket {

    private int entityId;
    private int viewDistance;
    private int dimension;

    private long seed;

    private short gameMode;
    private short difficulty;
    private short maxPlayers;

    private boolean reducedDebugInfo;
    private boolean normalRespawn;

    private String levelType;


    public LoginPacket() {
        registerClientPacket(ClientVersion.V1_8, 0x01);
        registerClientPacket(ClientVersion.V1_9, 0x23);
        registerClientPacket(ClientVersion.V1_9_1, 0x23);
        registerClientPacket(ClientVersion.V1_9_2, 0x23);
        registerClientPacket(ClientVersion.V1_9_3, 0x23);
        registerClientPacket(ClientVersion.V1_9_4, 0x23);
        registerClientPacket(ClientVersion.V1_10, 0x23);
        registerClientPacket(ClientVersion.V1_11, 0x23);
        registerClientPacket(ClientVersion.V1_11_1, 0x23);
        registerClientPacket(ClientVersion.V1_11_2, 0x23);
        registerClientPacket(ClientVersion.V1_12, 0x23);
        registerClientPacket(ClientVersion.V1_12_1, 0x23);
        registerClientPacket(ClientVersion.V1_12_2, 0x23);
        registerClientPacket(ClientVersion.V1_13, 0x25);
        registerClientPacket(ClientVersion.V1_13_1, 0x25);
        registerClientPacket(ClientVersion.V1_13_2, 0x25);
        registerClientPacket(ClientVersion.V1_14, 0x25);
        registerClientPacket(ClientVersion.V1_14_1, 0x25);
        registerClientPacket(ClientVersion.V1_14_2, 0x25);
        registerClientPacket(ClientVersion.V1_14_3, 0x25);
        registerClientPacket(ClientVersion.V1_14_4, 0x25);
        registerClientPacket(ClientVersion.V1_15, 0x26);
        registerClientPacket(ClientVersion.V1_15_1, 0x26);
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeInt(entityId);
        channelPacketBuffer.writeByte(gameMode);

        if (clientVersion > ClientVersion.V1_9.getVersion()) {
            channelPacketBuffer.writeInt(dimension);
        } else {
            channelPacketBuffer.writeByte(dimension);
        }

        if (clientVersion >= ClientVersion.V1_15.getVersion()) {
            channelPacketBuffer.writeLong(seed);
        }

        if (clientVersion < ClientVersion.V1_14.getVersion()) {
            channelPacketBuffer.writeByte(difficulty);
        }

        channelPacketBuffer.writeByte(maxPlayers);
        channelPacketBuffer.writeString(levelType);

        if (clientVersion >= ClientVersion.V1_14.getVersion()) {
            channelPacketBuffer.writeVarInt(viewDistance);
        }

        if (clientVersion >= 29) {
            channelPacketBuffer.writeBoolean(reducedDebugInfo);
        }

        if (clientVersion >= ClientVersion.V1_15.getVersion()) {
            channelPacketBuffer.writeBoolean(normalRespawn);
        }
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.entityId = channelPacketBuffer.readInt();
        this.gameMode = channelPacketBuffer.readUnsignedByte();

        if (clientVersion > ClientVersion.V1_9.getVersion()) {
            dimension = channelPacketBuffer.readInt();
        } else {
            dimension = channelPacketBuffer.readByte();
        }

        if (clientVersion >= ClientVersion.V1_15.getVersion()) {
            seed = channelPacketBuffer.readLong();
        }

        if (clientVersion < ClientVersion.V1_14.getVersion()) {
            difficulty = channelPacketBuffer.readUnsignedByte();
        }

        maxPlayers = channelPacketBuffer.readUnsignedByte();
        levelType = channelPacketBuffer.readString();

        if (clientVersion >= ClientVersion.V1_14.getVersion()) {
            viewDistance = channelPacketBuffer.readVarInt();
        }

        if (clientVersion >= ClientVersion.V1_14.getVersion()) {
            reducedDebugInfo = channelPacketBuffer.readBoolean();
        }

        if (clientVersion >= ClientVersion.V1_15.getVersion()) {
            normalRespawn = channelPacketBuffer.readBoolean();
        }
    }

    @Override
    public void handle(Channel channel) { }
}
