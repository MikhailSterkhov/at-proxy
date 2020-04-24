package net.advanceteam.proxy.netty.protocol.packet.impl.game;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.version.MinecraftVersion;

@AllArgsConstructor
@NoArgsConstructor
public class LoginPacket implements MinecraftPacket {

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


    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeInt(entityId);
        channelPacketBuffer.writeByte(gameMode);

        if (clientVersion > MinecraftVersion.V1_9.getVersionId()) {
            channelPacketBuffer.writeInt(dimension);
        } else {
            channelPacketBuffer.writeByte(dimension);
        }

        if (clientVersion >= MinecraftVersion.V1_15.getVersionId()) {
            channelPacketBuffer.writeLong(seed);
        }

        if (clientVersion < MinecraftVersion.V1_14.getVersionId()) {
            channelPacketBuffer.writeByte(difficulty);
        }

        channelPacketBuffer.writeByte(maxPlayers);
        channelPacketBuffer.writeString(levelType);

        if (clientVersion >= MinecraftVersion.V1_14.getVersionId()) {
            channelPacketBuffer.writeVarInt(viewDistance);
        }

        if (clientVersion >= 29) {
            channelPacketBuffer.writeBoolean(reducedDebugInfo);
        }

        if (clientVersion >= MinecraftVersion.V1_15.getVersionId()) {
            channelPacketBuffer.writeBoolean(normalRespawn);
        }
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.entityId = channelPacketBuffer.readInt();
        this.gameMode = channelPacketBuffer.readUnsignedByte();

        if (clientVersion > MinecraftVersion.V1_9.getVersionId()) {
            dimension = channelPacketBuffer.readInt();
        } else {
            dimension = channelPacketBuffer.readByte();
        }

        if (clientVersion >= MinecraftVersion.V1_15.getVersionId()) {
            seed = channelPacketBuffer.readLong();
        }

        if (clientVersion < MinecraftVersion.V1_14.getVersionId()) {
            difficulty = channelPacketBuffer.readUnsignedByte();
        }

        maxPlayers = channelPacketBuffer.readUnsignedByte();
        levelType = channelPacketBuffer.readString();

        if (clientVersion >= MinecraftVersion.V1_14.getVersionId()) {
            viewDistance = channelPacketBuffer.readVarInt();
        }

        if (clientVersion >= MinecraftVersion.V1_14.getVersionId()) {
            reducedDebugInfo = channelPacketBuffer.readBoolean();
        }

        if (clientVersion >= MinecraftVersion.V1_15.getVersionId()) {
            normalRespawn = channelPacketBuffer.readBoolean();
        }
    }

    @Override
    public void handle(Channel channel) { }
}
