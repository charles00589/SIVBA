package kotlinx.coroutines.channels;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: Add missing generic type declarations: [V] */
/* compiled from: Deprecated.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004*\b\u0012\u0004\u0012\u0002H\u00040\u0005H\u008a@"}, d2 = {"<anonymous>", "", "E", "R", "V", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 6, 0}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$zip$2", f = "Deprecated.kt", i = {0, 0, 0, 1, 1, 1, 1, 2, 2, 2}, l = {487, 469, 471}, m = "invokeSuspend", n = {"$this$produce", "otherIterator", "$this$consume$iv$iv", "$this$produce", "otherIterator", "$this$consume$iv$iv", "element1", "$this$produce", "otherIterator", "$this$consume$iv$iv"}, s = {"L$0", "L$1", "L$3", "L$0", "L$1", "L$3", "L$5", "L$0", "L$1", "L$3"})
/* loaded from: classes.dex */
public final class ChannelsKt__DeprecatedKt$zip$2<V> extends SuspendLambda implements Function2<ProducerScope<? super V>, Continuation<? super Unit>, Object> {
    final /* synthetic */ ReceiveChannel<R> $other;
    final /* synthetic */ ReceiveChannel<E> $this_zip;
    final /* synthetic */ Function2<E, R, V> $transform;
    private /* synthetic */ Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    Object L$4;
    Object L$5;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public ChannelsKt__DeprecatedKt$zip$2(ReceiveChannel<? extends R> receiveChannel, ReceiveChannel<? extends E> receiveChannel2, Function2<? super E, ? super R, ? extends V> function2, Continuation<? super ChannelsKt__DeprecatedKt$zip$2> continuation) {
        super(2, continuation);
        this.$other = receiveChannel;
        this.$this_zip = receiveChannel2;
        this.$transform = function2;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        ChannelsKt__DeprecatedKt$zip$2 channelsKt__DeprecatedKt$zip$2 = new ChannelsKt__DeprecatedKt$zip$2(this.$other, this.$this_zip, this.$transform, continuation);
        channelsKt__DeprecatedKt$zip$2.L$0 = obj;
        return channelsKt__DeprecatedKt$zip$2;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(ProducerScope<? super V> producerScope, Continuation<? super Unit> continuation) {
        return ((ChannelsKt__DeprecatedKt$zip$2) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0091, code lost:
    
        r14 = r1;
        r1 = r6;
        r6 = r8;
        r7 = r9;
        r8 = r10;
        r9 = r11;
     */
    /* JADX WARN: Removed duplicated region for block: B:17:0x00b6 A[Catch: all -> 0x0055, TRY_LEAVE, TryCatch #1 {all -> 0x0055, blocks: (B:15:0x00ae, B:17:0x00b6, B:38:0x0105, B:49:0x004a), top: B:48:0x004a }] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00de A[Catch: all -> 0x0102, TRY_LEAVE, TryCatch #0 {all -> 0x0102, blocks: (B:22:0x00d6, B:24:0x00de), top: B:21:0x00d6 }] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0105 A[Catch: all -> 0x0055, TRY_ENTER, TRY_LEAVE, TryCatch #1 {all -> 0x0055, blocks: (B:15:0x00ae, B:17:0x00b6, B:38:0x0105, B:49:0x004a), top: B:48:0x004a }] */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Object invokeSuspend(Object obj) {
        ReceiveChannel receiveChannel;
        Function2 function2;
        ProducerScope producerScope;
        Throwable th;
        ChannelIterator channelIterator;
        ChannelIterator it;
        ReceiveChannel receiveChannel2;
        ProducerScope producerScope2;
        ChannelIterator channelIterator2;
        Function2 function22;
        ReceiveChannel receiveChannel3;
        Object obj2;
        ChannelIterator channelIterator3;
        Throwable th2;
        ProducerScope producerScope3;
        ChannelIterator channelIterator4;
        Function2 function23;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        try {
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                ProducerScope producerScope4 = (ProducerScope) this.L$0;
                ChannelIterator it2 = this.$other.iterator();
                receiveChannel = this.$this_zip;
                function2 = this.$transform;
                producerScope = producerScope4;
                th = null;
                channelIterator = it2;
                it = receiveChannel.iterator();
            } else if (i == 1) {
                ChannelIterator channelIterator5 = (ChannelIterator) this.L$4;
                ReceiveChannel receiveChannel4 = (ReceiveChannel) this.L$3;
                Function2 function24 = (Function2) this.L$2;
                ChannelIterator channelIterator6 = (ChannelIterator) this.L$1;
                ProducerScope producerScope5 = (ProducerScope) this.L$0;
                ResultKt.throwOnFailure(obj);
                producerScope3 = producerScope5;
                channelIterator4 = channelIterator6;
                function23 = function24;
                receiveChannel2 = receiveChannel4;
                channelIterator3 = channelIterator5;
                th2 = null;
                if (!((Boolean) obj).booleanValue()) {
                    Object next = channelIterator3.next();
                    this.L$0 = producerScope3;
                    this.L$1 = channelIterator4;
                    this.L$2 = function23;
                    this.L$3 = receiveChannel2;
                    this.L$4 = channelIterator3;
                    this.L$5 = next;
                    this.label = 2;
                    Object hasNext = channelIterator4.hasNext(this);
                    if (hasNext == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    ReceiveChannel receiveChannel5 = receiveChannel2;
                    obj2 = next;
                    obj = hasNext;
                    producerScope2 = producerScope3;
                    channelIterator2 = channelIterator4;
                    function22 = function23;
                    receiveChannel3 = receiveChannel5;
                    if (((Boolean) obj).booleanValue()) {
                    }
                    th = th2;
                    it = channelIterator3;
                    receiveChannel = receiveChannel3;
                    function2 = function22;
                    channelIterator = channelIterator2;
                    producerScope = producerScope2;
                } else {
                    Unit unit = Unit.INSTANCE;
                    ChannelsKt.cancelConsumed(receiveChannel2, th2);
                    return Unit.INSTANCE;
                }
            } else if (i == 2) {
                Object obj3 = this.L$5;
                channelIterator3 = (ChannelIterator) this.L$4;
                receiveChannel2 = (ReceiveChannel) this.L$3;
                Function2 function25 = (Function2) this.L$2;
                ChannelIterator channelIterator7 = (ChannelIterator) this.L$1;
                ProducerScope producerScope6 = (ProducerScope) this.L$0;
                try {
                    ResultKt.throwOnFailure(obj);
                    producerScope2 = producerScope6;
                    channelIterator2 = channelIterator7;
                    function22 = function25;
                    receiveChannel3 = receiveChannel2;
                    obj2 = obj3;
                    th2 = null;
                    try {
                        if (((Boolean) obj).booleanValue()) {
                            Object invoke = function22.invoke(obj2, channelIterator2.next());
                            this.L$0 = producerScope2;
                            this.L$1 = channelIterator2;
                            this.L$2 = function22;
                            this.L$3 = receiveChannel3;
                            this.L$4 = channelIterator3;
                            this.L$5 = null;
                            this.label = 3;
                            if (producerScope2.send(invoke, this) == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                        }
                        th = th2;
                        it = channelIterator3;
                        receiveChannel = receiveChannel3;
                        function2 = function22;
                        channelIterator = channelIterator2;
                        producerScope = producerScope2;
                    } catch (Throwable th3) {
                        th = th3;
                        receiveChannel = receiveChannel3;
                        try {
                            throw th;
                        } catch (Throwable th4) {
                            ChannelsKt.cancelConsumed(receiveChannel, th);
                            throw th4;
                        }
                    }
                } catch (Throwable th5) {
                    th = th5;
                    receiveChannel = receiveChannel2;
                    throw th;
                }
            } else {
                if (i != 3) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                it = (ChannelIterator) this.L$4;
                receiveChannel = (ReceiveChannel) this.L$3;
                function2 = (Function2) this.L$2;
                channelIterator = (ChannelIterator) this.L$1;
                producerScope = (ProducerScope) this.L$0;
                ResultKt.throwOnFailure(obj);
                th = null;
            }
            this.L$0 = producerScope;
            this.L$1 = channelIterator;
            this.L$2 = function2;
            this.L$3 = receiveChannel;
            this.L$4 = it;
            this.L$5 = null;
            this.label = 1;
            Object hasNext2 = it.hasNext(this);
            if (hasNext2 == coroutine_suspended) {
                return coroutine_suspended;
            }
            ChannelIterator channelIterator8 = it;
            th2 = th;
            obj = hasNext2;
            producerScope3 = producerScope;
            channelIterator4 = channelIterator;
            function23 = function2;
            receiveChannel2 = receiveChannel;
            channelIterator3 = channelIterator8;
            if (!((Boolean) obj).booleanValue()) {
            }
        } catch (Throwable th6) {
            th = th6;
        }
    }
}
