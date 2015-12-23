package org.jglr.flows.looping.defaults;

import org.jglr.flows.looping.StreamLoop;

import java.util.Objects;
import java.util.function.Predicate;

public class WhileStreamLoop extends StreamLoop {
    private final Predicate<Long> condition;
    private final boolean skipToEnd;

    public WhileStreamLoop(long startPos, long endPos, Predicate<Long> condition) {
        this(startPos, endPos, condition, true);
    }

    public WhileStreamLoop(long startPos, long endPos, Predicate<Long> condition, boolean skipToEnd) {
        super(startPos, endPos);
        this.skipToEnd = skipToEnd;
        this.condition = Objects.requireNonNull(condition, "condition should not be null");
    }

    @Override
    public void onLoopStart() {

    }

    @Override
    public void onLoopEnd() {

    }

    @Override
    public boolean shouldContinue(long currentPosition) {
        return condition.test(currentPosition);
    }

    @Override
    public boolean shouldSkipToEnd(long currentPosition) {
        return skipToEnd;
    }
}
