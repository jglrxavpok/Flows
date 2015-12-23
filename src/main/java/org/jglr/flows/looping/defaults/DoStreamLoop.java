package org.jglr.flows.looping.defaults;

import org.jglr.flows.looping.LoopingInputStream;
import org.jglr.flows.looping.StreamLoop;

public class DoStreamLoop extends StreamLoop {
    public DoStreamLoop(long startPos, long endPos) {
        super(startPos, endPos);
    }

    @Override
    public void onLoopStart() {

    }

    @Override
    public void onLoopEnd() {

    }

    @Override
    public boolean shouldContinue(long currentPosition) {
        return true;
    }

    @Override
    public boolean shouldSkipToEnd(long currentPosition) {
        return true;
    }

    public boolean continueOnEOFReached(long currentPosition, LoopingInputStream in) {
        return false;
    }
}
