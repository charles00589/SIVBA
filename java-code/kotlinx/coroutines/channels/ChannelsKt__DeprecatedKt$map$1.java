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
/* JADX INFO: Add missing generic type declarations: [R] */
/* compiled from: Deprecated.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u0004H\u008a@"}, d2 = {"<anonymous>", "", "E", "R", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 6, 0}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$map$1", f = "Deprecated.kt", i = {0, 0, 1, 1, 2, 2}, l = {487, 333, 333}, m = "invokeSuspend", n = {"$this$produce", "$this$consume$iv$iv", "$this$produce", "$this$consume$iv$iv", "$this$produce", "$this$consume$iv$iv"}, s = {"L$0", "L$2", "L$0", "L$2", "L$0", "L$2"})
/* loaded from: classes.dex */
public final class ChannelsKt__DeprecatedKt$map$1<R> extends SuspendLambda implements Function2<ProducerScope<? super R>, Continuation<? super Unit>, Object> {
    final /* synthetic */ ReceiveChannel<E> $this_map;
    final /* synthetic */ Function2<E, Continuation<? super R>, Object> $transform;
    private /* synthetic */ Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    Object L$4;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public ChannelsKt__DeprecatedKt$map$1(ReceiveChannel<? extends E> receiveChannel, Function2<? super E, ? super Continuation<? super R>, ? extends Object> function2, Continuation<? super ChannelsKt__DeprecatedKt$map$1> continuation) {
        super(2, continuation);
        this.$this_map = receiveChannel;
        this.$transform = function2;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        ChannelsKt__DeprecatedKt$map$1 channelsKt__DeprecatedKt$map$1 = new ChannelsKt__DeprecatedKt$map$1(this.$this_map, this.$transform, continuation);
        channelsKt__DeprecatedKt$map$1.L$0 = obj;
        return channelsKt__DeprecatedKt$map$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(ProducerScope<? super R> producerScope, Continuation<? super Unit> continuation) {
        return ((ChannelsKt__DeprecatedKt$map$1) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0093 A[Catch: all -> 0x00d1, TRY_LEAVE, TryCatch #0 {all -> 0x00d1, blocks: (B:8:0x0022, B:10:0x0076, B:15:0x008b, B:17:0x0093, B:34:0x00c9, B:45:0x005e, B:48:0x006e), top: B:2:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00c2 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00c3  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00c9 A[Catch: all -> 0x00d1, TRY_ENTER, TRY_LEAVE, TryCatch #0 {all -> 0x00d1, blocks: (B:8:0x0022, B:10:0x0076, B:15:0x008b, B:17:0x0093, B:34:0x00c9, B:45:0x005e, B:48:0x006e), top: B:2:0x000a }] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:24:0x00c3 -> B:10:0x0076). Please report as a decompilation issue!!! */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Object invokeSuspend(Object obj) {
        ReceiveChannel receiveChannel;
        ProducerScope producerScope;
        Throwable th;
        Function2 function2;
        ChannelIterator it;
        ReceiveChannel receiveChannel2;
        Throwable th2;
        ProducerScope producerScope2;
        Function2 function22;
        ChannelIterator channelIterator;
        ProducerScope producerScope3;
        Throwable th3;
        ProducerScope producerScope4;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        try {
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                producerScope = (ProducerScope) this.L$0;
                receiveChannel = this.$this_map;
                th = null;
                function2 = this.$transform;
                it = receiveChannel.iterator();
            } else if (i == 1) {
                it = (ChannelIterator) this.L$3;
                receiveChannel = (ReceiveChannel) this.L$2;
                function2 = (Function2) this.L$1;
                producerScope4 = (ProducerScope) this.L$0;
                ResultKt.throwOnFailure(obj);
                th3 = null;
                if (!((Boolean) obj).booleanValue()) {
                    Object next = it.next();
                    this.L$0 = producerScope4;
                    this.L$1 = function2;
                    this.L$2 = receiveChannel;
                    this.L$3 = it;
                    this.L$4 = producerScope4;
                    this.label = 2;
                    obj = function2.invoke(next, this);
                    if (obj == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    th2 = th3;
                    producerScope2 = producerScope4;
                    function22 = function2;
                    receiveChannel2 = receiveChannel;
                    channelIterator = it;
                    producerScope3 = producerScope2;
                    this.L$0 = producerScope2;
                    this.L$1 = function22;
                    this.L$2 = receiveChannel2;
                    this.L$3 = channelIterator;
                    this.L$4 = null;
                    this.label = 3;
                    if (producerScope3.send(obj, this) != coroutine_suspended) {
                    }
                } else {
                    Unit unit = Unit.INSTANCE;
                    ChannelsKt.cancelConsumed(receiveChannel, th3);
                    return Unit.INSTANCE;
                }
            } else if (i == 2) {
                producerScope3 = (ProducerScope) this.L$4;
                channelIterator = (ChannelIterator) this.L$3;
                receiveChannel2 = (ReceiveChannel) this.L$2;
                function22 = (Function2) this.L$1;
                producerScope2 = (ProducerScope) this.L$0;
                try {
                    ResultKt.throwOnFailure(obj);
                    th2 = null;
                    this.L$0 = producerScope2;
                    this.L$1 = function22;
                    this.L$2 = receiveChannel2;
                    this.L$3 = channelIterator;
                    this.L$4 = null;
                    this.label = 3;
                    if (producerScope3.send(obj, this) != coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    it = channelIterator;
                    receiveChannel = receiveChannel2;
                    function2 = function22;
                    producerScope = producerScope2;
                    th = th2;
                } catch (Throwable th4) {
                    th = th4;
                    receiveChannel = receiveChannel2;
                    try {
                        throw th;
                    } catch (Throwable th5) {
                        ChannelsKt.cancelConsumed(receiveChannel, th);
                        throw th5;
                    }
                }
            } else {
                if (i != 3) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                it = (ChannelIterator) this.L$3;
                receiveChannel = (ReceiveChannel) this.L$2;
                function2 = (Function2) this.L$1;
                ProducerScope producerScope5 = (ProducerScope) this.L$0;
                ResultKt.throwOnFailure(obj);
                producerScope = producerScope5;
                th = null;
            }
            this.L$0 = producerScope;
            this.L$1 = function2;
            this.L$2 = receiveChannel;
            this.L$3 = it;
            this.label = 1;
            Object hasNext = it.hasNext(this);
            if (hasNext == coroutine_suspended) {
                return coroutine_suspended;
            }
            Throwable th6 = th;
            producerScope4 = producerScope;
            obj = hasNext;
            th3 = th6;
            if (!((Boolean) obj).booleanValue()) {
            }
        } catch (Throwable th7) {
            th = th7;
        }
    }
}
