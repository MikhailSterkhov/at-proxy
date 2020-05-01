package net.advanceteam.proxy.netty.protocol.codec.compress;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.jni.zlib.ProxyZlib;

import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PacketDecompressor extends MessageToMessageDecoder<ByteBuf> {

    private boolean compress;

    private Deflater deflater;
    private Inflater inflater;

    private final byte[] buffer = new byte[ 8192 ];


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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ChannelPacketBuffer channelPacketBuffer = new ChannelPacketBuffer(in);
        int size = channelPacketBuffer.readVarInt();

        if (size == 0) {
            out.add(in.slice().retain());
            in.skipBytes(in.readableBytes());
        } else {
            ByteBuf decompressed = ctx.alloc().directBuffer();

            try {
                process(in, decompressed);
                Preconditions.checkState(decompressed.readableBytes() == size, "Decompressed packet size mismatch");

                out.add(decompressed);
                decompressed = null;
            } finally {
                if (decompressed != null) {
                    decompressed.release();
                }
            }
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
