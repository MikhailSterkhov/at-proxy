package net.advanceteam.proxy.netty.protocol.packet.impl.login;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.callback.Callback;
import net.advanceteam.proxy.common.utility.EncryptionUtil;
import net.advanceteam.proxy.common.utility.JsonUtil;
import net.advanceteam.proxy.connection.http.HttpClient;
import net.advanceteam.proxy.connection.login.LoginResult;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.codec.MinecraftPacketDecoder;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptionResponsePacket implements MinecraftPacket {

    private byte[] sharedSecret;
    private byte[] verifyToken;


    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeArray(sharedSecret);
        channelPacketBuffer.writeArray(verifyToken);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.sharedSecret = channelPacketBuffer.readArray();
        this.verifyToken = channelPacketBuffer.readArray();
    }

    @Override
    public void handle(Channel channel) {
        MinecraftPacketDecoder packetDecoder = channel.pipeline().get(MinecraftPacketDecoder.class);

        try {
            SecretKey sharedKey = EncryptionUtil.getSecret(this, packetDecoder.getLastEncryptionRequest());

            String encName = URLEncoder.encode(packetDecoder.getPlayerName(), "UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");

            for (byte[] bit : new byte[][]{packetDecoder.getLastEncryptionRequest().getServerId().getBytes("ISO_8859_1"), sharedKey.getEncoded(), EncryptionUtil.getKeys().getPublic().getEncoded()}) {
                sha.update(bit);
            }

            String encodedHash = URLEncoder.encode(new BigInteger(sha.digest()).toString(16), "UTF-8");

            String preventProxy = "&ip=" + URLEncoder.encode(AdvanceProxy.getInstance().getProxyHost(), "UTF-8");
            String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash + preventProxy;

            Callback<String> handler = (result, error) -> {

                if (error == null) {
                    LoginResult loginResult = JsonUtil.fromJson(result, LoginResult.class);

                    if (loginResult != null && loginResult.getId() != null) {
                        String playerName = loginResult.getName();
                        UUID uuid = UUID.fromString(loginResult.getId());

                        packetDecoder.getLastLoginRequest().finish(channel, uuid, playerName);
                        return;
                    }

                    packetDecoder.getLastLoginRequest().disconnect(channel, "offline player");
                } else {
                    packetDecoder.getLastLoginRequest().disconnect(channel, "§cНа сервер могут зайти только лицензированные игроки");
                }
            };

            HttpClient.connectToUrl(authURL, channel.eventLoop(), handler);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
