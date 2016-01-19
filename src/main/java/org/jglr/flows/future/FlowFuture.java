package org.jglr.flows.future;

import java.util.ArrayList;
import java.util.List;

public class FlowFuture<T> {

    private List<FlowProgressListener<T>> listeners;
    private boolean done;
    private T progress;

    public FlowFuture() {
        listeners = new ArrayList<>();
    }

    public void addListener(FlowProgressListener<T> listener) {
        listeners.add(listener);
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(T finalValue) {
        if(done) {
            throw new IllegalStateException("Cannot set an already done future to done.");
        }
        done = true;
        for(FlowProgressListener<T> l : listeners) {
            l.onProgress(this, finalValue);
            l.onDone(this, finalValue);
        }
        progress = finalValue;
    }

    public void progress(T value) {
        progress = value;
        for(FlowProgressListener<T> l : listeners) {
            l.onProgress(this, value);
        }
    }

    public T getProgress() {
        return progress;
    }
}
