package com.facebook.react.bridge.queue;

import android.os.Looper;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ReactQueueConfigurationImpl.kt */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB!\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\b\u001a\u00020\tH\u0016J\b\u0010\n\u001a\u00020\tH\u0016J\b\u0010\u000b\u001a\u00020\tH\u0016J\b\u0010\f\u001a\u00020\rH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lcom/facebook/react/bridge/queue/ReactQueueConfigurationImpl;", "Lcom/facebook/react/bridge/queue/ReactQueueConfiguration;", "uiQueueThread", "Lcom/facebook/react/bridge/queue/MessageQueueThreadImpl;", "nativeModulesQueueThread", "jsQueueThread", "<init>", "(Lcom/facebook/react/bridge/queue/MessageQueueThreadImpl;Lcom/facebook/react/bridge/queue/MessageQueueThreadImpl;Lcom/facebook/react/bridge/queue/MessageQueueThreadImpl;)V", "getUIQueueThread", "Lcom/facebook/react/bridge/queue/MessageQueueThread;", "getNativeModulesQueueThread", "getJSQueueThread", "destroy", "", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ReactQueueConfigurationImpl implements ReactQueueConfiguration {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final MessageQueueThreadImpl jsQueueThread;
    private final MessageQueueThreadImpl nativeModulesQueueThread;
    private final MessageQueueThreadImpl uiQueueThread;

    public /* synthetic */ ReactQueueConfigurationImpl(MessageQueueThreadImpl messageQueueThreadImpl, MessageQueueThreadImpl messageQueueThreadImpl2, MessageQueueThreadImpl messageQueueThreadImpl3, DefaultConstructorMarker defaultConstructorMarker) {
        this(messageQueueThreadImpl, messageQueueThreadImpl2, messageQueueThreadImpl3);
    }

    @JvmStatic
    public static final ReactQueueConfigurationImpl create(ReactQueueConfigurationSpec reactQueueConfigurationSpec, QueueThreadExceptionHandler queueThreadExceptionHandler) {
        return INSTANCE.create(reactQueueConfigurationSpec, queueThreadExceptionHandler);
    }

    private ReactQueueConfigurationImpl(MessageQueueThreadImpl messageQueueThreadImpl, MessageQueueThreadImpl messageQueueThreadImpl2, MessageQueueThreadImpl messageQueueThreadImpl3) {
        this.uiQueueThread = messageQueueThreadImpl;
        this.nativeModulesQueueThread = messageQueueThreadImpl2;
        this.jsQueueThread = messageQueueThreadImpl3;
    }

    @Override // com.facebook.react.bridge.queue.ReactQueueConfiguration
    public MessageQueueThread getUIQueueThread() {
        return this.uiQueueThread;
    }

    @Override // com.facebook.react.bridge.queue.ReactQueueConfiguration
    public MessageQueueThread getNativeModulesQueueThread() {
        return this.nativeModulesQueueThread;
    }

    @Override // com.facebook.react.bridge.queue.ReactQueueConfiguration
    public MessageQueueThread getJSQueueThread() {
        return this.jsQueueThread;
    }

    @Override // com.facebook.react.bridge.queue.ReactQueueConfiguration
    public void destroy() {
        if (!Intrinsics.areEqual(this.nativeModulesQueueThread.getLooper(), Looper.getMainLooper())) {
            this.nativeModulesQueueThread.quitSynchronous();
        }
        if (Intrinsics.areEqual(this.jsQueueThread.getLooper(), Looper.getMainLooper())) {
            return;
        }
        this.jsQueueThread.quitSynchronous();
    }

    /* compiled from: ReactQueueConfigurationImpl.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0007¨\u0006\n"}, d2 = {"Lcom/facebook/react/bridge/queue/ReactQueueConfigurationImpl$Companion;", "", "<init>", "()V", "create", "Lcom/facebook/react/bridge/queue/ReactQueueConfigurationImpl;", "spec", "Lcom/facebook/react/bridge/queue/ReactQueueConfigurationSpec;", "exceptionHandler", "Lcom/facebook/react/bridge/queue/QueueThreadExceptionHandler;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final ReactQueueConfigurationImpl create(ReactQueueConfigurationSpec spec, QueueThreadExceptionHandler exceptionHandler) {
            Intrinsics.checkNotNullParameter(spec, "spec");
            Intrinsics.checkNotNullParameter(exceptionHandler, "exceptionHandler");
            return new ReactQueueConfigurationImpl(MessageQueueThreadImpl.INSTANCE.create(MessageQueueThreadSpec.INSTANCE.mainThreadSpec(), exceptionHandler), MessageQueueThreadImpl.INSTANCE.create(spec.getNativeModulesQueueThreadSpec(), exceptionHandler), MessageQueueThreadImpl.INSTANCE.create(spec.getJSQueueThreadSpec(), exceptionHandler), null);
        }
    }
}
