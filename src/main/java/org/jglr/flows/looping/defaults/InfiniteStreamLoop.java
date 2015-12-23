package org.jglr.flows.looping.defaults;

import org.jglr.flows.looping.StreamLoop;

public class InfiniteStreamLoop extends StreamLoop {
    public InfiniteStreamLoop() {
        super(0, Long.MAX_VALUE);
    }

    @Override
    public void onLoopStart() {}

    @Override
    public void onLoopEnd() {}

    @Override
    public boolean shouldContinue(long currentPosition) {
        return true;
    }

    @Override
    public boolean shouldSkipToEnd(long currentPosition) {
        return true;
    }
}
