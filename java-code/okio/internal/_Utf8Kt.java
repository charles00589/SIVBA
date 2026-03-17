package okio.internal;

import com.facebook.imageutils.JfifUtil;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlin.jvm.internal.Intrinsics;
import okio.Utf8;

/* compiled from: -Utf8.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0012\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\u001e\u0010\u0003\u001a\u00020\u0002*\u00020\u00012\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005¨\u0006\u0007"}, d2 = {"commonAsUtf8ToByteArray", "", "", "commonToUtf8String", "beginIndex", "", "endIndex", "okio"}, k = 2, mv = {1, 4, 0})
/* loaded from: classes.dex */
public final class _Utf8Kt {
    public static /* synthetic */ String commonToUtf8String$default(byte[] bArr, int i, int i2, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            i = 0;
        }
        if ((i3 & 2) != 0) {
            i2 = bArr.length;
        }
        return commonToUtf8String(bArr, i, i2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x00fb, code lost:
    
        if ((r16[r5] & 192) == 128) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x0090, code lost:
    
        if ((r16[r5] & 192) == 128) goto L31;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final String commonToUtf8String(byte[] commonToUtf8String, int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8 = i;
        Intrinsics.checkNotNullParameter(commonToUtf8String, "$this$commonToUtf8String");
        if (i8 < 0 || i2 > commonToUtf8String.length || i8 > i2) {
            throw new ArrayIndexOutOfBoundsException("size=" + commonToUtf8String.length + " beginIndex=" + i8 + " endIndex=" + i2);
        }
        char[] cArr = new char[i2 - i8];
        int i9 = 0;
        while (i8 < i2) {
            byte b = commonToUtf8String[i8];
            if (b >= 0) {
                i3 = i9 + 1;
                cArr[i9] = (char) b;
                i8++;
                while (i8 < i2) {
                    byte b2 = commonToUtf8String[i8];
                    if (b2 < 0) {
                        break;
                    }
                    i8++;
                    cArr[i3] = (char) b2;
                    i3++;
                }
            } else {
                if ((b >> 5) == -2) {
                    int i10 = i8 + 1;
                    if (i2 <= i10) {
                        i3 = i9 + 1;
                        cArr[i9] = (char) Utf8.REPLACEMENT_CODE_POINT;
                    } else {
                        byte b3 = commonToUtf8String[i10];
                        if ((b3 & 192) == 128) {
                            int i11 = (b << 6) ^ (b3 ^ ByteCompanionObject.MIN_VALUE);
                            if (i11 < 128) {
                                i3 = i9 + 1;
                                cArr[i9] = (char) Utf8.REPLACEMENT_CODE_POINT;
                            } else {
                                i3 = i9 + 1;
                                cArr[i9] = (char) i11;
                            }
                            Unit unit = Unit.INSTANCE;
                            i4 = 2;
                        } else {
                            i3 = i9 + 1;
                            cArr[i9] = (char) Utf8.REPLACEMENT_CODE_POINT;
                        }
                    }
                    Unit unit2 = Unit.INSTANCE;
                    i4 = 1;
                } else if ((b >> 4) == -2) {
                    int i12 = i8 + 2;
                    if (i2 <= i12) {
                        i3 = i9 + 1;
                        cArr[i9] = (char) Utf8.REPLACEMENT_CODE_POINT;
                        Unit unit3 = Unit.INSTANCE;
                        int i13 = i8 + 1;
                        if (i2 > i13) {
                        }
                        i4 = 1;
                    } else {
                        byte b4 = commonToUtf8String[i8 + 1];
                        if ((b4 & 192) == 128) {
                            byte b5 = commonToUtf8String[i12];
                            if ((b5 & 192) == 128) {
                                int i14 = (b << 12) ^ ((b5 ^ ByteCompanionObject.MIN_VALUE) ^ (b4 << 6));
                                if (i14 < 2048) {
                                    i3 = i9 + 1;
                                    cArr[i9] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                } else if (55296 <= i14 && 57343 >= i14) {
                                    i3 = i9 + 1;
                                    cArr[i9] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                } else {
                                    i3 = i9 + 1;
                                    cArr[i9] = (char) i14;
                                }
                                Unit unit4 = Unit.INSTANCE;
                                i4 = 3;
                            } else {
                                i3 = i9 + 1;
                                cArr[i9] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                Unit unit5 = Unit.INSTANCE;
                                i4 = 2;
                            }
                        } else {
                            i3 = i9 + 1;
                            cArr[i9] = (char) Utf8.REPLACEMENT_CODE_POINT;
                            Unit unit6 = Unit.INSTANCE;
                            i4 = 1;
                        }
                    }
                } else {
                    if ((b >> 3) == -2) {
                        int i15 = i8 + 3;
                        if (i2 <= i15) {
                            i5 = i9 + 1;
                            cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                            Unit unit7 = Unit.INSTANCE;
                            int i16 = i8 + 1;
                            if (i2 > i16 && (commonToUtf8String[i16] & 192) == 128) {
                                int i17 = i8 + 2;
                                if (i2 > i17) {
                                }
                                i7 = 2;
                            }
                            i7 = 1;
                        } else {
                            byte b6 = commonToUtf8String[i8 + 1];
                            if ((b6 & 192) == 128) {
                                byte b7 = commonToUtf8String[i8 + 2];
                                if ((b7 & 192) == 128) {
                                    byte b8 = commonToUtf8String[i15];
                                    if ((b8 & 192) == 128) {
                                        int i18 = (b << 18) ^ (((b8 ^ ByteCompanionObject.MIN_VALUE) ^ (b7 << 6)) ^ (b6 << 12));
                                        if (i18 > 1114111) {
                                            i5 = i9 + 1;
                                            cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                                        } else if (55296 <= i18 && 57343 >= i18) {
                                            i5 = i9 + 1;
                                            cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                                        } else if (i18 < 65536) {
                                            i5 = i9 + 1;
                                            cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                                        } else {
                                            if (i18 != 65533) {
                                                cArr[i9] = (char) ((i18 >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                                i6 = i9 + 2;
                                                cArr[i9 + 1] = (char) ((i18 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                            } else {
                                                cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                                                i6 = i9 + 1;
                                            }
                                            Unit unit8 = Unit.INSTANCE;
                                            i5 = i6;
                                            i7 = 4;
                                        }
                                        Unit unit9 = Unit.INSTANCE;
                                        i7 = 4;
                                    } else {
                                        i5 = i9 + 1;
                                        cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                                        Unit unit10 = Unit.INSTANCE;
                                        i7 = 3;
                                    }
                                } else {
                                    i5 = i9 + 1;
                                    cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                                    Unit unit11 = Unit.INSTANCE;
                                    i7 = 2;
                                }
                            } else {
                                i5 = i9 + 1;
                                cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                                Unit unit12 = Unit.INSTANCE;
                                i7 = 1;
                            }
                        }
                        i8 += i7;
                    } else {
                        i5 = i9 + 1;
                        cArr[i9] = Utf8.REPLACEMENT_CHARACTER;
                        i8++;
                    }
                    i9 = i5;
                }
                i8 += i4;
            }
            i9 = i3;
        }
        return new String(cArr, 0, i9);
    }

    public static final byte[] commonAsUtf8ToByteArray(String commonAsUtf8ToByteArray) {
        int i;
        char charAt;
        Intrinsics.checkNotNullParameter(commonAsUtf8ToByteArray, "$this$commonAsUtf8ToByteArray");
        byte[] bArr = new byte[commonAsUtf8ToByteArray.length() * 4];
        int length = commonAsUtf8ToByteArray.length();
        int i2 = 0;
        while (i2 < length) {
            char charAt2 = commonAsUtf8ToByteArray.charAt(i2);
            if (Intrinsics.compare((int) charAt2, 128) >= 0) {
                int length2 = commonAsUtf8ToByteArray.length();
                int i3 = i2;
                while (i2 < length2) {
                    char charAt3 = commonAsUtf8ToByteArray.charAt(i2);
                    if (Intrinsics.compare((int) charAt3, 128) < 0) {
                        int i4 = i3 + 1;
                        bArr[i3] = (byte) charAt3;
                        i2++;
                        while (i2 < length2 && Intrinsics.compare((int) commonAsUtf8ToByteArray.charAt(i2), 128) < 0) {
                            bArr[i4] = (byte) commonAsUtf8ToByteArray.charAt(i2);
                            i2++;
                            i4++;
                        }
                        i3 = i4;
                    } else {
                        if (Intrinsics.compare((int) charAt3, 2048) < 0) {
                            bArr[i3] = (byte) ((charAt3 >> 6) | JfifUtil.MARKER_SOFn);
                            i3 += 2;
                            bArr[i3 + 1] = (byte) ((charAt3 & '?') | 128);
                        } else if (55296 > charAt3 || 57343 < charAt3) {
                            bArr[i3] = (byte) ((charAt3 >> '\f') | 224);
                            bArr[i3 + 1] = (byte) (((charAt3 >> 6) & 63) | 128);
                            i3 += 3;
                            bArr[i3 + 2] = (byte) ((charAt3 & '?') | 128);
                        } else if (Intrinsics.compare((int) charAt3, 56319) > 0 || length2 <= (i = i2 + 1) || 56320 > (charAt = commonAsUtf8ToByteArray.charAt(i)) || 57343 < charAt) {
                            bArr[i3] = Utf8.REPLACEMENT_BYTE;
                            i2++;
                            i3++;
                        } else {
                            int charAt4 = ((charAt3 << '\n') + commonAsUtf8ToByteArray.charAt(i)) - 56613888;
                            bArr[i3] = (byte) ((charAt4 >> 18) | 240);
                            bArr[i3 + 1] = (byte) (((charAt4 >> 12) & 63) | 128);
                            bArr[i3 + 2] = (byte) (((charAt4 >> 6) & 63) | 128);
                            i3 += 4;
                            bArr[i3 + 3] = (byte) ((charAt4 & 63) | 128);
                            i2 += 2;
                        }
                        i2++;
                    }
                }
                byte[] copyOf = Arrays.copyOf(bArr, i3);
                Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, newSize)");
                return copyOf;
            }
            bArr[i2] = (byte) charAt2;
            i2++;
        }
        byte[] copyOf2 = Arrays.copyOf(bArr, commonAsUtf8ToByteArray.length());
        Intrinsics.checkNotNullExpressionValue(copyOf2, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf2;
    }
}
