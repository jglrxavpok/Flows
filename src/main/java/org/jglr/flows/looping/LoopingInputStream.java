package org.jglr.flows.looping;

import org.jglr.flows.MarkableFileInputStream;
import org.jglr.flows.looping.defaults.DoStreamLoop;
import org.jglr.flows.looping.defaults.InfiniteStreamLoop;

import java.io.*;
import java.util.Objects;

/**
 * A <code>LoopingInputStream</code> provides methods to loop through a specific part of an InputStream. Could be used
 * for music/sound loops.<br/>
 * If the underlying stream does not support marking and resetting, <code>LoopingInputStream</code> will attempt to provide
 * workarounds:
 * <dl>
 *     <dt>FileInputStreams</dt>
 *     <dd>As they do not support marking, they are wrapped with an instance of {@link MarkableFileInputStream}</dd>
 * </ul>
 */
public class LoopingInputStream extends FilterInputStream {

    private final StreamLoop mainLoop;
    private StreamLoop currentLoop;
    private long currentPosition;
    private long loopStart;

    /**
     * Creates a new instance of LoopingInputStream. Calls {@link #LoopingInputStream(InputStream, boolean)} with
     * <code>bufferingAccepted</code> set to <code>true</code>.
     * @param in
     *          The input stream to loop.
     *          Cannot be <code>null</code>.
     */
    public LoopingInputStream(InputStream in) {
        this(in, true);
    }

    /**
     * Creates a new instance of LoopingInputStream.
     * @param in
     *          The input stream to loop. Cannot be <code>null</code>. Should preferably support marking though
     *          workarounds can be provided by {@link LoopingInputStream}
     * @param bufferingAccepted
     *          Indicates if it is acceptable to wrap the stream with a {@link java.io.BufferedInputStream BufferedInputStream}
     *          in order to help marking. Defaults to <code>true</code>. Set it to <code>false</code> if you don't want
     *          the {@link java.io.BufferedInputStream BufferedInputStream} instance to create too many buffers.
     */
    public LoopingInputStream(InputStream in, boolean bufferingAccepted) {
        super(Objects.requireNonNull(in, "in"));
        if(!in.markSupported()) {
            if(in instanceof FileInputStream) {
                this.in = new MarkableFileInputStream((FileInputStream) in);
            } else if(bufferingAccepted) {
                this.in = new BufferedInputStream(in);
            } else {
                throw new IllegalArgumentException("The provided input stream did not support marking and no workaround " +
                        "was possible. (buffering was said unacceptable)");
            }
        }
        mainLoop = currentLoop = new DoStreamLoop(0, Long.MAX_VALUE);
    }

    @Override
    public synchronized int read() throws IOException {
        if(currentLoop.shouldContinue(currentPosition)) {
            if(currentLoop.getStartPosition() == currentPosition) {
                mark(Integer.MAX_VALUE);
                currentLoop.onLoopStart();
                loopStart = currentPosition;
            }
            currentPosition++;
            if(currentLoop.getEndPosition() == currentPosition) {
                reset();
                currentPosition = loopStart;
                currentLoop.onLoopEnd();
            }
        } else {
            if(currentLoop.shouldSkipToEnd(currentPosition)) {
                return -1;
            } else {
                currentPosition++;
                if(currentLoop.getEndPosition() == currentPosition || currentLoop.getEndPosition() == Long.MAX_VALUE) {
                    currentLoop = mainLoop;
                }
            }
        }
        int result = in.read();
        if(result == -1) {
            if(currentLoop.continueOnEOFReached(currentPosition, this)) {
                reset();
                currentLoop.onLoopEnd();
                currentPosition = loopStart;
                return read();
            }
        }
        return result;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte)c;
            }
        } catch (IOException ee) {
        }
        return i;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public StreamLoop getCurrentLoop() {
        return currentLoop;
    }

    public void setCurrentLoop(StreamLoop loop) {
        this.currentLoop = loop;
    }
}
