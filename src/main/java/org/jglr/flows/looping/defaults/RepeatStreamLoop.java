package org.jglr.flows.looping.defaults;

import org.jglr.flows.looping.LoopingInputStream;
import org.jglr.flows.looping.StreamLoop;

public class RepeatStreamLoop extends StreamLoop {
    private final int count;
    private final boolean skipToEnd;
    private int iterationIndex;
    private boolean stop;

    public RepeatStreamLoop(long startPos, long endPos, int count) {
        this(startPos, endPos, count, true);
    }

    public RepeatStreamLoop(long startPos, long endPos, int count, boolean skipToEnd) {
        super(startPos, endPos);
        this.count = count;
        this.skipToEnd = skipToEnd;
    }

    @Override
    public void onLoopStart() {
        iterationIndex++;
    }

    @Override
    public void onLoopEnd() {
        if(iterationIndex >= count) {
            stop = true;
        }
    }

    @Override
    public boolean shouldContinue(long currentPosition) {
        return !stop;
    }

    @Override
    public boolean shouldSkipToEnd(long currentPosition) {
        return skipToEnd;
    }

}
