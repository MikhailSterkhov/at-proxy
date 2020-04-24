package net.advanceteam.proxy.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.netty.exception.OverflowPacketException;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ChannelPacketBuffer extends ByteBuf {

    private final ByteBuf byteBuf;

    public void writeString(String string) {
        if (string.length() > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot send string longer than Short.MAX_VALUE (got %s characters)", string.length()));
        }

        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        writeVarInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public String readString() {
        int len = readVarInt();

        if (len > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        }

        byte[] bytes = new byte[len];

        byteBuf.readBytes(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeArray(byte[] bytes) {
        if (bytes.length > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot send byte array longer than Short.MAX_VALUE (got %s bytes)", bytes.length));
        }

        writeVarInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public byte[] toArray() {
        byte[] ret = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(ret);

        return ret;
    }

    public byte[] readArray() {
        return readArray(byteBuf.readableBytes());
    }

    public byte[] readArray(int limit) {
        int len = readVarInt();

        if (len > limit) {
            throw new OverflowPacketException(String.format("Cannot receive byte array longer than %s (got %s bytes)", limit, len));
        }

        byte[] ret = new byte[len];

        byteBuf.readBytes(ret);

        return ret;
    }

    public int[] readVarIntArray() {
        int len = readVarInt();

        int[] ret = new int[len];

        for (int i = 0; i < len; i++) {
            ret[i] = readVarInt();
        }

        return ret;
    }

    public void writeStringArray(List<String> list) {
        writeVarInt(list.size());

        for (String string : list) {
            writeString(string);
        }
    }

    public List<String> readStringArray() {
        int len = readVarInt();

        List<String> ret = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            ret.add(readString());
        }

        return ret;
    }

    public int readVarInt() {
        int out = 0;
        int bytes = 0;

        byte in;

        do {

            in = byteBuf.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

        } while ((in & 0x80) == 0x80);

        return out;
    }

    public int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;

        byte in;

        do {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new RuntimeException("VarInt too big");
            }

        } while ((in & 0x80) == 0x80);

        return out;
    }

    public void writeVarInt(int value) {
        int part;

        do {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            byteBuf.writeByte(part);

        } while (value != 0);
    }

    public void writeVarInt(int value, ByteBuf output) {
        int part;
        do {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

        } while (value != 0);
    }

    public int readVarShort() {
        int low = byteBuf.readUnsignedShort();

        int high = 0;

        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;

            high = byteBuf.readUnsignedByte();
        }

        return ((high & 0xFF) << 15) | low;
    }

    public void writeVarShort(int toWrite) {
        int low = toWrite & 0x7FFF;

        int high = (toWrite & 0x7F8000) >> 15;

        if (high != 0) {
            low = low | 0x8000;
        }
        byteBuf.writeShort(low);
        if (high != 0) {
            byteBuf.writeByte(high);
        }
    }

    public void writeUUID(UUID value) {
        byteBuf.writeLong(value.getMostSignificantBits());
        byteBuf.writeLong(value.getLeastSignificantBits());
    }

    public UUID readUUID() {
        return new UUID(byteBuf.readLong(), byteBuf.readLong());
    }


    @Override
    public int capacity() {
        return this.byteBuf.capacity();
    }

    @Override
    public ByteBuf capacity(int p_capacity_1_) {
        return this.byteBuf.capacity(p_capacity_1_);
    }

    @Override
    public int maxCapacity() {
        return this.byteBuf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.byteBuf.alloc();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ByteOrder order() {
        return this.byteBuf.order();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ByteBuf order(ByteOrder p_order_1_) {
        return this.byteBuf.order(p_order_1_);
    }

    @Override
    public ByteBuf unwrap() {
        return this.byteBuf.unwrap();
    }

    @Override
    public boolean isDirect() {
        return this.byteBuf.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return this.byteBuf.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.byteBuf.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return this.byteBuf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int p_readerIndex_1_) {
        return this.byteBuf.readerIndex(p_readerIndex_1_);
    }

    @Override
    public int writerIndex() {
        return this.byteBuf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int p_writerIndex_1_) {
        return this.byteBuf.writerIndex(p_writerIndex_1_);
    }

    @Override
    public ByteBuf setIndex(int p_setIndex_1_, int p_setIndex_2_) {
        return this.byteBuf.setIndex(p_setIndex_1_, p_setIndex_2_);
    }

    @Override
    public int readableBytes() {
        return this.byteBuf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.byteBuf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.byteBuf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.byteBuf.isReadable();
    }

    @Override
    public boolean isReadable(int p_isReadable_1_) {
        return this.byteBuf.isReadable(p_isReadable_1_);
    }

    @Override
    public boolean isWritable() {
        return this.byteBuf.isWritable();
    }

    @Override
    public boolean isWritable(int p_isWritable_1_) {
        return this.byteBuf.isWritable(p_isWritable_1_);
    }

    @Override
    public ByteBuf clear() {
        return this.byteBuf.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this.byteBuf.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this.byteBuf.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this.byteBuf.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this.byteBuf.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return this.byteBuf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return this.byteBuf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int p_ensureWritable_1_) {
        return this.byteBuf.ensureWritable(p_ensureWritable_1_);
    }

    @Override
    public int ensureWritable(int p_ensureWritable_1_, boolean p_ensureWritable_2_) {
        return this.byteBuf.ensureWritable(p_ensureWritable_1_, p_ensureWritable_2_);
    }

    @Override
    public boolean getBoolean(int p_getBoolean_1_) {
        return this.byteBuf.getBoolean(p_getBoolean_1_);
    }

    @Override
    public byte getByte(int p_getByte_1_) {
        return this.byteBuf.getByte(p_getByte_1_);
    }

    @Override
    public short getUnsignedByte(int p_getUnsignedByte_1_) {
        return this.byteBuf.getUnsignedByte(p_getUnsignedByte_1_);
    }

    @Override
    public short getShort(int p_getShort_1_) {
        return this.byteBuf.getShort(p_getShort_1_);
    }

    @Override
    public short getShortLE(int p_getShortLE_1_) {
        return this.byteBuf.getShortLE(p_getShortLE_1_);
    }

    @Override
    public int getUnsignedShort(int p_getUnsignedShort_1_) {
        return this.byteBuf.getUnsignedShort(p_getUnsignedShort_1_);
    }

    @Override
    public int getUnsignedShortLE(int p_getUnsignedShortLE_1_) {
        return this.byteBuf.getUnsignedShortLE(p_getUnsignedShortLE_1_);
    }

    @Override
    public int getMedium(int p_getMedium_1_) {
        return this.byteBuf.getMedium(p_getMedium_1_);
    }

    @Override
    public int getMediumLE(int p_getMediumLE_1_) {
        return this.byteBuf.getMediumLE(p_getMediumLE_1_);
    }

    @Override
    public int getUnsignedMedium(int p_getUnsignedMedium_1_) {
        return this.byteBuf.getUnsignedMedium(p_getUnsignedMedium_1_);
    }

    @Override
    public int getUnsignedMediumLE(int p_getUnsignedMediumLE_1_) {
        return this.byteBuf.getUnsignedMediumLE(p_getUnsignedMediumLE_1_);
    }

    @Override
    public int getInt(int p_getInt_1_) {
        return this.byteBuf.getInt(p_getInt_1_);
    }

    @Override
    public int getIntLE(int p_getIntLE_1_) {
        return this.byteBuf.getIntLE(p_getIntLE_1_);
    }

    @Override
    public long getUnsignedInt(int p_getUnsignedInt_1_) {
        return this.byteBuf.getUnsignedInt(p_getUnsignedInt_1_);
    }

    @Override
    public long getUnsignedIntLE(int p_getUnsignedIntLE_1_) {
        return this.byteBuf.getUnsignedIntLE(p_getUnsignedIntLE_1_);
    }

    @Override
    public long getLong(int p_getLong_1_) {
        return this.byteBuf.getLong(p_getLong_1_);
    }

    @Override
    public long getLongLE(int p_getLongLE_1_) {
        return this.byteBuf.getLongLE(p_getLongLE_1_);
    }

    @Override
    public char getChar(int p_getChar_1_) {
        return this.byteBuf.getChar(p_getChar_1_);
    }

    @Override
    public float getFloat(int p_getFloat_1_) {
        return this.byteBuf.getFloat(p_getFloat_1_);
    }

    @Override
    public double getDouble(int p_getDouble_1_) {
        return this.byteBuf.getDouble(p_getDouble_1_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_) {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_) {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_) {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuffer p_getBytes_2_) {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, OutputStream p_getBytes_2_, int p_getBytes_3_) throws IOException {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }

    @Override
    public int getBytes(int p_getBytes_1_, GatheringByteChannel p_getBytes_2_, int p_getBytes_3_) throws IOException {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }

    @Override
    public int getBytes(int p_getBytes_1_, FileChannel p_getBytes_2_, long p_getBytes_3_, int p_getBytes_5_) throws IOException {
        return this.byteBuf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_5_);
    }

    @Override
    public CharSequence getCharSequence(int p_getCharSequence_1_, int p_getCharSequence_2_, Charset p_getCharSequence_3_) {
        return this.byteBuf.getCharSequence(p_getCharSequence_1_, p_getCharSequence_2_, p_getCharSequence_3_);
    }

    @Override
    public ByteBuf setBoolean(int p_setBoolean_1_, boolean p_setBoolean_2_) {
        return this.byteBuf.setBoolean(p_setBoolean_1_, p_setBoolean_2_);
    }

    @Override
    public ByteBuf setByte(int p_setByte_1_, int p_setByte_2_) {
        return this.byteBuf.setByte(p_setByte_1_, p_setByte_2_);
    }

    @Override
    public ByteBuf setShort(int p_setShort_1_, int p_setShort_2_) {
        return this.byteBuf.setShort(p_setShort_1_, p_setShort_2_);
    }

    @Override
    public ByteBuf setShortLE(int p_setShortLE_1_, int p_setShortLE_2_) {
        return this.byteBuf.setShortLE(p_setShortLE_1_, p_setShortLE_2_);
    }

    @Override
    public ByteBuf setMedium(int p_setMedium_1_, int p_setMedium_2_) {
        return this.byteBuf.setMedium(p_setMedium_1_, p_setMedium_2_);
    }

    @Override
    public ByteBuf setMediumLE(int p_setMediumLE_1_, int p_setMediumLE_2_) {
        return this.byteBuf.setMediumLE(p_setMediumLE_1_, p_setMediumLE_2_);
    }

    @Override
    public ByteBuf setInt(int p_setInt_1_, int p_setInt_2_) {
        return this.byteBuf.setInt(p_setInt_1_, p_setInt_2_);
    }

    @Override
    public ByteBuf setIntLE(int p_setIntLE_1_, int p_setIntLE_2_) {
        return this.byteBuf.setIntLE(p_setIntLE_1_, p_setIntLE_2_);
    }

    @Override
    public ByteBuf setLong(int p_setLong_1_, long p_setLong_2_) {
        return this.byteBuf.setLong(p_setLong_1_, p_setLong_2_);
    }

    @Override
    public ByteBuf setLongLE(int p_setLongLE_1_, long p_setLongLE_2_) {
        return this.byteBuf.setLongLE(p_setLongLE_1_, p_setLongLE_2_);
    }

    @Override
    public ByteBuf setChar(int p_setChar_1_, int p_setChar_2_) {
        return this.byteBuf.setChar(p_setChar_1_, p_setChar_2_);
    }

    @Override
    public ByteBuf setFloat(int p_setFloat_1_, float p_setFloat_2_) {
        return this.byteBuf.setFloat(p_setFloat_1_, p_setFloat_2_);
    }

    @Override
    public ByteBuf setDouble(int p_setDouble_1_, double p_setDouble_2_) {
        return this.byteBuf.setDouble(p_setDouble_1_, p_setDouble_2_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_) {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_) {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_) {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuffer p_setBytes_2_) {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_);
    }

    @Override
    public int setBytes(int p_setBytes_1_, InputStream p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    @Override
    public int setBytes(int p_setBytes_1_, ScatteringByteChannel p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    @Override
    public int setBytes(int p_setBytes_1_, FileChannel p_setBytes_2_, long p_setBytes_3_, int p_setBytes_5_) throws IOException {
        return this.byteBuf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_5_);
    }

    @Override
    public ByteBuf setZero(int p_setZero_1_, int p_setZero_2_) {
        return this.byteBuf.setZero(p_setZero_1_, p_setZero_2_);
    }

    @Override
    public int setCharSequence(int p_setCharSequence_1_, CharSequence p_setCharSequence_2_, Charset p_setCharSequence_3_) {
        return this.byteBuf.setCharSequence(p_setCharSequence_1_, p_setCharSequence_2_, p_setCharSequence_3_);
    }

    @Override
    public boolean readBoolean() {
        return this.byteBuf.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.byteBuf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.byteBuf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.byteBuf.readShort();
    }

    @Override
    public short readShortLE() {
        return this.byteBuf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.byteBuf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.byteBuf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.byteBuf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.byteBuf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.byteBuf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.byteBuf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.byteBuf.readInt();
    }

    @Override
    public int readIntLE() {
        return this.byteBuf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.byteBuf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.byteBuf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.byteBuf.readLong();
    }

    @Override
    public long readLongLE() {
        return this.byteBuf.readLongLE();
    }

    @Override
    public char readChar() {
        return this.byteBuf.readChar();
    }

    @Override
    public float readFloat() {
        return this.byteBuf.readFloat();
    }

    @Override
    public double readDouble() {
        return this.byteBuf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int p_readBytes_1_) {
        return this.byteBuf.readBytes(p_readBytes_1_);
    }

    @Override
    public ByteBuf readSlice(int p_readSlice_1_) {
        return this.byteBuf.readSlice(p_readSlice_1_);
    }

    @Override
    public ByteBuf readRetainedSlice(int p_readRetainedSlice_1_) {
        return this.byteBuf.readRetainedSlice(p_readRetainedSlice_1_);
    }

    @Override
    public ByteBuf readBytes(ByteBuf p_readBytes_1_) {
        return this.byteBuf.readBytes(p_readBytes_1_);
    }

    @Override
    public ByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_) {
        return this.byteBuf.readBytes(p_readBytes_1_, p_readBytes_2_);
    }

    @Override
    public ByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        return this.byteBuf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
    }

    @Override
    public ByteBuf readBytes(byte[] p_readBytes_1_) {
        return this.byteBuf.readBytes(p_readBytes_1_);
    }

    @Override
    public ByteBuf readBytes(byte[] p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        return this.byteBuf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer p_readBytes_1_) {
        return this.byteBuf.readBytes(p_readBytes_1_);
    }

    @Override
    public ByteBuf readBytes(OutputStream p_readBytes_1_, int p_readBytes_2_) throws IOException {
        return this.byteBuf.readBytes(p_readBytes_1_, p_readBytes_2_);
    }

    @Override
    public int readBytes(GatheringByteChannel p_readBytes_1_, int p_readBytes_2_) throws IOException {
        return this.byteBuf.readBytes(p_readBytes_1_, p_readBytes_2_);
    }

    @Override
    public CharSequence readCharSequence(int p_readCharSequence_1_, Charset p_readCharSequence_2_) {
        return this.byteBuf.readCharSequence(p_readCharSequence_1_, p_readCharSequence_2_);
    }

    @Override
    public int readBytes(FileChannel p_readBytes_1_, long p_readBytes_2_, int p_readBytes_4_) throws IOException {
        return this.byteBuf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_4_);
    }

    @Override
    public ByteBuf skipBytes(int p_skipBytes_1_) {
        return this.byteBuf.skipBytes(p_skipBytes_1_);
    }

    @Override
    public ByteBuf writeBoolean(boolean p_writeBoolean_1_) {
        return this.byteBuf.writeBoolean(p_writeBoolean_1_);
    }

    @Override
    public ByteBuf writeByte(int p_writeByte_1_) {
        return this.byteBuf.writeByte(p_writeByte_1_);
    }

    @Override
    public ByteBuf writeShort(int p_writeShort_1_) {
        return this.byteBuf.writeShort(p_writeShort_1_);
    }

    @Override
    public ByteBuf writeShortLE(int p_writeShortLE_1_) {
        return this.byteBuf.writeShortLE(p_writeShortLE_1_);
    }

    @Override
    public ByteBuf writeMedium(int p_writeMedium_1_) {
        return this.byteBuf.writeMedium(p_writeMedium_1_);
    }

    @Override
    public ByteBuf writeMediumLE(int p_writeMediumLE_1_) {
        return this.byteBuf.writeMediumLE(p_writeMediumLE_1_);
    }

    @Override
    public ByteBuf writeInt(int p_writeInt_1_) {
        return this.byteBuf.writeInt(p_writeInt_1_);
    }

    @Override
    public ByteBuf writeIntLE(int p_writeIntLE_1_) {
        return this.byteBuf.writeIntLE(p_writeIntLE_1_);
    }

    @Override
    public ByteBuf writeLong(long p_writeLong_1_) {
        return this.byteBuf.writeLong(p_writeLong_1_);
    }

    @Override
    public ByteBuf writeLongLE(long p_writeLongLE_1_) {
        return this.byteBuf.writeLongLE(p_writeLongLE_1_);
    }

    @Override
    public ByteBuf writeChar(int p_writeChar_1_) {
        return this.byteBuf.writeChar(p_writeChar_1_);
    }

    @Override
    public ByteBuf writeFloat(float p_writeFloat_1_) {
        return this.byteBuf.writeFloat(p_writeFloat_1_);
    }

    @Override
    public ByteBuf writeDouble(double p_writeDouble_1_) {
        return this.byteBuf.writeDouble(p_writeDouble_1_);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_) {
        return this.byteBuf.writeBytes(p_writeBytes_1_);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_) {
        return this.byteBuf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        return this.byteBuf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
    }

    @Override
    public ByteBuf writeBytes(byte[] p_writeBytes_1_) {
        return this.byteBuf.writeBytes(p_writeBytes_1_);
    }

    @Override
    public ByteBuf writeBytes(byte[] p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        return this.byteBuf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer p_writeBytes_1_) {
        return this.byteBuf.writeBytes(p_writeBytes_1_);
    }

    @Override
    public int writeBytes(InputStream p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.byteBuf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    @Override
    public int writeBytes(ScatteringByteChannel p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.byteBuf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    @Override
    public int writeBytes(FileChannel p_writeBytes_1_, long p_writeBytes_2_, int p_writeBytes_4_) throws IOException {
        return this.byteBuf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_4_);
    }

    @Override
    public ByteBuf writeZero(int p_writeZero_1_) {
        return this.byteBuf.writeZero(p_writeZero_1_);
    }

    @Override
    public int writeCharSequence(CharSequence p_writeCharSequence_1_, Charset p_writeCharSequence_2_) {
        return this.byteBuf.writeCharSequence(p_writeCharSequence_1_, p_writeCharSequence_2_);
    }

    @Override
    public int indexOf(int p_indexOf_1_, int p_indexOf_2_, byte p_indexOf_3_) {
        return this.byteBuf.indexOf(p_indexOf_1_, p_indexOf_2_, p_indexOf_3_);
    }

    @Override
    public int bytesBefore(byte p_bytesBefore_1_) {
        return this.byteBuf.bytesBefore(p_bytesBefore_1_);
    }

    @Override
    public int bytesBefore(int p_bytesBefore_1_, byte p_bytesBefore_2_) {
        return this.byteBuf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_);
    }

    @Override
    public int bytesBefore(int p_bytesBefore_1_, int p_bytesBefore_2_, byte p_bytesBefore_3_) {
        return this.byteBuf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_, p_bytesBefore_3_);
    }

    @Override
    public int forEachByte(ByteProcessor p_forEachByte_1_) {
        return this.byteBuf.forEachByte(p_forEachByte_1_);
    }

    @Override
    public int forEachByte(int p_forEachByte_1_, int p_forEachByte_2_, ByteProcessor p_forEachByte_3_) {
        return this.byteBuf.forEachByte(p_forEachByte_1_, p_forEachByte_2_, p_forEachByte_3_);
    }

    @Override
    public int forEachByteDesc(ByteProcessor p_forEachByteDesc_1_) {
        return this.byteBuf.forEachByteDesc(p_forEachByteDesc_1_);
    }

    @Override
    public int forEachByteDesc(int p_forEachByteDesc_1_, int p_forEachByteDesc_2_, ByteProcessor p_forEachByteDesc_3_) {
        return this.byteBuf.forEachByteDesc(p_forEachByteDesc_1_, p_forEachByteDesc_2_, p_forEachByteDesc_3_);
    }

    @Override
    public ByteBuf copy() {
        return this.byteBuf.copy();
    }

    @Override
    public ByteBuf copy(int p_copy_1_, int p_copy_2_) {
        return this.byteBuf.copy(p_copy_1_, p_copy_2_);
    }

    @Override
    public ByteBuf slice() {
        return this.byteBuf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.byteBuf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int p_slice_1_, int p_slice_2_) {
        return this.byteBuf.slice(p_slice_1_, p_slice_2_);
    }

    @Override
    public ByteBuf retainedSlice(int p_retainedSlice_1_, int p_retainedSlice_2_) {
        return this.byteBuf.retainedSlice(p_retainedSlice_1_, p_retainedSlice_2_);
    }

    @Override
    public ByteBuf duplicate() {
        return this.byteBuf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.byteBuf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return this.byteBuf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.byteBuf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int p_nioBuffer_1_, int p_nioBuffer_2_) {
        return this.byteBuf.nioBuffer(p_nioBuffer_1_, p_nioBuffer_2_);
    }

    @Override
    public ByteBuffer internalNioBuffer(int p_internalNioBuffer_1_, int p_internalNioBuffer_2_) {
        return this.byteBuf.internalNioBuffer(p_internalNioBuffer_1_, p_internalNioBuffer_2_);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.byteBuf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int p_nioBuffers_1_, int p_nioBuffers_2_) {
        return this.byteBuf.nioBuffers(p_nioBuffers_1_, p_nioBuffers_2_);
    }

    @Override
    public boolean hasArray() {
        return this.byteBuf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.byteBuf.array();
    }

    @Override
    public int arrayOffset() {
        return this.byteBuf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.byteBuf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.byteBuf.memoryAddress();
    }

    @Override
    public String toString(Charset p_toString_1_) {
        return this.byteBuf.toString(p_toString_1_);
    }

    @Override
    public String toString(int p_toString_1_, int p_toString_2_, Charset p_toString_3_) {
        return this.byteBuf.toString(p_toString_1_, p_toString_2_, p_toString_3_);
    }

    @Override
    public int hashCode() {
        return this.byteBuf.hashCode();
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return this.byteBuf.equals(p_equals_1_);
    }

    @Override
    public int compareTo(ByteBuf p_compareTo_1_) {
        return this.byteBuf.compareTo(p_compareTo_1_);
    }

    @Override
    public String toString() {
        return this.byteBuf.toString();
    }

    @Override
    public ByteBuf retain(int p_retain_1_) {
        return this.byteBuf.retain(p_retain_1_);
    }

    @Override
    public ByteBuf retain() {
        return this.byteBuf.retain();
    }

    @Override
    public ByteBuf touch() {
        return this.byteBuf.touch();
    }

    @Override
    public ByteBuf touch(Object p_touch_1_) {
        return this.byteBuf.touch(p_touch_1_);
    }

    @Override
    public int refCnt() {
        return this.byteBuf.refCnt();
    }

    @Override
    public boolean release() {
        return this.byteBuf.release();
    }

    @Override
    public boolean release(int p_release_1_) {
        return this.byteBuf.release(p_release_1_);
    }
}
