package net.advanceteam.proxy.netty.protocol.codec.frame;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;

@ChannelHandler.Sharable
public class Varint21LengthFieldEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        ChannelPacketBuffer channelPacketBuffer = new ChannelPacketBuffer(byteBuf);

        int bodyLen = byteBuf.readableBytes();
        int headerLen = varintSize(bodyLen);

        byteBuf2.ensureWritable(headerLen + bodyLen);

        channelPacketBuffer.writeVarInt(bodyLen, byteBuf2);
        byteBuf2.writeBytes(byteBuf);
    }

    private static int varintSize(int paramInt) {
        if ((paramInt & 0xFFFFFF80) == 0) {
            return 1;
        }

        if ((paramInt & 0xFFFFC000) == 0) {
            return 2;
        }

        if ((paramInt & 0xFFE00000) == 0) {
            return 3;
        }

        if ((paramInt & 0xF0000000) == 0) {
            return 4;
        }

        return 5;
    }
}
