package org.jglr.flows.looping;

public abstract class StreamLoop {

    private final long startPos;
    private final long endPos;

    public StreamLoop(long startPos, long endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public long getStartPosition() {
        return startPos;
    }

    public long getEndPosition() {
        return endPos;
    }

    public abstract void onLoopStart();

    public abstract void onLoopEnd();

    public abstract boolean shouldContinue(long currentPosition);

    public abstract boolean shouldSkipToEnd(long currentPosition);

    public boolean continueOnEOFReached(long currentPosition, LoopingInputStream in) {
        return shouldContinue(currentPosition);
    }
}
