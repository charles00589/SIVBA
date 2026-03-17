package com.facebook.imagepipeline.listener;

import com.facebook.common.logging.FLog;
import com.facebook.imagepipeline.producers.ProducerContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ForwardingRequestListener2.kt */
@Metadata(d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010!\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010%\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u0000 (2\u00020\u0001:\u0001(B\u001b\b\u0016\u0012\u0010\u0010\u0002\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0001\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005B!\b\u0016\u0012\u0016\u0010\u0002\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00010\u0006\"\u0004\u0018\u00010\u0001¢\u0006\u0004\b\u0004\u0010\u0007J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0001J%\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000f2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b0\u0011H\u0082\bJ\u0010\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u000fH\u0016J2\u0010\u0017\u001a\u00020\u000b2\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\u0010\u0016\u001a\u0004\u0018\u00010\u000f2\u0014\u0010\u0018\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u0019H\u0016J<\u0010\u001a\u001a\u00020\u000b2\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\u0010\u0016\u001a\u0004\u0018\u00010\u000f2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0014\u0010\u0018\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u0019H\u0016J2\u0010\u001d\u001a\u00020\u000b2\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\u0010\u0016\u001a\u0004\u0018\u00010\u000f2\u0014\u0010\u0018\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u0019H\u0016J \u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020\u000fH\u0016J \u0010 \u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u000f2\u0006\u0010!\u001a\u00020\"H\u0016J\u0010\u0010#\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010$\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u001cH\u0016J\u0010\u0010&\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0018\u0010'\u001a\u00020\"2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u000fH\u0016R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006)"}, d2 = {"Lcom/facebook/imagepipeline/listener/ForwardingRequestListener2;", "Lcom/facebook/imagepipeline/listener/RequestListener2;", "listenersToAdd", "", "<init>", "(Ljava/util/Set;)V", "", "([Lcom/facebook/imagepipeline/listener/RequestListener2;)V", "requestListeners", "", "addRequestListener", "", "requestListener", "forEachListener", "methodName", "", "block", "Lkotlin/Function1;", "onRequestStart", "producerContext", "Lcom/facebook/imagepipeline/producers/ProducerContext;", "onProducerStart", "producerName", "onProducerFinishWithSuccess", "extraMap", "", "onProducerFinishWithFailure", "t", "", "onProducerFinishWithCancellation", "onProducerEvent", "producerEventName", "onUltimateProducerReached", "successful", "", "onRequestSuccess", "onRequestFailure", "throwable", "onRequestCancellation", "requiresExtraMap", "Companion", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ForwardingRequestListener2 implements RequestListener2 {
    private static final String TAG = "ForwardingRequestListener2";
    private final List<RequestListener2> requestListeners;

    public ForwardingRequestListener2(Set<? extends RequestListener2> set) {
        if (set == null) {
            this.requestListeners = new ArrayList();
            return;
        }
        ArrayList arrayList = new ArrayList(set.size());
        this.requestListeners = arrayList;
        CollectionsKt.filterNotNullTo(set, arrayList);
    }

    public ForwardingRequestListener2(RequestListener2... listenersToAdd) {
        Intrinsics.checkNotNullParameter(listenersToAdd, "listenersToAdd");
        ArrayList arrayList = new ArrayList(listenersToAdd.length);
        this.requestListeners = arrayList;
        ArraysKt.filterNotNullTo(listenersToAdd, arrayList);
    }

    public final void addRequestListener(RequestListener2 requestListener) {
        Intrinsics.checkNotNullParameter(requestListener, "requestListener");
        this.requestListeners.add(requestListener);
    }

    private final void forEachListener(String methodName, Function1<? super RequestListener2, Unit> block) {
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                block.invoke((RequestListener2) it.next());
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in " + methodName, e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public boolean requiresExtraMap(ProducerContext producerContext, String producerName) {
        Intrinsics.checkNotNullParameter(producerContext, "producerContext");
        Intrinsics.checkNotNullParameter(producerName, "producerName");
        List<RequestListener2> list = this.requestListeners;
        if ((list instanceof Collection) && list.isEmpty()) {
            return false;
        }
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            if (((RequestListener2) it.next()).requiresExtraMap(producerContext, producerName)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.facebook.imagepipeline.listener.RequestListener2
    public void onRequestStart(ProducerContext producerContext) {
        Intrinsics.checkNotNullParameter(producerContext, "producerContext");
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onRequestStart(producerContext);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onRequestStart", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerStart(ProducerContext producerContext, String producerName) {
        Intrinsics.checkNotNullParameter(producerContext, "producerContext");
        Intrinsics.checkNotNullParameter(producerName, "producerName");
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onProducerStart(producerContext, producerName);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onProducerStart", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerFinishWithSuccess(ProducerContext producerContext, String producerName, Map<String, String> extraMap) {
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onProducerFinishWithSuccess(producerContext, producerName, extraMap);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onProducerFinishWithSuccess", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerFinishWithFailure(ProducerContext producerContext, String producerName, Throwable t, Map<String, String> extraMap) {
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onProducerFinishWithFailure(producerContext, producerName, t, extraMap);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onProducerFinishWithFailure", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerFinishWithCancellation(ProducerContext producerContext, String producerName, Map<String, String> extraMap) {
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onProducerFinishWithCancellation(producerContext, producerName, extraMap);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onProducerFinishWithCancellation", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerEvent(ProducerContext producerContext, String producerName, String producerEventName) {
        Intrinsics.checkNotNullParameter(producerContext, "producerContext");
        Intrinsics.checkNotNullParameter(producerName, "producerName");
        Intrinsics.checkNotNullParameter(producerEventName, "producerEventName");
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onProducerEvent(producerContext, producerName, producerEventName);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onIntermediateChunkStart", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onUltimateProducerReached(ProducerContext producerContext, String producerName, boolean successful) {
        Intrinsics.checkNotNullParameter(producerContext, "producerContext");
        Intrinsics.checkNotNullParameter(producerName, "producerName");
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onUltimateProducerReached(producerContext, producerName, successful);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onProducerFinishWithSuccess", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.listener.RequestListener2
    public void onRequestSuccess(ProducerContext producerContext) {
        Intrinsics.checkNotNullParameter(producerContext, "producerContext");
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onRequestSuccess(producerContext);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onRequestSuccess", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.listener.RequestListener2
    public void onRequestFailure(ProducerContext producerContext, Throwable throwable) {
        Intrinsics.checkNotNullParameter(producerContext, "producerContext");
        Intrinsics.checkNotNullParameter(throwable, "throwable");
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onRequestFailure(producerContext, throwable);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onRequestFailure", e);
            }
        }
    }

    @Override // com.facebook.imagepipeline.listener.RequestListener2
    public void onRequestCancellation(ProducerContext producerContext) {
        Intrinsics.checkNotNullParameter(producerContext, "producerContext");
        Iterator<T> it = this.requestListeners.iterator();
        while (it.hasNext()) {
            try {
                ((RequestListener2) it.next()).onRequestCancellation(producerContext);
            } catch (Exception e) {
                FLog.e(TAG, "InternalListener exception in onRequestCancellation", e);
            }
        }
    }
}
