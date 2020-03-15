package net.advanceteam.proxy.netty.protocol.client.codec.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;

import java.util.List;

public class Varint21FrameDecoder extends ByteToMessageDecoder {

    private static boolean DIRECT_WARNING;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        byteBuf.markReaderIndex();
        ChannelPacketBuffer channelPacketBuffer = new ChannelPacketBuffer(byteBuf);

        final byte[] buf = new byte[3];

        for (int i = 0; i < buf.length; i++) {

            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();

                return;
            }

            buf[i] = byteBuf.readByte();

            if (buf[i] >= 0) {

                int length = channelPacketBuffer.readVarInt(Unpooled.wrappedBuffer(buf));

                if (length == 0) {
                    throw new CorruptedFrameException("Empty Packet!");
                }

                if (byteBuf.readableBytes() < length) {
                    byteBuf.resetReaderIndex();

                    return;

                } else {

                    if (byteBuf.hasMemoryAddress()) {

                        list.add(byteBuf.slice(byteBuf.readerIndex(), length).retain());

                        byteBuf.skipBytes(length);

                    } else {

                        if (!DIRECT_WARNING) {
                            DIRECT_WARNING = true;

                            System.out.println("Netty is not using direct IO buffers.");
                        }

                        ByteBuf dst = channelHandlerContext.alloc().directBuffer(length);

                        byteBuf.readBytes(dst);

                        list.add(dst);
                    }
                    return;
                }
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
