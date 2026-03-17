package com.facebook.imagepipeline.core;

import android.net.Uri;
import android.os.StrictMode;
import bolts.CancellationTokenSource;
import bolts.Continuation;
import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.callercontext.CallerContextVerifier;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Predicate;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSources;
import com.facebook.datasource.SimpleDataSource;
import com.facebook.fresco.urimod.UriModifier;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.datasource.CloseableProducerToDataSourceAdapter;
import com.facebook.imagepipeline.datasource.ProducerToDataSourceAdapter;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.ForwardingRequestListener;
import com.facebook.imagepipeline.listener.ForwardingRequestListener2;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestListener2;
import com.facebook.imagepipeline.producers.InternalRequestListener;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.SettableProducerContext;
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicLong;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;

/* compiled from: ImagePipeline.kt */
@Metadata(d1 = {"\u0000ê\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 u2\u00020\u0001:\u0001uB±\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\u0010\u0004\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005\u0012\u000e\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0005\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\r\u0012\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00110\r\u0012\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\n\u0012\u0006\u0010\u0014\u001a\u00020\u0015\u0012\u0006\u0010\u0016\u001a\u00020\u0017\u0012\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\b\u0010\u001a\u001a\u0004\u0018\u00010\u001b\u0012\u0006\u0010\u001c\u001a\u00020\u001d¢\u0006\u0004\b\u001e\u0010\u001fJ\u0006\u0010.\u001a\u00020/J4\u00100\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02010\n2\u0006\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u00106\u001a\u0004\u0018\u000107J>\u00100\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02010\n2\u0006\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u00106\u001a\u0004\u0018\u0001072\b\u0010\"\u001a\u0004\u0018\u00010\u0006JH\u00100\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02010\n2\u0006\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u00106\u001a\u0004\u0018\u0001072\b\u0010\"\u001a\u0004\u0018\u00010\u00062\b\u00108\u001a\u0004\u0018\u00010/J*\u00109\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001102010\n2\u0006\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u0001J$\u0010:\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02012\u0006\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u0001JJ\u0010;\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\n\b\u0002\u0010<\u001a\u0004\u0018\u0001072\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u00108\u001a\u0004\u0018\u00010/J&\u0010;\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u0001J.\u0010;\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\u0006\u0010\"\u001a\u00020\u0006J.\u0010;\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\u0006\u0010<\u001a\u000207JV\u0010;\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f02012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\u0006\u0010<\u001a\u0002072\b\u0010\"\u001a\u0004\u0018\u00010\u00062\b\u00108\u001a\u0004\u0018\u00010/2\u0012\u0010=\u001a\u000e\u0012\u0004\u0012\u00020/\u0012\u0002\b\u0003\u0018\u00010>J$\u0010?\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001102012\u0006\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u0001J.\u0010?\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001102012\u0006\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u0010\"\u001a\u0004\u0018\u00010\u0006J$\u0010@\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u0001H\u0007J.\u0010@\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u0010\"\u001a\u0004\u0018\u00010\u0006H\u0007J,\u0010B\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u0010\"\u001a\u0004\u0018\u00010\u0006J\"\u0010B\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u0001J*\u0010B\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\u0006\u0010C\u001a\u00020DJ6\u0010B\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\u0006\u0010C\u001a\u00020D2\b\u0010\"\u001a\u0004\u0018\u00010\u0006H\u0007J,\u0010E\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u0010\"\u001a\u0004\u0018\u00010\u0006J:\u0010E\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u00012\b\b\u0002\u0010C\u001a\u00020D2\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u0006H\u0007J\u000e\u0010F\u001a\u00020G2\u0006\u0010H\u001a\u00020IJ\u0010\u0010J\u001a\u00020G2\b\u0010H\u001a\u0004\u0018\u00010IJ\u0010\u0010J\u001a\u00020G2\b\u00103\u001a\u0004\u0018\u000104J\u000e\u0010K\u001a\u00020G2\u0006\u0010H\u001a\u00020IJ\u0006\u0010L\u001a\u00020GJ\u0006\u0010M\u001a\u00020GJ\u0006\u0010R\u001a\u00020GJ\u0010\u0010S\u001a\u00020\u000b2\b\u0010H\u001a\u0004\u0018\u00010IJ\u0010\u0010S\u001a\u00020\u000b2\b\u00103\u001a\u0004\u0018\u000104J\u0010\u0010T\u001a\u00020\u000b2\b\u0010H\u001a\u0004\u0018\u00010IJ\u0010\u0010T\u001a\u00020\u000b2\b\u00103\u001a\u0004\u0018\u000104J\u0010\u0010U\u001a\u00020\u000b2\b\u0010H\u001a\u0004\u0018\u00010IJ\u001a\u0010U\u001a\u00020\u000b2\b\u0010H\u001a\u0004\u0018\u00010I2\b\u0010V\u001a\u0004\u0018\u00010WJ\u0010\u0010X\u001a\u00020\u000b2\u0006\u00103\u001a\u000204H\u0002J\u000e\u0010U\u001a\u00020\u000b2\u0006\u00103\u001a\u000204J\u0016\u0010Y\u001a\b\u0012\u0004\u0012\u00020\u000b012\b\u0010H\u001a\u0004\u0018\u00010IJ<\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u000b0[2\b\u00103\u001a\u0004\u0018\u0001042\u0006\u0010\\\u001a\u00020\u000e2\u0012\u0010]\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020A0^2\u0006\u0010_\u001a\u00020`H\u0002J\u0016\u0010Y\u001a\b\u0012\u0004\u0012\u00020\u000b012\b\u00103\u001a\u0004\u0018\u000104J\u001c\u0010a\u001a\u0004\u0018\u00010\u000e2\b\u00103\u001a\u0004\u0018\u0001042\b\u00105\u001a\u0004\u0018\u00010\u0001J\u0018\u0010b\u001a\n\u0012\u0004\u0012\u00020\u000f\u0018\u0001022\b\u0010\\\u001a\u0004\u0018\u00010\u000eJ\u0010\u0010c\u001a\u00020\u000b2\b\u0010\\\u001a\u0004\u0018\u00010\u000eJ\\\u0010d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002He0201\"\u0004\b\u0000\u0010e2\u0012\u0010f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002He020g2\u0006\u00103\u001a\u0002042\u0006\u0010<\u001a\u0002072\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u0010\"\u001a\u0004\u0018\u00010\u00062\b\u00108\u001a\u0004\u0018\u00010/H\u0002Jp\u0010d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002He0201\"\u0004\b\u0000\u0010e2\u0012\u0010f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002He020g2\u0006\u00103\u001a\u0002042\u0006\u0010<\u001a\u0002072\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u0010\"\u001a\u0004\u0018\u00010\u00062\b\u00108\u001a\u0004\u0018\u00010/2\u0012\u0010=\u001a\u000e\u0012\u0004\u0012\u00020/\u0012\u0002\b\u0003\u0018\u00010>H\u0002Jf\u0010d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002He0201\"\u0004\b\u0000\u0010e2\u0012\u0010f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002He020g2\u0006\u00103\u001a\u0002042\u0006\u0010<\u001a\u0002072\b\u00105\u001a\u0004\u0018\u00010\u00012\b\u0010\"\u001a\u0004\u0018\u00010\u00062\u0012\u0010=\u001a\u000e\u0012\u0004\u0012\u00020/\u0012\u0002\b\u0003\u0018\u00010>H\u0002J@\u0010d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002He0201\"\u0004\b\u0000\u0010e2\u0014\u0010f\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u0002He\u0018\u0001020g2\u0006\u0010h\u001a\u00020i2\b\u0010\"\u001a\u0004\u0018\u00010\u0006JL\u0010j\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A012\u000e\u0010f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010A0g2\u0006\u00103\u001a\u0002042\u0006\u0010<\u001a\u0002072\b\u00105\u001a\u0004\u0018\u00010\u00012\u0006\u0010C\u001a\u00020D2\b\u0010\"\u001a\u0004\u0018\u00010\u0006H\u0002J\u001a\u0010k\u001a\u00020\u00062\b\u00103\u001a\u0004\u0018\u0001042\b\u0010\"\u001a\u0004\u0018\u00010\u0006J\u0010\u0010l\u001a\u00020\u00062\b\u0010m\u001a\u0004\u0018\u00010\u0006J\u0016\u0010n\u001a\b\u0012\u0004\u0012\u00020\u000e0o2\u0006\u0010H\u001a\u00020IH\u0002J\u0006\u0010p\u001a\u00020GJ\u0006\u0010q\u001a\u00020GJ\u0006\u0010t\u001a\u00020GR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\r¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u001a\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00110\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b&\u0010'R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020)X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010*\u001a\b\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u001c\u001a\u00020\u001d¢\u0006\b\n\u0000\u001a\u0004\b,\u0010-R\u0011\u0010N\u001a\u00020O8F¢\u0006\u0006\u001a\u0004\bP\u0010QR\u0011\u0010r\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\br\u0010s¨\u0006v"}, d2 = {"Lcom/facebook/imagepipeline/core/ImagePipeline;", "", "producerSequenceFactory", "Lcom/facebook/imagepipeline/core/ProducerSequenceFactory;", "requestListeners", "", "Lcom/facebook/imagepipeline/listener/RequestListener;", "requestListener2s", "Lcom/facebook/imagepipeline/listener/RequestListener2;", "isPrefetchEnabledSupplier", "Lcom/facebook/common/internal/Supplier;", "", "bitmapMemoryCache", "Lcom/facebook/imagepipeline/cache/MemoryCache;", "Lcom/facebook/cache/common/CacheKey;", "Lcom/facebook/imagepipeline/image/CloseableImage;", "encodedMemoryCache", "Lcom/facebook/common/memory/PooledByteBuffer;", "diskCachesStoreSupplier", "Lcom/facebook/imagepipeline/core/DiskCachesStore;", "cacheKeyFactory", "Lcom/facebook/imagepipeline/cache/CacheKeyFactory;", "threadHandoffProducerQueue", "Lcom/facebook/imagepipeline/producers/ThreadHandoffProducerQueue;", "suppressBitmapPrefetchingSupplier", "lazyDataSource", "callerContextVerifier", "Lcom/facebook/callercontext/CallerContextVerifier;", "config", "Lcom/facebook/imagepipeline/core/ImagePipelineConfigInterface;", "<init>", "(Lcom/facebook/imagepipeline/core/ProducerSequenceFactory;Ljava/util/Set;Ljava/util/Set;Lcom/facebook/common/internal/Supplier;Lcom/facebook/imagepipeline/cache/MemoryCache;Lcom/facebook/imagepipeline/cache/MemoryCache;Lcom/facebook/common/internal/Supplier;Lcom/facebook/imagepipeline/cache/CacheKeyFactory;Lcom/facebook/imagepipeline/producers/ThreadHandoffProducerQueue;Lcom/facebook/common/internal/Supplier;Lcom/facebook/common/internal/Supplier;Lcom/facebook/callercontext/CallerContextVerifier;Lcom/facebook/imagepipeline/core/ImagePipelineConfigInterface;)V", "getProducerSequenceFactory", "()Lcom/facebook/imagepipeline/core/ProducerSequenceFactory;", "requestListener", "requestListener2", "getBitmapMemoryCache", "()Lcom/facebook/imagepipeline/cache/MemoryCache;", "getCacheKeyFactory", "()Lcom/facebook/imagepipeline/cache/CacheKeyFactory;", "idCounter", "Ljava/util/concurrent/atomic/AtomicLong;", "isLazyDataSource", "()Lcom/facebook/common/internal/Supplier;", "getConfig", "()Lcom/facebook/imagepipeline/core/ImagePipelineConfigInterface;", "generateUniqueFutureId", "", "getDataSourceSupplier", "Lcom/facebook/datasource/DataSource;", "Lcom/facebook/common/references/CloseableReference;", "imageRequest", "Lcom/facebook/imagepipeline/request/ImageRequest;", "callerContext", "requestLevel", "Lcom/facebook/imagepipeline/request/ImageRequest$RequestLevel;", "uiComponentId", "getEncodedImageDataSourceSupplier", "fetchImageFromBitmapCache", "fetchDecodedImage", "lowestPermittedRequestLevelOnSubmit", "extras", "", "fetchEncodedImage", "prefetchToBitmapCache", "Ljava/lang/Void;", "prefetchToDiskCache", "priority", "Lcom/facebook/imagepipeline/common/Priority;", "prefetchToEncodedCache", "evictFromMemoryCache", "", "uri", "Landroid/net/Uri;", "evictFromDiskCache", "evictFromCache", "clearMemoryCaches", "clearDiskCaches", "usedDiskCacheSize", "", "getUsedDiskCacheSize", "()J", "clearCaches", "isInBitmapMemoryCache", "isInEncodedMemoryCache", "isInDiskCacheSync", "cacheChoice", "Lcom/facebook/imagepipeline/request/ImageRequest$CacheChoice;", "isInDynamicDiskCachesSync", "isInDiskCache", "isInDynamicDiskCaches", "Lbolts/Task;", "cacheKey", "intermediateContinuation", "Lbolts/Continuation;", "cts", "Lbolts/CancellationTokenSource;", "getCacheKey", "getCachedImage", "hasCachedImage", "submitFetchRequest", "T", "producerSequence", "Lcom/facebook/imagepipeline/producers/Producer;", "settableProducerContext", "Lcom/facebook/imagepipeline/producers/SettableProducerContext;", "submitPrefetchRequest", "getRequestListenerForRequest", "getCombinedRequestListener", "listener", "predicateForUri", "Lcom/facebook/common/internal/Predicate;", "pause", "resume", "isPaused", "()Z", "init", "Companion", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ImagePipeline {
    private final MemoryCache<CacheKey, CloseableImage> bitmapMemoryCache;
    private final CacheKeyFactory cacheKeyFactory;
    private final CallerContextVerifier callerContextVerifier;
    private final ImagePipelineConfigInterface config;
    private final Supplier<DiskCachesStore> diskCachesStoreSupplier;
    private final MemoryCache<CacheKey, PooledByteBuffer> encodedMemoryCache;
    private final AtomicLong idCounter;
    private final Supplier<Boolean> isLazyDataSource;
    private final Supplier<Boolean> isPrefetchEnabledSupplier;
    private final ProducerSequenceFactory producerSequenceFactory;
    private final RequestListener requestListener;
    private final RequestListener2 requestListener2;
    private final Supplier<Boolean> suppressBitmapPrefetchingSupplier;
    private final ThreadHandoffProducerQueue threadHandoffProducerQueue;
    private static final CancellationException PREFETCH_EXCEPTION = new CancellationException("Prefetching is not enabled");
    private static final CancellationException NULL_IMAGEREQUEST_EXCEPTION = new CancellationException("ImageRequest is null");
    private static final CancellationException MODIFIED_URL_IS_NULL = new CancellationException("Modified URL is null");

    /* compiled from: ImagePipeline.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ImageRequest.CacheChoice.values().length];
            try {
                iArr[ImageRequest.CacheChoice.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[ImageRequest.CacheChoice.SMALL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[ImageRequest.CacheChoice.DYNAMIC.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean clearMemoryCaches$lambda$3(CacheKey it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return true;
    }

    public final void init() {
    }

    public final DataSource<Void> prefetchToEncodedCache(ImageRequest imageRequest, Object obj) {
        return prefetchToEncodedCache$default(this, imageRequest, obj, null, null, 12, null);
    }

    public final DataSource<Void> prefetchToEncodedCache(ImageRequest imageRequest, Object obj, Priority priority) {
        Intrinsics.checkNotNullParameter(priority, "priority");
        return prefetchToEncodedCache$default(this, imageRequest, obj, priority, null, 8, null);
    }

    public ImagePipeline(ProducerSequenceFactory producerSequenceFactory, Set<? extends RequestListener> requestListeners, Set<? extends RequestListener2> requestListener2s, Supplier<Boolean> isPrefetchEnabledSupplier, MemoryCache<CacheKey, CloseableImage> bitmapMemoryCache, MemoryCache<CacheKey, PooledByteBuffer> encodedMemoryCache, Supplier<DiskCachesStore> diskCachesStoreSupplier, CacheKeyFactory cacheKeyFactory, ThreadHandoffProducerQueue threadHandoffProducerQueue, Supplier<Boolean> suppressBitmapPrefetchingSupplier, Supplier<Boolean> lazyDataSource, CallerContextVerifier callerContextVerifier, ImagePipelineConfigInterface config) {
        Intrinsics.checkNotNullParameter(producerSequenceFactory, "producerSequenceFactory");
        Intrinsics.checkNotNullParameter(requestListeners, "requestListeners");
        Intrinsics.checkNotNullParameter(requestListener2s, "requestListener2s");
        Intrinsics.checkNotNullParameter(isPrefetchEnabledSupplier, "isPrefetchEnabledSupplier");
        Intrinsics.checkNotNullParameter(bitmapMemoryCache, "bitmapMemoryCache");
        Intrinsics.checkNotNullParameter(encodedMemoryCache, "encodedMemoryCache");
        Intrinsics.checkNotNullParameter(diskCachesStoreSupplier, "diskCachesStoreSupplier");
        Intrinsics.checkNotNullParameter(cacheKeyFactory, "cacheKeyFactory");
        Intrinsics.checkNotNullParameter(threadHandoffProducerQueue, "threadHandoffProducerQueue");
        Intrinsics.checkNotNullParameter(suppressBitmapPrefetchingSupplier, "suppressBitmapPrefetchingSupplier");
        Intrinsics.checkNotNullParameter(lazyDataSource, "lazyDataSource");
        Intrinsics.checkNotNullParameter(config, "config");
        this.producerSequenceFactory = producerSequenceFactory;
        this.isPrefetchEnabledSupplier = isPrefetchEnabledSupplier;
        this.diskCachesStoreSupplier = diskCachesStoreSupplier;
        this.requestListener = new ForwardingRequestListener((Set<RequestListener>) requestListeners);
        this.requestListener2 = new ForwardingRequestListener2(requestListener2s);
        this.idCounter = new AtomicLong();
        this.bitmapMemoryCache = bitmapMemoryCache;
        this.encodedMemoryCache = encodedMemoryCache;
        this.cacheKeyFactory = cacheKeyFactory;
        this.threadHandoffProducerQueue = threadHandoffProducerQueue;
        this.suppressBitmapPrefetchingSupplier = suppressBitmapPrefetchingSupplier;
        this.isLazyDataSource = lazyDataSource;
        this.callerContextVerifier = callerContextVerifier;
        this.config = config;
    }

    public final ProducerSequenceFactory getProducerSequenceFactory() {
        return this.producerSequenceFactory;
    }

    public final MemoryCache<CacheKey, CloseableImage> getBitmapMemoryCache() {
        return this.bitmapMemoryCache;
    }

    public final CacheKeyFactory getCacheKeyFactory() {
        return this.cacheKeyFactory;
    }

    public final Supplier<Boolean> isLazyDataSource() {
        return this.isLazyDataSource;
    }

    public final ImagePipelineConfigInterface getConfig() {
        return this.config;
    }

    public final String generateUniqueFutureId() {
        return String.valueOf(this.idCounter.getAndIncrement());
    }

    public final Supplier<DataSource<CloseableReference<CloseableImage>>> getDataSourceSupplier(final ImageRequest imageRequest, final Object callerContext, final ImageRequest.RequestLevel requestLevel) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        return new Supplier<DataSource<CloseableReference<CloseableImage>>>() { // from class: com.facebook.imagepipeline.core.ImagePipeline$getDataSourceSupplier$1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.facebook.common.internal.Supplier
            public DataSource<CloseableReference<CloseableImage>> get() {
                return ImagePipeline.fetchDecodedImage$default(ImagePipeline.this, imageRequest, callerContext, requestLevel, null, null, 24, null);
            }

            public String toString() {
                String toStringHelper = Objects.toStringHelper(this).add("uri", imageRequest.getSourceUri()).toString();
                Intrinsics.checkNotNullExpressionValue(toStringHelper, "toString(...)");
                return toStringHelper;
            }
        };
    }

    public final Supplier<DataSource<CloseableReference<CloseableImage>>> getDataSourceSupplier(final ImageRequest imageRequest, final Object callerContext, final ImageRequest.RequestLevel requestLevel, final RequestListener requestListener) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        return new Supplier<DataSource<CloseableReference<CloseableImage>>>() { // from class: com.facebook.imagepipeline.core.ImagePipeline$getDataSourceSupplier$2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.facebook.common.internal.Supplier
            public DataSource<CloseableReference<CloseableImage>> get() {
                return ImagePipeline.fetchDecodedImage$default(ImagePipeline.this, imageRequest, callerContext, requestLevel, requestListener, null, 16, null);
            }

            public String toString() {
                String toStringHelper = Objects.toStringHelper(this).add("uri", imageRequest.getSourceUri()).toString();
                Intrinsics.checkNotNullExpressionValue(toStringHelper, "toString(...)");
                return toStringHelper;
            }
        };
    }

    public final Supplier<DataSource<CloseableReference<CloseableImage>>> getDataSourceSupplier(final ImageRequest imageRequest, final Object callerContext, final ImageRequest.RequestLevel requestLevel, final RequestListener requestListener, final String uiComponentId) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        return new Supplier<DataSource<CloseableReference<CloseableImage>>>() { // from class: com.facebook.imagepipeline.core.ImagePipeline$getDataSourceSupplier$3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.facebook.common.internal.Supplier
            public DataSource<CloseableReference<CloseableImage>> get() {
                return ImagePipeline.this.fetchDecodedImage(imageRequest, callerContext, requestLevel, requestListener, uiComponentId);
            }

            public String toString() {
                String toStringHelper = Objects.toStringHelper(this).add("uri", imageRequest.getSourceUri()).toString();
                Intrinsics.checkNotNullExpressionValue(toStringHelper, "toString(...)");
                return toStringHelper;
            }
        };
    }

    public final Supplier<DataSource<CloseableReference<PooledByteBuffer>>> getEncodedImageDataSourceSupplier(final ImageRequest imageRequest, final Object callerContext) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        return new Supplier<DataSource<CloseableReference<PooledByteBuffer>>>() { // from class: com.facebook.imagepipeline.core.ImagePipeline$getEncodedImageDataSourceSupplier$1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.facebook.common.internal.Supplier
            public DataSource<CloseableReference<PooledByteBuffer>> get() {
                return ImagePipeline.this.fetchEncodedImage(imageRequest, callerContext);
            }

            public String toString() {
                String toStringHelper = Objects.toStringHelper(this).add("uri", imageRequest.getSourceUri()).toString();
                Intrinsics.checkNotNullExpressionValue(toStringHelper, "toString(...)");
                return toStringHelper;
            }
        };
    }

    public final DataSource<CloseableReference<CloseableImage>> fetchImageFromBitmapCache(ImageRequest imageRequest, Object callerContext) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        return fetchDecodedImage(imageRequest, callerContext, ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE);
    }

    public static /* synthetic */ DataSource fetchDecodedImage$default(ImagePipeline imagePipeline, ImageRequest imageRequest, Object obj, ImageRequest.RequestLevel requestLevel, RequestListener requestListener, String str, int i, Object obj2) {
        return imagePipeline.fetchDecodedImage(imageRequest, obj, (i & 4) != 0 ? null : requestLevel, (i & 8) != 0 ? null : requestListener, (i & 16) != 0 ? null : str);
    }

    public final DataSource<CloseableReference<CloseableImage>> fetchDecodedImage(ImageRequest imageRequest, Object callerContext, ImageRequest.RequestLevel lowestPermittedRequestLevelOnSubmit, RequestListener requestListener, String uiComponentId) {
        if (imageRequest == null) {
            DataSource<CloseableReference<CloseableImage>> immediateFailedDataSource = DataSources.immediateFailedDataSource(new NullPointerException());
            Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource, "immediateFailedDataSource(...)");
            return immediateFailedDataSource;
        }
        try {
            Producer<CloseableReference<CloseableImage>> decodedImageProducerSequence = this.producerSequenceFactory.getDecodedImageProducerSequence(imageRequest);
            if (lowestPermittedRequestLevelOnSubmit == null) {
                lowestPermittedRequestLevelOnSubmit = ImageRequest.RequestLevel.FULL_FETCH;
            }
            return submitFetchRequest(decodedImageProducerSequence, imageRequest, lowestPermittedRequestLevelOnSubmit, callerContext, requestListener, uiComponentId);
        } catch (Exception e) {
            return DataSources.immediateFailedDataSource(e);
        }
    }

    public final DataSource<CloseableReference<CloseableImage>> fetchDecodedImage(ImageRequest imageRequest, Object callerContext) {
        return fetchDecodedImage$default(this, imageRequest, callerContext, null, null, null, 24, null);
    }

    public final DataSource<CloseableReference<CloseableImage>> fetchDecodedImage(ImageRequest imageRequest, Object callerContext, RequestListener requestListener) {
        Intrinsics.checkNotNullParameter(requestListener, "requestListener");
        return fetchDecodedImage$default(this, imageRequest, callerContext, null, requestListener, null, 16, null);
    }

    public final DataSource<CloseableReference<CloseableImage>> fetchDecodedImage(ImageRequest imageRequest, Object callerContext, ImageRequest.RequestLevel lowestPermittedRequestLevelOnSubmit) {
        Intrinsics.checkNotNullParameter(lowestPermittedRequestLevelOnSubmit, "lowestPermittedRequestLevelOnSubmit");
        return fetchDecodedImage$default(this, imageRequest, callerContext, lowestPermittedRequestLevelOnSubmit, null, null, 16, null);
    }

    public final DataSource<CloseableReference<CloseableImage>> fetchDecodedImage(ImageRequest imageRequest, Object callerContext, ImageRequest.RequestLevel lowestPermittedRequestLevelOnSubmit, RequestListener requestListener, String uiComponentId, Map<String, ?> extras) {
        Intrinsics.checkNotNullParameter(lowestPermittedRequestLevelOnSubmit, "lowestPermittedRequestLevelOnSubmit");
        if (imageRequest == null) {
            DataSource<CloseableReference<CloseableImage>> immediateFailedDataSource = DataSources.immediateFailedDataSource(new NullPointerException());
            Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource, "immediateFailedDataSource(...)");
            return immediateFailedDataSource;
        }
        try {
            return submitFetchRequest(this.producerSequenceFactory.getDecodedImageProducerSequence(imageRequest), imageRequest, lowestPermittedRequestLevelOnSubmit, callerContext, requestListener, uiComponentId, extras);
        } catch (Exception e) {
            return DataSources.immediateFailedDataSource(e);
        }
    }

    public final DataSource<CloseableReference<PooledByteBuffer>> fetchEncodedImage(ImageRequest imageRequest, Object callerContext) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        return fetchEncodedImage(imageRequest, callerContext, null);
    }

    public final DataSource<CloseableReference<PooledByteBuffer>> fetchEncodedImage(ImageRequest imageRequest, Object callerContext, RequestListener requestListener) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        if (imageRequest.getSourceUri() == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        try {
            Producer<CloseableReference<PooledByteBuffer>> encodedImageProducerSequence = this.producerSequenceFactory.getEncodedImageProducerSequence(imageRequest);
            if (imageRequest.getResizeOptions() != null) {
                imageRequest = ImageRequestBuilder.fromRequest(imageRequest).setResizeOptions(null).build();
            }
            return submitFetchRequest(encodedImageProducerSequence, imageRequest, ImageRequest.RequestLevel.FULL_FETCH, callerContext, requestListener, null, null);
        } catch (Exception e) {
            return DataSources.immediateFailedDataSource(e);
        }
    }

    public final DataSource<Void> prefetchToBitmapCache(ImageRequest imageRequest, Object callerContext) {
        return prefetchToBitmapCache(imageRequest, callerContext, null);
    }

    public final DataSource<Void> prefetchToBitmapCache(ImageRequest imageRequest, Object callerContext, RequestListener requestListener) {
        DataSource<Void> immediateFailedDataSource;
        Producer<Void> decodedImagePrefetchProducerSequence;
        Producer<Void> decodedImagePrefetchProducerSequence2;
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        boolean z = true;
        if (!FrescoSystrace.isTracing()) {
            if (!this.isPrefetchEnabledSupplier.get().booleanValue()) {
                DataSource<Void> immediateFailedDataSource2 = DataSources.immediateFailedDataSource(PREFETCH_EXCEPTION);
                Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource2, "immediateFailedDataSource(...)");
                return immediateFailedDataSource2;
            }
            try {
                if (this.config.getExperiments().getPrefetchShortcutEnabled() && isInBitmapMemoryCache(imageRequest)) {
                    DataSource<Void> immediateSuccessfulDataSource = DataSources.immediateSuccessfulDataSource();
                    Intrinsics.checkNotNullExpressionValue(immediateSuccessfulDataSource, "immediateSuccessfulDataSource(...)");
                    return immediateSuccessfulDataSource;
                }
                if (imageRequest == null) {
                    throw new IllegalStateException("Required value was null.".toString());
                }
                Boolean shouldDecodePrefetches = imageRequest.shouldDecodePrefetches();
                if (shouldDecodePrefetches != null) {
                    if (shouldDecodePrefetches.booleanValue()) {
                        z = false;
                    }
                } else {
                    Boolean bool = this.suppressBitmapPrefetchingSupplier.get();
                    Intrinsics.checkNotNullExpressionValue(bool, "get(...)");
                    z = bool.booleanValue();
                }
                if (z) {
                    decodedImagePrefetchProducerSequence2 = this.producerSequenceFactory.getEncodedImagePrefetchProducerSequence(imageRequest);
                } else {
                    decodedImagePrefetchProducerSequence2 = this.producerSequenceFactory.getDecodedImagePrefetchProducerSequence(imageRequest);
                }
                return submitPrefetchRequest(decodedImagePrefetchProducerSequence2, imageRequest, ImageRequest.RequestLevel.FULL_FETCH, callerContext, Priority.MEDIUM, requestListener);
            } catch (Exception e) {
                return DataSources.immediateFailedDataSource(e);
            }
        }
        FrescoSystrace.beginSection("ImagePipeline#prefetchToBitmapCache");
        try {
            if (!this.isPrefetchEnabledSupplier.get().booleanValue()) {
                DataSource<Void> immediateFailedDataSource3 = DataSources.immediateFailedDataSource(PREFETCH_EXCEPTION);
                Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource3, "immediateFailedDataSource(...)");
                return immediateFailedDataSource3;
            }
            try {
            } catch (Exception e2) {
                immediateFailedDataSource = DataSources.immediateFailedDataSource(e2);
            }
            if (this.config.getExperiments().getPrefetchShortcutEnabled() && isInBitmapMemoryCache(imageRequest)) {
                DataSource<Void> immediateSuccessfulDataSource2 = DataSources.immediateSuccessfulDataSource();
                Intrinsics.checkNotNullExpressionValue(immediateSuccessfulDataSource2, "immediateSuccessfulDataSource(...)");
                return immediateSuccessfulDataSource2;
            }
            if (imageRequest == null) {
                throw new IllegalStateException("Required value was null.".toString());
            }
            Boolean shouldDecodePrefetches2 = imageRequest.shouldDecodePrefetches();
            if (shouldDecodePrefetches2 != null) {
                if (shouldDecodePrefetches2.booleanValue()) {
                    z = false;
                }
            } else {
                Boolean bool2 = this.suppressBitmapPrefetchingSupplier.get();
                Intrinsics.checkNotNullExpressionValue(bool2, "get(...)");
                z = bool2.booleanValue();
            }
            if (z) {
                decodedImagePrefetchProducerSequence = this.producerSequenceFactory.getEncodedImagePrefetchProducerSequence(imageRequest);
            } else {
                decodedImagePrefetchProducerSequence = this.producerSequenceFactory.getDecodedImagePrefetchProducerSequence(imageRequest);
            }
            immediateFailedDataSource = submitPrefetchRequest(decodedImagePrefetchProducerSequence, imageRequest, ImageRequest.RequestLevel.FULL_FETCH, callerContext, Priority.MEDIUM, requestListener);
            return immediateFailedDataSource;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final DataSource<Void> prefetchToDiskCache(ImageRequest imageRequest, Object callerContext, RequestListener requestListener) {
        return prefetchToDiskCache(imageRequest, callerContext, Priority.MEDIUM, requestListener);
    }

    public final DataSource<Void> prefetchToDiskCache(ImageRequest imageRequest, Object callerContext) {
        return prefetchToDiskCache(imageRequest, callerContext, Priority.MEDIUM, null);
    }

    public final DataSource<Void> prefetchToDiskCache(ImageRequest imageRequest, Object callerContext, Priority priority) {
        Intrinsics.checkNotNullParameter(priority, "priority");
        return prefetchToDiskCache(imageRequest, callerContext, priority, null);
    }

    public final DataSource<Void> prefetchToDiskCache(ImageRequest imageRequest, Object callerContext, Priority priority, RequestListener requestListener) {
        Intrinsics.checkNotNullParameter(priority, "priority");
        if (!this.isPrefetchEnabledSupplier.get().booleanValue()) {
            DataSource<Void> immediateFailedDataSource = DataSources.immediateFailedDataSource(PREFETCH_EXCEPTION);
            Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource, "immediateFailedDataSource(...)");
            return immediateFailedDataSource;
        }
        if (imageRequest == null) {
            DataSource<Void> immediateFailedDataSource2 = DataSources.immediateFailedDataSource(new NullPointerException("imageRequest is null"));
            Intrinsics.checkNotNull(immediateFailedDataSource2);
            return immediateFailedDataSource2;
        }
        try {
            return submitPrefetchRequest(this.producerSequenceFactory.getEncodedImagePrefetchProducerSequence(imageRequest), imageRequest, ImageRequest.RequestLevel.FULL_FETCH, callerContext, priority, requestListener);
        } catch (Exception e) {
            return DataSources.immediateFailedDataSource(e);
        }
    }

    public final DataSource<Void> prefetchToEncodedCache(ImageRequest imageRequest, Object callerContext, RequestListener requestListener) {
        return prefetchToEncodedCache(imageRequest, callerContext, Priority.MEDIUM, requestListener);
    }

    public static /* synthetic */ DataSource prefetchToEncodedCache$default(ImagePipeline imagePipeline, ImageRequest imageRequest, Object obj, Priority priority, RequestListener requestListener, int i, Object obj2) {
        if ((i & 4) != 0) {
            priority = Priority.MEDIUM;
        }
        if ((i & 8) != 0) {
            requestListener = null;
        }
        return imagePipeline.prefetchToEncodedCache(imageRequest, obj, priority, requestListener);
    }

    public final DataSource<Void> prefetchToEncodedCache(ImageRequest imageRequest, Object callerContext, Priority priority, RequestListener requestListener) {
        DataSource<Void> immediateFailedDataSource;
        Intrinsics.checkNotNullParameter(priority, "priority");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            if (!this.isPrefetchEnabledSupplier.get().booleanValue()) {
                DataSource<Void> immediateFailedDataSource2 = DataSources.immediateFailedDataSource(PREFETCH_EXCEPTION);
                Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource2, "immediateFailedDataSource(...)");
                return immediateFailedDataSource2;
            }
            if (imageRequest == null) {
                DataSource<Void> immediateFailedDataSource3 = DataSources.immediateFailedDataSource(NULL_IMAGEREQUEST_EXCEPTION);
                Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource3, "immediateFailedDataSource(...)");
                return immediateFailedDataSource3;
            }
            try {
                if (!this.config.getExperiments().getPrefetchShortcutEnabled() || !isInEncodedMemoryCache(imageRequest)) {
                    return submitPrefetchRequest(this.producerSequenceFactory.getEncodedImagePrefetchProducerSequence(imageRequest), imageRequest, ImageRequest.RequestLevel.FULL_FETCH, callerContext, priority, requestListener);
                }
                DataSource<Void> immediateSuccessfulDataSource = DataSources.immediateSuccessfulDataSource();
                Intrinsics.checkNotNullExpressionValue(immediateSuccessfulDataSource, "immediateSuccessfulDataSource(...)");
                return immediateSuccessfulDataSource;
            } catch (Exception e) {
                return DataSources.immediateFailedDataSource(e);
            }
        }
        FrescoSystrace.beginSection("ImagePipeline#prefetchToEncodedCache");
        try {
            if (!this.isPrefetchEnabledSupplier.get().booleanValue()) {
                DataSource<Void> immediateFailedDataSource4 = DataSources.immediateFailedDataSource(PREFETCH_EXCEPTION);
                Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource4, "immediateFailedDataSource(...)");
                return immediateFailedDataSource4;
            }
            if (imageRequest == null) {
                DataSource<Void> immediateFailedDataSource5 = DataSources.immediateFailedDataSource(NULL_IMAGEREQUEST_EXCEPTION);
                Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource5, "immediateFailedDataSource(...)");
                return immediateFailedDataSource5;
            }
            try {
            } catch (Exception e2) {
                immediateFailedDataSource = DataSources.immediateFailedDataSource(e2);
            }
            if (!this.config.getExperiments().getPrefetchShortcutEnabled() || !isInEncodedMemoryCache(imageRequest)) {
                immediateFailedDataSource = submitPrefetchRequest(this.producerSequenceFactory.getEncodedImagePrefetchProducerSequence(imageRequest), imageRequest, ImageRequest.RequestLevel.FULL_FETCH, callerContext, priority, requestListener);
                return immediateFailedDataSource;
            }
            DataSource<Void> immediateSuccessfulDataSource2 = DataSources.immediateSuccessfulDataSource();
            Intrinsics.checkNotNullExpressionValue(immediateSuccessfulDataSource2, "immediateSuccessfulDataSource(...)");
            return immediateSuccessfulDataSource2;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final void evictFromMemoryCache(Uri uri) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        Predicate<CacheKey> predicateForUri = predicateForUri(uri);
        this.bitmapMemoryCache.removeAll(predicateForUri);
        this.encodedMemoryCache.removeAll(predicateForUri);
    }

    public final void evictFromDiskCache(Uri uri) {
        ImageRequest fromUri = ImageRequest.fromUri(uri);
        if (fromUri == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        evictFromDiskCache(fromUri);
    }

    public final void evictFromDiskCache(ImageRequest imageRequest) {
        if (imageRequest == null) {
            return;
        }
        CacheKey encodedCacheKey = this.cacheKeyFactory.getEncodedCacheKey(imageRequest, null);
        DiskCachesStore diskCachesStore = this.diskCachesStoreSupplier.get();
        Intrinsics.checkNotNullExpressionValue(diskCachesStore, "get(...)");
        DiskCachesStore diskCachesStore2 = diskCachesStore;
        BufferedDiskCache mainBufferedDiskCache = diskCachesStore2.getMainBufferedDiskCache();
        Intrinsics.checkNotNull(encodedCacheKey);
        mainBufferedDiskCache.remove(encodedCacheKey);
        diskCachesStore2.getSmallImageBufferedDiskCache().remove(encodedCacheKey);
        Iterator<Map.Entry<String, BufferedDiskCache>> it = diskCachesStore2.getDynamicBufferedDiskCaches().entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().remove(encodedCacheKey);
        }
    }

    public final void evictFromCache(Uri uri) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        evictFromMemoryCache(uri);
        evictFromDiskCache(uri);
    }

    public final void clearMemoryCaches() {
        Predicate<CacheKey> predicate = new Predicate() { // from class: com.facebook.imagepipeline.core.ImagePipeline$$ExternalSyntheticLambda5
            @Override // com.facebook.common.internal.Predicate
            public final boolean apply(Object obj) {
                boolean clearMemoryCaches$lambda$3;
                clearMemoryCaches$lambda$3 = ImagePipeline.clearMemoryCaches$lambda$3((CacheKey) obj);
                return clearMemoryCaches$lambda$3;
            }
        };
        this.bitmapMemoryCache.removeAll(predicate);
        this.encodedMemoryCache.removeAll(predicate);
    }

    public final void clearDiskCaches() {
        DiskCachesStore diskCachesStore = this.diskCachesStoreSupplier.get();
        Intrinsics.checkNotNullExpressionValue(diskCachesStore, "get(...)");
        DiskCachesStore diskCachesStore2 = diskCachesStore;
        diskCachesStore2.getMainBufferedDiskCache().clearAll();
        diskCachesStore2.getSmallImageBufferedDiskCache().clearAll();
        Iterator<Map.Entry<String, BufferedDiskCache>> it = diskCachesStore2.getDynamicBufferedDiskCaches().entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().clearAll();
        }
    }

    public final long getUsedDiskCacheSize() {
        DiskCachesStore diskCachesStore = this.diskCachesStoreSupplier.get();
        Intrinsics.checkNotNullExpressionValue(diskCachesStore, "get(...)");
        DiskCachesStore diskCachesStore2 = diskCachesStore;
        long size = diskCachesStore2.getMainBufferedDiskCache().getSize() + diskCachesStore2.getSmallImageBufferedDiskCache().getSize();
        Collection<BufferedDiskCache> values = diskCachesStore2.getDynamicBufferedDiskCaches().values();
        Intrinsics.checkNotNullExpressionValue(values, "<get-values>(...)");
        Iterator<T> it = values.iterator();
        long j = 0;
        while (it.hasNext()) {
            j += ((BufferedDiskCache) it.next()).getSize();
        }
        return size + j;
    }

    public final void clearCaches() {
        clearMemoryCaches();
        clearDiskCaches();
    }

    public final boolean isInBitmapMemoryCache(Uri uri) {
        if (uri == null) {
            return false;
        }
        return this.bitmapMemoryCache.contains(predicateForUri(uri));
    }

    public final boolean isInBitmapMemoryCache(ImageRequest imageRequest) {
        if (imageRequest == null) {
            return false;
        }
        CacheKey bitmapCacheKey = this.cacheKeyFactory.getBitmapCacheKey(imageRequest, null);
        MemoryCache<CacheKey, CloseableImage> memoryCache = this.bitmapMemoryCache;
        Intrinsics.checkNotNull(bitmapCacheKey);
        CloseableReference<CloseableImage> closeableReference = memoryCache.get(bitmapCacheKey);
        try {
            return CloseableReference.isValid(closeableReference);
        } finally {
            CloseableReference.closeSafely(closeableReference);
        }
    }

    public final boolean isInEncodedMemoryCache(Uri uri) {
        if (uri == null) {
            return false;
        }
        return this.encodedMemoryCache.contains(predicateForUri(uri));
    }

    public final boolean isInEncodedMemoryCache(ImageRequest imageRequest) {
        if (imageRequest == null) {
            return false;
        }
        CacheKey encodedCacheKey = this.cacheKeyFactory.getEncodedCacheKey(imageRequest, null);
        MemoryCache<CacheKey, PooledByteBuffer> memoryCache = this.encodedMemoryCache;
        Intrinsics.checkNotNull(encodedCacheKey);
        CloseableReference<PooledByteBuffer> closeableReference = memoryCache.get(encodedCacheKey);
        try {
            return CloseableReference.isValid(closeableReference);
        } finally {
            CloseableReference.closeSafely(closeableReference);
        }
    }

    public final boolean isInDiskCacheSync(Uri uri) {
        return isInDiskCacheSync(uri, ImageRequest.CacheChoice.SMALL) || isInDiskCacheSync(uri, ImageRequest.CacheChoice.DEFAULT) || isInDiskCacheSync(uri, ImageRequest.CacheChoice.DYNAMIC);
    }

    public final boolean isInDiskCacheSync(Uri uri, ImageRequest.CacheChoice cacheChoice) {
        ImageRequest build = ImageRequestBuilder.newBuilderWithSource(uri).setCacheChoice(cacheChoice).build();
        Intrinsics.checkNotNull(build);
        return isInDiskCacheSync(build);
    }

    private final boolean isInDynamicDiskCachesSync(ImageRequest imageRequest) {
        DiskCachesStore diskCachesStore = this.diskCachesStoreSupplier.get();
        Intrinsics.checkNotNullExpressionValue(diskCachesStore, "get(...)");
        DiskCachesStore diskCachesStore2 = diskCachesStore;
        CacheKey encodedCacheKey = this.cacheKeyFactory.getEncodedCacheKey(imageRequest, null);
        String diskCacheId = imageRequest.getDiskCacheId();
        if (diskCacheId != null) {
            BufferedDiskCache bufferedDiskCache = diskCachesStore2.getDynamicBufferedDiskCaches().get(diskCacheId);
            if (bufferedDiskCache == null) {
                return false;
            }
            Intrinsics.checkNotNull(encodedCacheKey);
            return bufferedDiskCache.diskCheckSync(encodedCacheKey);
        }
        Iterator<Map.Entry<String, BufferedDiskCache>> it = diskCachesStore2.getDynamicBufferedDiskCaches().entrySet().iterator();
        while (it.hasNext()) {
            BufferedDiskCache value = it.next().getValue();
            Intrinsics.checkNotNull(encodedCacheKey);
            if (value.diskCheckSync(encodedCacheKey)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isInDiskCacheSync(ImageRequest imageRequest) {
        boolean diskCheckSync;
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        DiskCachesStore diskCachesStore = this.diskCachesStoreSupplier.get();
        Intrinsics.checkNotNullExpressionValue(diskCachesStore, "get(...)");
        DiskCachesStore diskCachesStore2 = diskCachesStore;
        CacheKey encodedCacheKey = this.cacheKeyFactory.getEncodedCacheKey(imageRequest, null);
        ImageRequest.CacheChoice cacheChoice = imageRequest.getCacheChoice();
        Intrinsics.checkNotNullExpressionValue(cacheChoice, "getCacheChoice(...)");
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            int i = WhenMappings.$EnumSwitchMapping$0[cacheChoice.ordinal()];
            if (i == 1) {
                BufferedDiskCache mainBufferedDiskCache = diskCachesStore2.getMainBufferedDiskCache();
                Intrinsics.checkNotNull(encodedCacheKey);
                diskCheckSync = mainBufferedDiskCache.diskCheckSync(encodedCacheKey);
            } else if (i == 2) {
                BufferedDiskCache smallImageBufferedDiskCache = diskCachesStore2.getSmallImageBufferedDiskCache();
                Intrinsics.checkNotNull(encodedCacheKey);
                diskCheckSync = smallImageBufferedDiskCache.diskCheckSync(encodedCacheKey);
            } else {
                if (i != 3) {
                    throw new NoWhenBranchMatchedException();
                }
                diskCheckSync = isInDynamicDiskCachesSync(imageRequest);
            }
            return diskCheckSync;
        } finally {
            StrictMode.setThreadPolicy(allowThreadDiskReads);
        }
    }

    public final DataSource<Boolean> isInDiskCache(Uri uri) {
        ImageRequest fromUri = ImageRequest.fromUri(uri);
        if (fromUri != null) {
            return isInDiskCache(fromUri);
        }
        throw new IllegalStateException("Required value was null.".toString());
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v4, types: [T, java.lang.Object, bolts.Task] */
    /* JADX WARN: Type inference failed for: r2v6, types: [T, bolts.Task] */
    private final Task<Boolean> isInDynamicDiskCaches(ImageRequest imageRequest, CacheKey cacheKey, final Continuation<Boolean, Void> intermediateContinuation, final CancellationTokenSource cts) {
        Task<Boolean> contains;
        DiskCachesStore diskCachesStore = this.diskCachesStoreSupplier.get();
        Intrinsics.checkNotNullExpressionValue(diskCachesStore, "get(...)");
        DiskCachesStore diskCachesStore2 = diskCachesStore;
        String diskCacheId = imageRequest != null ? imageRequest.getDiskCacheId() : null;
        if (diskCacheId != null) {
            BufferedDiskCache bufferedDiskCache = diskCachesStore2.getDynamicBufferedDiskCaches().get(diskCacheId);
            if (bufferedDiskCache != null && (contains = bufferedDiskCache.contains(cacheKey)) != null) {
                return contains;
            }
            Task<Boolean> forResult = Task.forResult(false);
            Intrinsics.checkNotNullExpressionValue(forResult, "forResult(...)");
            return forResult;
        }
        if (diskCachesStore2.getDynamicBufferedDiskCaches().size() == 0) {
            Task<Boolean> forResult2 = Task.forResult(false);
            Intrinsics.checkNotNullExpressionValue(forResult2, "forResult(...)");
            return forResult2;
        }
        Iterator<Map.Entry<String, BufferedDiskCache>> it = diskCachesStore2.getDynamicBufferedDiskCaches().entrySet().iterator();
        ?? forResult3 = Task.forResult(false);
        Intrinsics.checkNotNullExpressionValue(forResult3, "forResult(...)");
        final Ref.ObjectRef objectRef = new Ref.ObjectRef();
        objectRef.element = forResult3;
        Task<Boolean> task = forResult3;
        while (it.hasNext()) {
            objectRef.element = it.next().getValue().contains(cacheKey);
            task.continueWithTask((Continuation<Boolean, Task<TContinuationResult>>) new Continuation() { // from class: com.facebook.imagepipeline.core.ImagePipeline$isInDynamicDiskCaches$1
                @Override // bolts.Continuation
                public /* bridge */ /* synthetic */ Object then(Task task2) {
                    return then((Task<Boolean>) task2);
                }

                @Override // bolts.Continuation
                public final Task<? extends Object> then(Task<Boolean> task2) {
                    if (!task2.isCancelled() && !task2.isFaulted() && task2.getResult().booleanValue()) {
                        CancellationTokenSource.this.cancel();
                        return Task.forResult(true).continueWith(intermediateContinuation);
                    }
                    if (task2.isCancelled()) {
                        return Task.forResult(false);
                    }
                    return objectRef.element;
                }
            }, cts.getToken());
            task = (Task) objectRef.element;
        }
        return task;
    }

    public final DataSource<Boolean> isInDiskCache(final ImageRequest imageRequest) {
        DiskCachesStore diskCachesStore = this.diskCachesStoreSupplier.get();
        Intrinsics.checkNotNullExpressionValue(diskCachesStore, "get(...)");
        final DiskCachesStore diskCachesStore2 = diskCachesStore;
        final CacheKey encodedCacheKey = this.cacheKeyFactory.getEncodedCacheKey(imageRequest, null);
        final SimpleDataSource create = SimpleDataSource.create();
        final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        Continuation continuation = new Continuation() { // from class: com.facebook.imagepipeline.core.ImagePipeline$$ExternalSyntheticLambda0
            @Override // bolts.Continuation
            public final Object then(Task task) {
                Void isInDiskCache$lambda$7;
                isInDiskCache$lambda$7 = ImagePipeline.isInDiskCache$lambda$7(SimpleDataSource.this, task);
                return isInDiskCache$lambda$7;
            }
        };
        final Continuation continuation2 = new Continuation() { // from class: com.facebook.imagepipeline.core.ImagePipeline$$ExternalSyntheticLambda1
            @Override // bolts.Continuation
            public final Object then(Task task) {
                Void isInDiskCache$lambda$8;
                isInDiskCache$lambda$8 = ImagePipeline.isInDiskCache$lambda$8(SimpleDataSource.this, task);
                return isInDiskCache$lambda$8;
            }
        };
        BufferedDiskCache mainBufferedDiskCache = diskCachesStore2.getMainBufferedDiskCache();
        Intrinsics.checkNotNull(encodedCacheKey);
        mainBufferedDiskCache.contains(encodedCacheKey).continueWithTask(new Continuation() { // from class: com.facebook.imagepipeline.core.ImagePipeline$$ExternalSyntheticLambda2
            @Override // bolts.Continuation
            public final Object then(Task task) {
                Task isInDiskCache$lambda$9;
                isInDiskCache$lambda$9 = ImagePipeline.isInDiskCache$lambda$9(DiskCachesStore.this, encodedCacheKey, task);
                return isInDiskCache$lambda$9;
            }
        }).continueWithTask((Continuation<TContinuationResult, Task<TContinuationResult>>) new Continuation() { // from class: com.facebook.imagepipeline.core.ImagePipeline$$ExternalSyntheticLambda3
            @Override // bolts.Continuation
            public final Object then(Task task) {
                Task isInDiskCache$lambda$10;
                isInDiskCache$lambda$10 = ImagePipeline.isInDiskCache$lambda$10(ImagePipeline.this, imageRequest, encodedCacheKey, continuation2, cancellationTokenSource, task);
                return isInDiskCache$lambda$10;
            }
        }, cancellationTokenSource.getToken()).continueWith(continuation);
        Intrinsics.checkNotNull(create);
        return create;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final Void isInDiskCache$lambda$7(SimpleDataSource simpleDataSource, Task task) {
        Boolean bool = (Boolean) simpleDataSource.getResult();
        boolean z = false;
        if ((bool != null ? bool.booleanValue() : false) || (!task.isCancelled() && !task.isFaulted() && ((Boolean) task.getResult()).booleanValue())) {
            z = true;
        }
        simpleDataSource.setResult(Boolean.valueOf(z));
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final Void isInDiskCache$lambda$8(SimpleDataSource simpleDataSource, Task task) {
        Boolean bool = (Boolean) simpleDataSource.getResult();
        simpleDataSource.setResult(Boolean.valueOf((bool != null ? bool.booleanValue() : false) || !(task.isCancelled() || task.isFaulted() || !((Boolean) task.getResult()).booleanValue())), false);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Task isInDiskCache$lambda$9(DiskCachesStore diskCachesStore, CacheKey cacheKey, Task task) {
        Intrinsics.checkNotNullParameter(diskCachesStore, "$diskCachesStore");
        if (!task.isCancelled() && !task.isFaulted() && ((Boolean) task.getResult()).booleanValue()) {
            return Task.forResult(true);
        }
        BufferedDiskCache smallImageBufferedDiskCache = diskCachesStore.getSmallImageBufferedDiskCache();
        Intrinsics.checkNotNull(cacheKey);
        return smallImageBufferedDiskCache.contains(cacheKey);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Task isInDiskCache$lambda$10(ImagePipeline this$0, ImageRequest imageRequest, CacheKey cacheKey, Continuation intermediateContinuation, CancellationTokenSource cts, Task task) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(intermediateContinuation, "$intermediateContinuation");
        Intrinsics.checkNotNullParameter(cts, "$cts");
        if (!task.isCancelled() && !task.isFaulted() && ((Boolean) task.getResult()).booleanValue()) {
            return Task.forResult(true);
        }
        Intrinsics.checkNotNull(cacheKey);
        return this$0.isInDynamicDiskCaches(imageRequest, cacheKey, intermediateContinuation, cts);
    }

    public final CacheKey getCacheKey(ImageRequest imageRequest, Object callerContext) {
        CacheKey bitmapCacheKey;
        CacheKey bitmapCacheKey2;
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        CacheKey cacheKey = null;
        if (!FrescoSystrace.isTracing()) {
            if (imageRequest == null) {
                return null;
            }
            if (imageRequest.getPostprocessor() != null) {
                bitmapCacheKey2 = this.cacheKeyFactory.getPostprocessedBitmapCacheKey(imageRequest, callerContext);
            } else {
                bitmapCacheKey2 = this.cacheKeyFactory.getBitmapCacheKey(imageRequest, callerContext);
            }
            return bitmapCacheKey2;
        }
        FrescoSystrace.beginSection("ImagePipeline#getCacheKey");
        if (imageRequest != null) {
            try {
                if (imageRequest.getPostprocessor() != null) {
                    bitmapCacheKey = this.cacheKeyFactory.getPostprocessedBitmapCacheKey(imageRequest, callerContext);
                } else {
                    bitmapCacheKey = this.cacheKeyFactory.getBitmapCacheKey(imageRequest, callerContext);
                }
                cacheKey = bitmapCacheKey;
            } finally {
                FrescoSystrace.endSection();
            }
        }
        return cacheKey;
    }

    public final CloseableReference<CloseableImage> getCachedImage(CacheKey cacheKey) {
        if (cacheKey == null) {
            return null;
        }
        CloseableReference<CloseableImage> closeableReference = this.bitmapMemoryCache.get(cacheKey);
        if (closeableReference == null || closeableReference.get().getQualityInfo().isOfFullQuality()) {
            return closeableReference;
        }
        closeableReference.close();
        return null;
    }

    public final boolean hasCachedImage(CacheKey cacheKey) {
        if (cacheKey == null) {
            return false;
        }
        return this.bitmapMemoryCache.contains((MemoryCache<CacheKey, CloseableImage>) cacheKey);
    }

    private final <T> DataSource<CloseableReference<T>> submitFetchRequest(Producer<CloseableReference<T>> producerSequence, ImageRequest imageRequest, ImageRequest.RequestLevel lowestPermittedRequestLevelOnSubmit, Object callerContext, RequestListener requestListener, String uiComponentId) {
        return submitFetchRequest(producerSequence, imageRequest, lowestPermittedRequestLevelOnSubmit, callerContext, requestListener, uiComponentId, null);
    }

    private final <T> DataSource<CloseableReference<T>> submitFetchRequest(Producer<CloseableReference<T>> producerSequence, ImageRequest imageRequest, ImageRequest.RequestLevel lowestPermittedRequestLevelOnSubmit, Object callerContext, RequestListener requestListener, String uiComponentId, Map<String, ?> extras) {
        DataSource<CloseableReference<T>> immediateFailedDataSource;
        ImageRequest.RequestLevel max;
        String generateUniqueFutureId;
        InternalRequestListener internalRequestListener;
        boolean z;
        boolean z2;
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            InternalRequestListener internalRequestListener2 = new InternalRequestListener(getRequestListenerForRequest(imageRequest, requestListener), this.requestListener2);
            CallerContextVerifier callerContextVerifier = this.callerContextVerifier;
            if (callerContextVerifier != null) {
                callerContextVerifier.verifyCallerContext(callerContext, false);
            }
            try {
                ImageRequest.RequestLevel max2 = ImageRequest.RequestLevel.getMax(imageRequest.getLowestPermittedRequestLevel(), lowestPermittedRequestLevelOnSubmit);
                Intrinsics.checkNotNullExpressionValue(max2, "getMax(...)");
                String generateUniqueFutureId2 = generateUniqueFutureId();
                InternalRequestListener internalRequestListener3 = internalRequestListener2;
                if (!imageRequest.getProgressiveRenderingEnabled() && UriUtil.isNetworkUri(imageRequest.getSourceUri())) {
                    z2 = false;
                    SettableProducerContext settableProducerContext = new SettableProducerContext(imageRequest, generateUniqueFutureId2, uiComponentId, internalRequestListener3, callerContext, max2, false, z2, imageRequest.getPriority(), this.config);
                    settableProducerContext.putExtras(extras);
                    return CloseableProducerToDataSourceAdapter.create(producerSequence, settableProducerContext, internalRequestListener2);
                }
                z2 = true;
                SettableProducerContext settableProducerContext2 = new SettableProducerContext(imageRequest, generateUniqueFutureId2, uiComponentId, internalRequestListener3, callerContext, max2, false, z2, imageRequest.getPriority(), this.config);
                settableProducerContext2.putExtras(extras);
                return CloseableProducerToDataSourceAdapter.create(producerSequence, settableProducerContext2, internalRequestListener2);
            } catch (Exception e) {
                return DataSources.immediateFailedDataSource(e);
            }
        }
        FrescoSystrace.beginSection("ImagePipeline#submitFetchRequest");
        try {
            InternalRequestListener internalRequestListener4 = new InternalRequestListener(getRequestListenerForRequest(imageRequest, requestListener), this.requestListener2);
            CallerContextVerifier callerContextVerifier2 = this.callerContextVerifier;
            if (callerContextVerifier2 != null) {
                callerContextVerifier2.verifyCallerContext(callerContext, false);
            }
            try {
                max = ImageRequest.RequestLevel.getMax(imageRequest.getLowestPermittedRequestLevel(), lowestPermittedRequestLevelOnSubmit);
                Intrinsics.checkNotNullExpressionValue(max, "getMax(...)");
                generateUniqueFutureId = generateUniqueFutureId();
                internalRequestListener = internalRequestListener4;
            } catch (Exception e2) {
                immediateFailedDataSource = DataSources.immediateFailedDataSource(e2);
            }
            if (!imageRequest.getProgressiveRenderingEnabled() && UriUtil.isNetworkUri(imageRequest.getSourceUri())) {
                z = false;
                SettableProducerContext settableProducerContext3 = new SettableProducerContext(imageRequest, generateUniqueFutureId, uiComponentId, internalRequestListener, callerContext, max, false, z, imageRequest.getPriority(), this.config);
                settableProducerContext3.putExtras(extras);
                immediateFailedDataSource = CloseableProducerToDataSourceAdapter.create(producerSequence, settableProducerContext3, internalRequestListener4);
                return immediateFailedDataSource;
            }
            z = true;
            SettableProducerContext settableProducerContext32 = new SettableProducerContext(imageRequest, generateUniqueFutureId, uiComponentId, internalRequestListener, callerContext, max, false, z, imageRequest.getPriority(), this.config);
            settableProducerContext32.putExtras(extras);
            immediateFailedDataSource = CloseableProducerToDataSourceAdapter.create(producerSequence, settableProducerContext32, internalRequestListener4);
            return immediateFailedDataSource;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    private final <T> DataSource<CloseableReference<T>> submitFetchRequest(Producer<CloseableReference<T>> producerSequence, ImageRequest imageRequest, ImageRequest.RequestLevel lowestPermittedRequestLevelOnSubmit, Object callerContext, RequestListener requestListener, Map<String, ?> extras) {
        DataSource<CloseableReference<T>> immediateFailedDataSource;
        ImageRequest.RequestLevel max;
        String generateUniqueFutureId;
        InternalRequestListener internalRequestListener;
        boolean z;
        boolean z2;
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            InternalRequestListener internalRequestListener2 = new InternalRequestListener(getRequestListenerForRequest(imageRequest, requestListener), this.requestListener2);
            CallerContextVerifier callerContextVerifier = this.callerContextVerifier;
            if (callerContextVerifier != null) {
                callerContextVerifier.verifyCallerContext(callerContext, false);
            }
            try {
                ImageRequest.RequestLevel max2 = ImageRequest.RequestLevel.getMax(imageRequest.getLowestPermittedRequestLevel(), lowestPermittedRequestLevelOnSubmit);
                Intrinsics.checkNotNullExpressionValue(max2, "getMax(...)");
                String generateUniqueFutureId2 = generateUniqueFutureId();
                InternalRequestListener internalRequestListener3 = internalRequestListener2;
                if (!imageRequest.getProgressiveRenderingEnabled() && UriUtil.isNetworkUri(imageRequest.getSourceUri())) {
                    z2 = false;
                    return CloseableProducerToDataSourceAdapter.create(producerSequence, new SettableProducerContext(imageRequest, generateUniqueFutureId2, null, internalRequestListener3, callerContext, max2, false, z2, imageRequest.getPriority(), this.config), internalRequestListener2);
                }
                z2 = true;
                return CloseableProducerToDataSourceAdapter.create(producerSequence, new SettableProducerContext(imageRequest, generateUniqueFutureId2, null, internalRequestListener3, callerContext, max2, false, z2, imageRequest.getPriority(), this.config), internalRequestListener2);
            } catch (Exception e) {
                return DataSources.immediateFailedDataSource(e);
            }
        }
        FrescoSystrace.beginSection("ImagePipeline#submitFetchRequest");
        try {
            InternalRequestListener internalRequestListener4 = new InternalRequestListener(getRequestListenerForRequest(imageRequest, requestListener), this.requestListener2);
            CallerContextVerifier callerContextVerifier2 = this.callerContextVerifier;
            if (callerContextVerifier2 != null) {
                callerContextVerifier2.verifyCallerContext(callerContext, false);
            }
            try {
                max = ImageRequest.RequestLevel.getMax(imageRequest.getLowestPermittedRequestLevel(), lowestPermittedRequestLevelOnSubmit);
                Intrinsics.checkNotNullExpressionValue(max, "getMax(...)");
                generateUniqueFutureId = generateUniqueFutureId();
                internalRequestListener = internalRequestListener4;
            } catch (Exception e2) {
                immediateFailedDataSource = DataSources.immediateFailedDataSource(e2);
            }
            if (!imageRequest.getProgressiveRenderingEnabled() && UriUtil.isNetworkUri(imageRequest.getSourceUri())) {
                z = false;
                immediateFailedDataSource = CloseableProducerToDataSourceAdapter.create(producerSequence, new SettableProducerContext(imageRequest, generateUniqueFutureId, null, internalRequestListener, callerContext, max, false, z, imageRequest.getPriority(), this.config), internalRequestListener4);
                return immediateFailedDataSource;
            }
            z = true;
            immediateFailedDataSource = CloseableProducerToDataSourceAdapter.create(producerSequence, new SettableProducerContext(imageRequest, generateUniqueFutureId, null, internalRequestListener, callerContext, max, false, z, imageRequest.getPriority(), this.config), internalRequestListener4);
            return immediateFailedDataSource;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final <T> DataSource<CloseableReference<T>> submitFetchRequest(Producer<CloseableReference<T>> producerSequence, SettableProducerContext settableProducerContext, RequestListener requestListener) {
        DataSource<CloseableReference<T>> immediateFailedDataSource;
        Intrinsics.checkNotNullParameter(producerSequence, "producerSequence");
        Intrinsics.checkNotNullParameter(settableProducerContext, "settableProducerContext");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            try {
                return CloseableProducerToDataSourceAdapter.create(producerSequence, settableProducerContext, new InternalRequestListener(requestListener, this.requestListener2));
            } catch (Exception e) {
                return DataSources.immediateFailedDataSource(e);
            }
        }
        FrescoSystrace.beginSection("ImagePipeline#submitFetchRequest");
        try {
            try {
                immediateFailedDataSource = CloseableProducerToDataSourceAdapter.create(producerSequence, settableProducerContext, new InternalRequestListener(requestListener, this.requestListener2));
            } catch (Exception e2) {
                immediateFailedDataSource = DataSources.immediateFailedDataSource(e2);
            }
            return immediateFailedDataSource;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    private final DataSource<Void> submitPrefetchRequest(Producer<Void> producerSequence, ImageRequest imageRequest, ImageRequest.RequestLevel lowestPermittedRequestLevelOnSubmit, Object callerContext, Priority priority, RequestListener requestListener) {
        ImageRequest imageRequest2 = imageRequest;
        InternalRequestListener internalRequestListener = new InternalRequestListener(getRequestListenerForRequest(imageRequest2, requestListener), this.requestListener2);
        CallerContextVerifier callerContextVerifier = this.callerContextVerifier;
        if (callerContextVerifier != null) {
            callerContextVerifier.verifyCallerContext(callerContext, true);
        }
        Uri sourceUri = imageRequest.getSourceUri();
        Intrinsics.checkNotNullExpressionValue(sourceUri, "getSourceUri(...)");
        Uri modifyPrefetchUri = UriModifier.INSTANCE.modifyPrefetchUri(sourceUri, callerContext);
        if (modifyPrefetchUri == null) {
            DataSource<Void> immediateFailedDataSource = DataSources.immediateFailedDataSource(MODIFIED_URL_IS_NULL);
            Intrinsics.checkNotNullExpressionValue(immediateFailedDataSource, "immediateFailedDataSource(...)");
            return immediateFailedDataSource;
        }
        if (!Intrinsics.areEqual(sourceUri, modifyPrefetchUri)) {
            imageRequest2 = ImageRequestBuilder.fromRequest(imageRequest).setSource(modifyPrefetchUri).build();
        }
        ImageRequest imageRequest3 = imageRequest2;
        try {
            ImageRequest.RequestLevel max = ImageRequest.RequestLevel.getMax(imageRequest3.getLowestPermittedRequestLevel(), lowestPermittedRequestLevelOnSubmit);
            Intrinsics.checkNotNullExpressionValue(max, "getMax(...)");
            String generateUniqueFutureId = generateUniqueFutureId();
            InternalRequestListener internalRequestListener2 = internalRequestListener;
            ImagePipelineExperiments experiments = this.config.getExperiments();
            return ProducerToDataSourceAdapter.INSTANCE.create(producerSequence, new SettableProducerContext(imageRequest3, generateUniqueFutureId, internalRequestListener2, callerContext, max, true, experiments != null && experiments.getAllowProgressiveOnPrefetch() && imageRequest3.getProgressiveRenderingEnabled(), priority, this.config), internalRequestListener);
        } catch (Exception e) {
            return DataSources.immediateFailedDataSource(e);
        }
    }

    public final RequestListener getRequestListenerForRequest(ImageRequest imageRequest, RequestListener requestListener) {
        ForwardingRequestListener forwardingRequestListener;
        if (imageRequest == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        if (requestListener == null) {
            if (imageRequest.getRequestListener() == null) {
                return this.requestListener;
            }
            return new ForwardingRequestListener(this.requestListener, imageRequest.getRequestListener());
        }
        if (imageRequest.getRequestListener() == null) {
            forwardingRequestListener = new ForwardingRequestListener(this.requestListener, requestListener);
        } else {
            forwardingRequestListener = new ForwardingRequestListener(this.requestListener, requestListener, imageRequest.getRequestListener());
        }
        return forwardingRequestListener;
    }

    public final RequestListener getCombinedRequestListener(RequestListener listener) {
        return listener != null ? new ForwardingRequestListener(this.requestListener, listener) : this.requestListener;
    }

    private final Predicate<CacheKey> predicateForUri(final Uri uri) {
        return new Predicate() { // from class: com.facebook.imagepipeline.core.ImagePipeline$$ExternalSyntheticLambda4
            @Override // com.facebook.common.internal.Predicate
            public final boolean apply(Object obj) {
                boolean predicateForUri$lambda$16;
                predicateForUri$lambda$16 = ImagePipeline.predicateForUri$lambda$16(uri, (CacheKey) obj);
                return predicateForUri$lambda$16;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean predicateForUri$lambda$16(Uri uri, CacheKey key) {
        Intrinsics.checkNotNullParameter(uri, "$uri");
        Intrinsics.checkNotNullParameter(key, "key");
        return key.containsUri(uri);
    }

    public final void pause() {
        this.threadHandoffProducerQueue.startQueueing();
    }

    public final void resume() {
        this.threadHandoffProducerQueue.stopQueuing();
    }

    public final boolean isPaused() {
        return this.threadHandoffProducerQueue.isQueueing();
    }
}
