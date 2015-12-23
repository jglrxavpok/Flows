package org.jglr.flows;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * A <code>MarkableFileInputStream</code> provides a way to mark and reset FileInputStreams thanks to a {@link FileChannel} instance.<br/>
 *
 * <a href="http://stackoverflow.com/questions/1094703/java-file-input-with-rewind-reset-capability">Stackoverflow question regarding
 * marking and resetting FileInputStreams</a>
 * @author ykaganovich
 */
public class MarkableFileInputStream extends FilterInputStream {
    private FileChannel fileChannel;
    private long mark = -1;

    public MarkableFileInputStream(FileInputStream fis) {
        super(fis);
        fileChannel = fis.getChannel();
    }

    public long getMark() {
        return mark;
    }

    public synchronized void seek(long position) throws IOException {
        fileChannel.position(position);
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            mark = fileChannel.position();
        } catch (IOException ex) {
            ex.printStackTrace();
            mark = -1;
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (mark == -1) {
            throw new IOException("Cannot reset if the stream is not yet marked");
        }
        fileChannel.position(mark);
    }
}