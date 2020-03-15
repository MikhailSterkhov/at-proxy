package net.advanceteam.proxy.netty.protocol.client.packet.game;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)

@ClientPacketHandler(packetQuery = "GAME")
public class PluginMessagePacket implements ClientPacket {

    public static final java.util.function.Function<String, @Nullable String> MODERNISE = new Function<String, String>() {
        @Override
        public String apply(String tag) {
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
        }
    };

    public static final java.util.function.Predicate<PluginMessagePacket> SHOULD_RELAY = (Predicate<PluginMessagePacket>)
            input -> (input.getTag().equals("REGISTER") || input.getTag().equals("minecraft:register")
                    || input.getTag().equals("MC|Brand") || input.getTag().equals("minecraft:brand"))

                    && input.getData().length < Byte.MAX_VALUE;

    private String tag;
    private byte[] data;

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
        //DataInput in = pluginMessage.getStream();
        //PluginMessageEvent event = new PluginMessageEvent(server, con, pluginMessage.getTag(), pluginMessage.getData().clone());
        //
        //if (bungee.getPluginManager().callEvent(event).isCancelled()) {
        //    throw CancelSendSignal.INSTANCE;
        //}
        //
        //if (pluginMessage.getTag().equals(con.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_13 ? "minecraft:brand" : "MC|Brand")) {
        //    ByteBuf brand = Unpooled.wrappedBuffer(pluginMessage.getData());
        //    String serverBrand = DefinedPacket.readString(brand);
        //    brand.release();
        //
        //    Preconditions.checkState(!serverBrand.contains(bungee.getName()), "Cannot connect proxy to itself!");
        //
        //    brand = ByteBufAllocator.DEFAULT.heapBuffer();
        //    DefinedPacket.writeString(bungee.getName() + " (" + bungee.getVersion() + ")" + " <- " + serverBrand, brand);
        //    pluginMessage.setData(DefinedPacket.toArray(brand));
        //    brand.release();
        //    // changes in the packet are ignored so we need to send it manually
        //    con.unsafe().sendPacket(pluginMessage);
        //    throw CancelSendSignal.INSTANCE;
        //}
        //
        //if (pluginMessage.getTag().equals("BungeeCord")) {
        //    ByteArrayDataOutput out = ByteStreams.newDataOutput();
        //    String subChannel = in.readUTF();
        //
        //    if (subChannel.equals("ForwardToPlayer")) {
        //        ProxiedPlayer target = bungee.getPlayer(in.readUTF());
        //        if (target != null) {
        //            // Read data from server
        //            String channel = in.readUTF();
        //            short len = in.readShort();
        //            byte[] data = new byte[len];
        //            in.readFully(data);
        //
        //            // Prepare new data to send
        //            out.writeUTF(channel);
        //            out.writeShort(data.length);
        //            out.write(data);
        //            byte[] payload = out.toByteArray();
        //
        //            target.getServer().sendData("BungeeCord", payload);
        //        }
        //
        //        // Null out stream, important as we don't want to send to ourselves
        //        out = null;
        //    }
        //    if (subChannel.equals("Forward")) {
        //        // Read data from server
        //        String target = in.readUTF();
        //        String channel = in.readUTF();
        //        short len = in.readShort();
        //        byte[] data = new byte[len];
        //        in.readFully(data);
        //
        //        // Prepare new data to send
        //        out.writeUTF(channel);
        //        out.writeShort(data.length);
        //        out.write(data);
        //        byte[] payload = out.toByteArray();
        //
        //        // Null out stream, important as we don't want to send to ourselves
        //        out = null;
        //
        //        if (target.equals("ALL")) {
        //            for (ServerInfo server : bungee.getServers().values()) {
        //                if (server != this.server.getInfo()) {
        //                    server.sendData("BungeeCord", payload);
        //                }
        //            }
        //        } else if (target.equals("ONLINE")) {
        //            for (ServerInfo server : bungee.getServers().values()) {
        //                if (server != this.server.getInfo()) {
        //                    server.sendData("BungeeCord", payload, false);
        //                }
        //            }
        //        } else {
        //            ServerInfo server = bungee.getServerInfo(target);
        //            if (server != null) {
        //                server.sendData("BungeeCord", payload);
        //            }
        //        }
        //    }
        //    if (subChannel.equals("Connect")) {
        //        ServerInfo server = bungee.getServerInfo(in.readUTF());
        //        if (server != null) {
        //            con.connect(server, ServerConnectEvent.Reason.PLUGIN_MESSAGE);
        //        }
        //    }
        //    if (subChannel.equals("ConnectOther")) {
        //        ProxiedPlayer player = bungee.getPlayer(in.readUTF());
        //        if (player != null) {
        //            ServerInfo server = bungee.getServerInfo(in.readUTF());
        //            if (server != null) {
        //                player.connect(server);
        //            }
        //        }
        //    }
        //    if (subChannel.equals("IP")) {
        //        out.writeUTF("IP");
        //        out.writeUTF(con.getAddress().getHostString());
        //        out.writeInt(con.getAddress().getPort());
        //    }
        //    if (subChannel.equals("PlayerCount")) {
        //        String target = in.readUTF();
        //        out.writeUTF("PlayerCount");
        //        if (target.equals("ALL")) {
        //            out.writeUTF("ALL");
        //            out.writeInt(bungee.getOnlineCount());
        //        } else {
        //            ServerInfo server = bungee.getServerInfo(target);
        //            if (server != null) {
        //                out.writeUTF(server.getName());
        //                out.writeInt(server.getPlayers().size());
        //            }
        //        }
        //    }
        //    if (subChannel.equals("PlayerList")) {
        //        String target = in.readUTF();
        //        out.writeUTF("PlayerList");
        //        if (target.equals("ALL")) {
        //            out.writeUTF("ALL");
        //            out.writeUTF(Util.csv(bungee.getPlayers()));
        //        } else {
        //            ServerInfo server = bungee.getServerInfo(target);
        //            if (server != null) {
        //                out.writeUTF(server.getName());
        //                out.writeUTF(Util.csv(server.getPlayers()));
        //            }
        //        }
        //    }
        //    if (subChannel.equals("GetServers")) {
        //        out.writeUTF("GetServers");
        //        out.writeUTF(Util.csv(bungee.getServers().keySet()));
        //    }
        //    if (subChannel.equals("Message")) {
        //        String target = in.readUTF();
        //        String message = in.readUTF();
        //        if (target.equals("ALL")) {
        //            for (ProxiedPlayer player : bungee.getPlayers()) {
        //                player.sendMessage(message);
        //            }
        //        } else {
        //            ProxiedPlayer player = bungee.getPlayer(target);
        //            if (player != null) {
        //                player.sendMessage(message);
        //            }
        //        }
        //    }
        //    if (subChannel.equals("GetServer")) {
        //        out.writeUTF("GetServer");
        //        out.writeUTF(server.getInfo().getName());
        //    }
        //    if (subChannel.equals("UUID")) {
        //        out.writeUTF("UUID");
        //        out.writeUTF(con.getUUID());
        //    }
        //    if (subChannel.equals("UUIDOther")) {
        //        ProxiedPlayer player = bungee.getPlayer(in.readUTF());
        //        if (player != null) {
        //            out.writeUTF("UUIDOther");
        //            out.writeUTF(player.getName());
        //            out.writeUTF(player.getUUID());
        //        }
        //    }
        //    if (subChannel.equals("ServerIP")) {
        //        ServerInfo info = bungee.getServerInfo(in.readUTF());
        //        if (info != null && !info.getAddress().isUnresolved()) {
        //            out.writeUTF("ServerIP");
        //            out.writeUTF(info.getName());
        //            out.writeUTF(info.getAddress().getAddress().getHostAddress());
        //            out.writeShort(info.getAddress().getPort());
        //        }
        //    }
        //    if (subChannel.equals("KickPlayer")) {
        //        ProxiedPlayer player = bungee.getPlayer(in.readUTF());
        //        if (player != null) {
        //            String kickReason = in.readUTF();
        //            player.disconnect(new TextComponent(kickReason));
        //        }
        //    }
        //
        //    // Check we haven't set out to null, and we have written data, if so reply back back along the BungeeCord channel
        //    if (out != null) {
        //        byte[] b = out.toByteArray();
        //        if (b.length != 0) {
        //            server.sendData("BungeeCord", b);
        //        }
        //    }
        //
        //    throw CancelSendSignal.INSTANCE;
        //}
    }
}
