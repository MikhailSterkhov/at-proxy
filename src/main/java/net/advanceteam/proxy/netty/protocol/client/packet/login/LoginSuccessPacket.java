package net.advanceteam.proxy.netty.protocol.client.packet.login;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketDecoder;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketEncoder;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@AllArgsConstructor
@ClientPacketHandler(packetQuery = "LOGIN")
public class LoginSuccessPacket implements ClientPacket {

    private String playerUuid;
    private String playerName;

    public LoginSuccessPacket() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            registerClientPacket(clientVersion, 0x02);
        }
    }

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
        AdvanceProxy.getInstance().getClientPacketManager().setPacketType(channel, "GAME");
    }
}
