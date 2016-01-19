package org.jglr.flows.future;

public abstract class FlowProgressListener<T> {

    public abstract void onProgress(FlowFuture<T> future, T progressValue);

    public void onDone(FlowFuture<T> future, T finalValue) {
        onProgress(future, finalValue);
    }
}
