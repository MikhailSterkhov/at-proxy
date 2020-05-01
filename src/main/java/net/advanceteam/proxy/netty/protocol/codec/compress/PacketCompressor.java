package net.advanceteam.proxy.netty.protocol.codec.compress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.jni.zlib.ProxyZlib;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PacketCompressor extends MessageToByteEncoder<ByteBuf> {

    private boolean compress;

    private Deflater deflater;
    private Inflater inflater;

    private final byte[] buffer = new byte[ 8192 ];


    @Setter
    private int threshold = 256;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.compress = true;
        free();

        if (compress) {
            deflater = new Deflater(-1);
        } else {
            inflater = new Inflater();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        free();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        ChannelPacketBuffer channelPacketBuffer = new ChannelPacketBuffer(msg);

        int origSize = msg.readableBytes();

        if (origSize < threshold) {
            channelPacketBuffer.writeVarInt(0, out);
            out.writeBytes(msg);
        } else {
            channelPacketBuffer.writeVarInt(origSize, out);

            process(msg, out);
        }
    }

    private void process(ByteBuf in, ByteBuf out) throws DataFormatException {
        byte[] inData = new byte[in.readableBytes()];
        in.readBytes(inData);

        if (compress) {
            deflater.setInput(inData);
            deflater.finish();

            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                out.writeBytes(buffer, 0, count);
            }

            deflater.reset();
        } else {
            inflater.setInput(inData);

            while (!inflater.finished() && inflater.getTotalIn() < inData.length) {
                int count = inflater.inflate(buffer);
                out.writeBytes(buffer, 0, count);
            }

            inflater.reset();
        }
    }

    private void free() {
        if (deflater != null) {
            deflater.end();
        }
        if (inflater != null) {
            inflater.end();
        }
    }
}
