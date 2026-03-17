package com.facebook.react.bridge.queue;

import com.facebook.react.devsupport.StackTraceHelper;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import kotlin.Metadata;

/* compiled from: MessageQueueThread.kt */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\"\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\b0\u0007\"\u0004\b\u0000\u0010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\b0\nH&J\b\u0010\u000b\u001a\u00020\u0003H&J\b\u0010\f\u001a\u00020\rH&J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH&J\b\u0010\u0010\u001a\u00020\rH&J\n\u0010\u0011\u001a\u0004\u0018\u00010\u0012H&J\b\u0010\u0013\u001a\u00020\rH&J\b\u0010\u0014\u001a\u00020\u0003H&ø\u0001\u0000\u0082\u0002\u0006\n\u0004\b!0\u0001¨\u0006\u0015À\u0006\u0001"}, d2 = {"Lcom/facebook/react/bridge/queue/MessageQueueThread;", "", "runOnQueue", "", "runnable", "Ljava/lang/Runnable;", "callOnQueue", "Ljava/util/concurrent/Future;", "T", "callable", "Ljava/util/concurrent/Callable;", "isOnThread", "assertIsOnThread", "", StackTraceHelper.MESSAGE_KEY, "", "quitSynchronous", "getPerfStats", "Lcom/facebook/react/bridge/queue/MessageQueueThreadPerfStats;", "resetPerfStats", "isIdle", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public interface MessageQueueThread {
    void assertIsOnThread();

    void assertIsOnThread(String message);

    <T> Future<T> callOnQueue(Callable<T> callable);

    MessageQueueThreadPerfStats getPerfStats();

    boolean isIdle();

    boolean isOnThread();

    void quitSynchronous();

    void resetPerfStats();

    boolean runOnQueue(Runnable runnable);
}
