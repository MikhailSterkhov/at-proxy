package net.advanceteam.proxy.netty.protocol.codec.compress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;

import java.util.zip.Deflater;

public class PacketCompressor extends MessageToByteEncoder<ByteBuf> {

    private final Deflater deflater = new Deflater();

    private final byte[] buffer = new byte[8192];

    @Setter
    private int threshold = 256;


    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        ChannelPacketBuffer channelPacketBuffer = new ChannelPacketBuffer(out);
        int origSize = msg.readableBytes();

        if (origSize < threshold) {
            channelPacketBuffer.writeVarInt(0);
            channelPacketBuffer.writeBytes(msg);
        } else {
            channelPacketBuffer.writeVarInt(origSize);

            process(msg, out);
        }
    }

    public void process(ByteBuf in, ByteBuf out) {
        byte[] inData = new byte[in.readableBytes()];
        in.readBytes(inData);

        deflater.setInput(inData);
        deflater.finish();

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            out.writeBytes(buffer, 0, count);
        }

        deflater.reset();
    }
}
