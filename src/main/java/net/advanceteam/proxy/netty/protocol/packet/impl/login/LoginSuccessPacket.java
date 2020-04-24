package net.advanceteam.proxy.netty.protocol.packet.impl.login;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.status.ProtocolStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessPacket implements MinecraftPacket {

    private String playerUuid;
    private String playerName;

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString(playerUuid);
        channelPacketBuffer.writeString(playerName);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.playerUuid = channelPacketBuffer.readString();
        this.playerName = channelPacketBuffer.readString();
    }

    @Override
    public void handle(Channel channel) {
        AdvanceProxy.getInstance().getMinecraftPacketManager().setProtocolStatus(channel, ProtocolStatus.GAME);
    }
}
