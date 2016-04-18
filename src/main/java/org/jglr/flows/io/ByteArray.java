package org.jglr.flows.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a resizable and growable byte array.
 * Also supports changing the byte order and automatically handle other primitives than <code>byte</code>.
 */
public class ByteArray {

    private ByteOrder byteOrder;
    private int writeCursor;
    private int readCursor;
    private byte[] data;

    /**
     * Creates an empty ByteArray instance with a length equal to 0.
     */
    public ByteArray() {
        this(0);
    }

    /**
     * Creates an empty ByteArray instance with a given length.
     * @param length
     *          The length of the ByteArray
     */
    public ByteArray(int length) {
        data = new byte[length];
        byteOrder = ByteOrder.nativeOrder();
    }

    /**
     * Creates a ByteArray instance with the contents <b>equal</b> to the given data. <br/>
     *
     * <b>Note:</b> Any operation that does not change the size of the array will also act on the provided array. Use {@link #wrap(byte[])} to only copy the contents
     * @param array
     *          The array to use the contents of
     */
    public ByteArray(byte[] array) {
        data = array;
        byteOrder = ByteOrder.nativeOrder();
    }

    /**
     * Returns the current ByteOrder.
     * @return
     *      The current ByteOrder
     */
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    /**
     * Changes the current ByteOrder.<br/>
     * Note that changing it after writing a word (eg a long, an int, or an unsigned int) does not change the encoding of said word.
     * @param order
     *        The new ByteOrder
     */
    public void setByteOrder(ByteOrder order) {
        this.byteOrder = order;
    }

    /**
     * Sets the byte at the current write index and grows the array if <code>writeIndex+1 >= length()</code><br/>
     * Increments the write index by 1.
     * @param b
     *          The byte to set
     */
    public void put(byte b) {
        growIfNecessary(1);
        data[writeCursor++] = b;
    }

    /**
     * Write a single signed integer into the ByteArray, respecting the current byte order.<br/>
     * Increments the write index by 4.
     * @param i
     *          The integer to write
     */
    public void putInt(int i) {
        putArray(toBytes(i, 4));
    }

    /**
     * Write a single signed long into the ByteArray, respecting the current byte order.<br/>
     * Increments the write index by 8.
     * @param l
     *          The long to write
     */
    public void putLong(long l) {
        putArray(toBytes(l, 8));
    }

    /**
     * Write a single unsigned integer into the ByteArray, respecting the current byte order.<br/>
     * Increments the write index by 4.
     * @param i
     *          The unsigned integer to write
     */
    public void putUnsignedInt(long i) {
        putArray(toBytes(i, 4));
    }

    /**
     * Writes the content of the given array inside the ByteArray.<br/>
     * Increments the write index by <code>bytes.length</code>
     * @param bytes
     *          The bytes to write
     */
    public void putArray(byte[] bytes) {
        growIfNecessary(bytes.length);
        for (byte b : bytes) {
            put(b);
        }
    }

    /**
     * Used to convert an int or a long to a byte array, while respecting the byte order
     * @param value
     *          Value to convert
     * @param byteCount
     *          The number of bytes used to represent the value
     * @return
     *          A byte array containing the bytes converted from the value
     *
     * @see #getByteOrder()
     * @see #setByteOrder(ByteOrder)
     */
    private byte[] toBytes(long value, int byteCount) {
        byte[] bytes = new byte[byteCount];
        for (int i = 0; i < byteCount; i++) {
            if(byteOrder == ByteOrder.BIG_ENDIAN) {
                bytes[i] = (byte) (value >> (1 << (i*8)) & 0xFF);
            } else {
                bytes[i] = (byte) (value >> (1 << (8*(byteCount-1)-i*8)) & 0xFF);
            }
        }
        return bytes;
    }

    /**
     * Returns the byte to the current read index
     * @return
     *      The byte at the read index
     */
    public byte get() {
        if(readCursor >= length() || readCursor < 0)
            throw new IndexOutOfBoundsException("Size: "+length()+", index: "+readCursor);
        return data[readCursor++];
    }

    private void growIfNecessary(int count) {
        if(writeCursor + count >= length()) {
            byte[] oldData = data;
            data = new byte[writeCursor+count];
            System.arraycopy(oldData, 0, data, 0, oldData.length);
        }
    }

    /**
     * Returns the current length of this ByteArray
     * @return
     *          The current length
     */
    public int length() {
        return data.length;
    }

    /**
     * Returns the value of the read index
     * @return
     *          The value of the read index
     */
    public int getReadCursor() {
        return readCursor;
    }

    /**
     * Set the value of the read index
     * @param readCursor
     *          The value of the read index
     */
    public void setReadCursor(int readCursor) {
        this.readCursor = readCursor;
    }

    /**
     * Returns the value of the write index
     * @return
     *          The value of the write index
     */
    public int getWriteCursor() {
        return writeCursor;
    }

    /**
     * Set the value of the write index
     * @param writeCursor
     *          The value of the write index
     */
    public void setWriteCursor(int writeCursor) {
        this.writeCursor = writeCursor;
    }

    /**
     * Returns the byte array backing this ByteArray instance
     * @return
     *        The backing byte array
     */
    public byte[] backingArray() {
        return data;
    }
}
