package com.facebook.react.common.mapbuffer;

import com.facebook.jni.HybridClassBase;
import com.facebook.react.common.mapbuffer.MapBuffer;
import com.facebook.react.uimanager.ViewProps;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.UShort;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.Charsets;
import kotlin.text.Typography;

/* compiled from: ReadableMapBuffer.kt */
@Metadata(d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010(\n\u0002\b\u0003\b\u0007\u0018\u0000 B2\u00020\u00012\u00020\u0002:\u0002ABB\u0019\b\u0003\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\r\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u0006H\u0002J\b\u0010\u000f\u001a\u00020\u0010H\u0002J\u0010\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u0006H\u0002J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0006H\u0002J\u0018\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u0016H\u0002J\u0017\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0006H\u0002¢\u0006\u0004\b\u001e\u0010\u001fJ\u0010\u0010 \u001a\u00020!2\u0006\u0010\u001d\u001a\u00020\u0006H\u0002J\u0010\u0010\"\u001a\u00020\u00062\u0006\u0010\u001d\u001a\u00020\u0006H\u0002J\u0010\u0010#\u001a\u00020$2\u0006\u0010\u001d\u001a\u00020\u0006H\u0002J\u0010\u0010%\u001a\u00020&2\u0006\u0010\u001d\u001a\u00020\u0006H\u0002J\u0010\u0010'\u001a\u00020(2\u0006\u0010\u001d\u001a\u00020\u0006H\u0002J\u0010\u0010)\u001a\u00020\u00002\u0006\u0010*\u001a\u00020\u0006H\u0002J\u0016\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00000,2\u0006\u0010*\u001a\u00020\u0006H\u0002J\u0010\u0010-\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\u0002J\u0010\u0010.\u001a\u00020&2\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0010\u0010/\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0010\u00100\u001a\u0002012\u0006\u0010\u000e\u001a\u00020\u0006H\u0016J\u0010\u00102\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0010\u00103\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0010\u00104\u001a\u00020$2\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0010\u00105\u001a\u00020!2\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0010\u00106\u001a\u00020(2\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0010\u00107\u001a\u00020&2\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0010\u00108\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\u0016\u00109\u001a\b\u0012\u0004\u0012\u00020\u00000,2\u0006\u0010\u0019\u001a\u00020\u0006H\u0016J\b\u0010:\u001a\u00020\u0006H\u0016J\u0013\u0010;\u001a\u00020&2\b\u0010<\u001a\u0004\u0018\u00010=H\u0096\u0002J\b\u0010>\u001a\u00020(H\u0016J\u000f\u0010?\u001a\b\u0012\u0004\u0012\u0002010@H\u0096\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\n\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u0006@RX\u0096\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0011\u001a\u00020\u00068BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\f¨\u0006C"}, d2 = {"Lcom/facebook/react/common/mapbuffer/ReadableMapBuffer;", "Lcom/facebook/jni/HybridClassBase;", "Lcom/facebook/react/common/mapbuffer/MapBuffer;", "buffer", "Ljava/nio/ByteBuffer;", "offsetToMapBuffer", "", "<init>", "(Ljava/nio/ByteBuffer;I)V", "value", "count", "getCount", "()I", "cloneWithOffset", "offset", "readHeader", "", "offsetForDynamicData", "getOffsetForDynamicData", "getBucketIndexForKey", "intKey", "readDataType", "Lcom/facebook/react/common/mapbuffer/MapBuffer$DataType;", "bucketIndex", "getTypedValueOffsetForKey", "key", "expected", "readUnsignedShort", "Lkotlin/UShort;", "bufferPosition", "readUnsignedShort-BwKQO78", "(I)S", "readDoubleValue", "", "readIntValue", "readLongValue", "", "readBooleanValue", "", "readStringValue", "", "readMapBufferValue", ViewProps.POSITION, "readMapBufferListValue", "", "getKeyOffsetForBucketIndex", "contains", "getKeyOffset", "entryAt", "Lcom/facebook/react/common/mapbuffer/MapBuffer$Entry;", "getType", "getInt", "getLong", "getDouble", "getString", "getBoolean", "getMapBuffer", "getMapBufferList", "hashCode", "equals", "other", "", "toString", "iterator", "", "MapBufferEntry", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ReadableMapBuffer extends HybridClassBase implements MapBuffer {
    private static final int ALIGNMENT = 254;
    private static final int BUCKET_SIZE = 12;
    private static final int HEADER_SIZE = 8;
    private static final int TYPE_OFFSET = 2;
    private static final int VALUE_OFFSET = 4;
    private final ByteBuffer buffer;
    private int count;
    private final int offsetToMapBuffer;

    /* compiled from: ReadableMapBuffer.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[MapBuffer.DataType.values().length];
            try {
                iArr[MapBuffer.DataType.BOOL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[MapBuffer.DataType.INT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[MapBuffer.DataType.LONG.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[MapBuffer.DataType.DOUBLE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                iArr[MapBuffer.DataType.STRING.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr[MapBuffer.DataType.MAP.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    private ReadableMapBuffer(ByteBuffer byteBuffer, int i) {
        this.buffer = byteBuffer;
        this.offsetToMapBuffer = i;
        readHeader();
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public int getCount() {
        return this.count;
    }

    private final ReadableMapBuffer cloneWithOffset(int offset) {
        ByteBuffer duplicate = this.buffer.duplicate();
        duplicate.position(offset);
        Intrinsics.checkNotNullExpressionValue(duplicate, "apply(...)");
        return new ReadableMapBuffer(duplicate, offset);
    }

    private final void readHeader() {
        if (this.buffer.getShort() != ALIGNMENT) {
            this.buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        this.count = m267readUnsignedShortBwKQO78(this.buffer.position()) & UShort.MAX_VALUE;
    }

    private final int getOffsetForDynamicData() {
        return getKeyOffsetForBucketIndex(getCount());
    }

    private final int getBucketIndexForKey(int intKey) {
        IntRange kEY_RANGE$ReactAndroid_release = MapBuffer.INSTANCE.getKEY_RANGE$ReactAndroid_release();
        int first = kEY_RANGE$ReactAndroid_release.getFirst();
        if (intKey <= kEY_RANGE$ReactAndroid_release.getLast() && first <= intKey) {
            short m770constructorimpl = UShort.m770constructorimpl((short) intKey);
            int count = getCount() - 1;
            int i = 0;
            while (i <= count) {
                int i2 = (i + count) >>> 1;
                int m267readUnsignedShortBwKQO78 = m267readUnsignedShortBwKQO78(getKeyOffsetForBucketIndex(i2)) & UShort.MAX_VALUE;
                int i3 = 65535 & m770constructorimpl;
                if (Intrinsics.compare(m267readUnsignedShortBwKQO78, i3) < 0) {
                    i = i2 + 1;
                } else {
                    if (Intrinsics.compare(m267readUnsignedShortBwKQO78, i3) <= 0) {
                        return i2;
                    }
                    count = i2 - 1;
                }
            }
        }
        return -1;
    }

    private final MapBuffer.DataType readDataType(int bucketIndex) {
        return MapBuffer.DataType.values()[m267readUnsignedShortBwKQO78(getKeyOffsetForBucketIndex(bucketIndex) + 2) & UShort.MAX_VALUE];
    }

    private final int getTypedValueOffsetForKey(int key, MapBuffer.DataType expected) {
        int bucketIndexForKey = getBucketIndexForKey(key);
        if (bucketIndexForKey == -1) {
            throw new IllegalArgumentException(("Key not found: " + key).toString());
        }
        MapBuffer.DataType readDataType = readDataType(bucketIndexForKey);
        if (readDataType != expected) {
            throw new IllegalStateException(("Expected " + expected + " for key: " + key + ", found " + readDataType + " instead.").toString());
        }
        return getKeyOffsetForBucketIndex(bucketIndexForKey) + 4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: readUnsignedShort-BwKQO78, reason: not valid java name */
    public final short m267readUnsignedShortBwKQO78(int bufferPosition) {
        return UShort.m770constructorimpl(this.buffer.getShort(bufferPosition));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final double readDoubleValue(int bufferPosition) {
        return this.buffer.getDouble(bufferPosition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int readIntValue(int bufferPosition) {
        return this.buffer.getInt(bufferPosition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final long readLongValue(int bufferPosition) {
        return this.buffer.getLong(bufferPosition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean readBooleanValue(int bufferPosition) {
        return readIntValue(bufferPosition) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final String readStringValue(int bufferPosition) {
        int offsetForDynamicData = getOffsetForDynamicData() + this.buffer.getInt(bufferPosition);
        int i = this.buffer.getInt(offsetForDynamicData);
        byte[] bArr = new byte[i];
        this.buffer.position(offsetForDynamicData + 4);
        this.buffer.get(bArr, 0, i);
        return new String(bArr, Charsets.UTF_8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final ReadableMapBuffer readMapBufferValue(int position) {
        return cloneWithOffset(getOffsetForDynamicData() + this.buffer.getInt(position) + 4);
    }

    private final List<ReadableMapBuffer> readMapBufferListValue(int position) {
        ArrayList arrayList = new ArrayList();
        int offsetForDynamicData = getOffsetForDynamicData() + this.buffer.getInt(position);
        int i = this.buffer.getInt(offsetForDynamicData);
        int i2 = offsetForDynamicData + 4;
        int i3 = 0;
        while (i3 < i) {
            int i4 = this.buffer.getInt(i2 + i3);
            int i5 = i3 + 4;
            arrayList.add(cloneWithOffset(i2 + i5));
            i3 = i5 + i4;
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int getKeyOffsetForBucketIndex(int bucketIndex) {
        return this.offsetToMapBuffer + 8 + (bucketIndex * 12);
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public boolean contains(int key) {
        return getBucketIndexForKey(key) != -1;
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public int getKeyOffset(int key) {
        return getBucketIndexForKey(key);
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public MapBuffer.Entry entryAt(int offset) {
        return new MapBufferEntry(getKeyOffsetForBucketIndex(offset));
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public MapBuffer.DataType getType(int key) {
        int bucketIndexForKey = getBucketIndexForKey(key);
        if (bucketIndexForKey == -1) {
            throw new IllegalArgumentException(("Key not found: " + key).toString());
        }
        return readDataType(bucketIndexForKey);
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public int getInt(int key) {
        return readIntValue(getTypedValueOffsetForKey(key, MapBuffer.DataType.INT));
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public long getLong(int key) {
        return readLongValue(getTypedValueOffsetForKey(key, MapBuffer.DataType.LONG));
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public double getDouble(int key) {
        return readDoubleValue(getTypedValueOffsetForKey(key, MapBuffer.DataType.DOUBLE));
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public String getString(int key) {
        return readStringValue(getTypedValueOffsetForKey(key, MapBuffer.DataType.STRING));
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public boolean getBoolean(int key) {
        return readBooleanValue(getTypedValueOffsetForKey(key, MapBuffer.DataType.BOOL));
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public ReadableMapBuffer getMapBuffer(int key) {
        return readMapBufferValue(getTypedValueOffsetForKey(key, MapBuffer.DataType.MAP));
    }

    @Override // com.facebook.react.common.mapbuffer.MapBuffer
    public List<ReadableMapBuffer> getMapBufferList(int key) {
        return readMapBufferListValue(getTypedValueOffsetForKey(key, MapBuffer.DataType.MAP));
    }

    public int hashCode() {
        this.buffer.rewind();
        return this.buffer.hashCode();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ReadableMapBuffer)) {
            return false;
        }
        ByteBuffer byteBuffer = this.buffer;
        ByteBuffer byteBuffer2 = ((ReadableMapBuffer) other).buffer;
        if (byteBuffer == byteBuffer2) {
            return true;
        }
        byteBuffer.rewind();
        byteBuffer2.rewind();
        return Intrinsics.areEqual(byteBuffer, byteBuffer2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        CollectionsKt.joinTo(this, sb, (r14 & 2) != 0 ? ", " : null, (r14 & 4) != 0 ? "" : null, (r14 & 8) != 0 ? "" : null, (r14 & 16) != 0 ? -1 : 0, (r14 & 32) != 0 ? "..." : null, (r14 & 64) != 0 ? null : new Function1() { // from class: com.facebook.react.common.mapbuffer.ReadableMapBuffer$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                CharSequence string$lambda$5;
                string$lambda$5 = ReadableMapBuffer.toString$lambda$5((MapBuffer.Entry) obj);
                return string$lambda$5;
            }
        });
        sb.append('}');
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "toString(...)");
        return sb2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence toString$lambda$5(MapBuffer.Entry entry) {
        Intrinsics.checkNotNullParameter(entry, "entry");
        StringBuilder sb = new StringBuilder();
        sb.append(entry.getKey());
        sb.append('=');
        switch (WhenMappings.$EnumSwitchMapping$0[entry.getType().ordinal()]) {
            case 1:
                sb.append(entry.getBooleanValue());
                break;
            case 2:
                sb.append(entry.getIntValue());
                break;
            case 3:
                sb.append(entry.getLongValue());
                break;
            case 4:
                sb.append(entry.getDoubleValue());
                break;
            case 5:
                sb.append(Typography.quote);
                sb.append(entry.getStringValue());
                sb.append(Typography.quote);
                break;
            case 6:
                sb.append(entry.getMapBufferValue().toString());
                break;
            default:
                throw new NoWhenBranchMatchedException();
        }
        return sb;
    }

    @Override // java.lang.Iterable
    public Iterator<MapBuffer.Entry> iterator() {
        return new ReadableMapBuffer$iterator$1(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ReadableMapBuffer.kt */
    @Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0082\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\r\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\u00118VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0014\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\fR\u0014\u0010\u0016\u001a\u00020\u00178VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001a\u001a\u00020\u001b8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001e\u001a\u00020\u001f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b \u0010!R\u0014\u0010\"\u001a\u00020#8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b$\u0010%¨\u0006&"}, d2 = {"Lcom/facebook/react/common/mapbuffer/ReadableMapBuffer$MapBufferEntry;", "Lcom/facebook/react/common/mapbuffer/MapBuffer$Entry;", "bucketOffset", "", "<init>", "(Lcom/facebook/react/common/mapbuffer/ReadableMapBuffer;I)V", "assertType", "", "expected", "Lcom/facebook/react/common/mapbuffer/MapBuffer$DataType;", "key", "getKey", "()I", "type", "getType", "()Lcom/facebook/react/common/mapbuffer/MapBuffer$DataType;", "doubleValue", "", "getDoubleValue", "()D", "intValue", "getIntValue", "longValue", "", "getLongValue", "()J", "booleanValue", "", "getBooleanValue", "()Z", "stringValue", "", "getStringValue", "()Ljava/lang/String;", "mapBufferValue", "Lcom/facebook/react/common/mapbuffer/MapBuffer;", "getMapBufferValue", "()Lcom/facebook/react/common/mapbuffer/MapBuffer;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public final class MapBufferEntry implements MapBuffer.Entry {
        private final int bucketOffset;

        public MapBufferEntry(int i) {
            this.bucketOffset = i;
        }

        private final void assertType(MapBuffer.DataType expected) {
            MapBuffer.DataType type = getType();
            if (expected == type) {
                return;
            }
            throw new IllegalStateException(("Expected " + expected + " for key: " + getKey() + " found " + type + " instead.").toString());
        }

        @Override // com.facebook.react.common.mapbuffer.MapBuffer.Entry
        public int getKey() {
            return ReadableMapBuffer.this.m267readUnsignedShortBwKQO78(this.bucketOffset) & UShort.MAX_VALUE;
        }

        @Override // com.facebook.react.common.mapbuffer.MapBuffer.Entry
        public MapBuffer.DataType getType() {
            return MapBuffer.DataType.values()[ReadableMapBuffer.this.m267readUnsignedShortBwKQO78(this.bucketOffset + 2) & UShort.MAX_VALUE];
        }

        @Override // com.facebook.react.common.mapbuffer.MapBuffer.Entry
        public double getDoubleValue() {
            assertType(MapBuffer.DataType.DOUBLE);
            return ReadableMapBuffer.this.readDoubleValue(this.bucketOffset + 4);
        }

        @Override // com.facebook.react.common.mapbuffer.MapBuffer.Entry
        public int getIntValue() {
            assertType(MapBuffer.DataType.INT);
            return ReadableMapBuffer.this.readIntValue(this.bucketOffset + 4);
        }

        @Override // com.facebook.react.common.mapbuffer.MapBuffer.Entry
        public long getLongValue() {
            assertType(MapBuffer.DataType.LONG);
            return ReadableMapBuffer.this.readLongValue(this.bucketOffset + 4);
        }

        @Override // com.facebook.react.common.mapbuffer.MapBuffer.Entry
        public boolean getBooleanValue() {
            assertType(MapBuffer.DataType.BOOL);
            return ReadableMapBuffer.this.readBooleanValue(this.bucketOffset + 4);
        }

        @Override // com.facebook.react.common.mapbuffer.MapBuffer.Entry
        public String getStringValue() {
            assertType(MapBuffer.DataType.STRING);
            return ReadableMapBuffer.this.readStringValue(this.bucketOffset + 4);
        }

        @Override // com.facebook.react.common.mapbuffer.MapBuffer.Entry
        public MapBuffer getMapBufferValue() {
            assertType(MapBuffer.DataType.MAP);
            return ReadableMapBuffer.this.readMapBufferValue(this.bucketOffset + 4);
        }
    }
}
