package net.advanceteam.proxy.netty.protocol.client.compress;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;

import java.util.List;
import java.util.zip.Inflater;

public class PacketDecompressor extends MessageToMessageDecoder<ByteBuf> {

    private final Inflater inflater = new Inflater();

    private final byte[] buffer = new byte[ 8192 ];


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int size = new ChannelPacketBuffer(in).readVarInt();

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

    public void process(ByteBuf in, ByteBuf out) throws Exception {
        byte[] inData = new byte[in.readableBytes()];
        in.readBytes(inData);

        inflater.setInput(inData);

        while (!inflater.finished() && inflater.getTotalIn() < inData.length) {
            int count = inflater.inflate(buffer);
            out.writeBytes(buffer, 0, count);
        }

        inflater.reset();
    }
}
