package com.facebook.react.modules.network;

import com.facebook.common.logging.FLog;
import com.facebook.common.util.UriUtil;
import com.facebook.react.common.ReactConstants;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ProgressiveStringDecoder.kt */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0000\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/facebook/react/modules/network/ProgressiveStringDecoder;", "", "charset", "Ljava/nio/charset/Charset;", "<init>", "(Ljava/nio/charset/Charset;)V", "decoder", "Ljava/nio/charset/CharsetDecoder;", "remainder", "", "decodeNext", "", UriUtil.DATA_SCHEME, "initialLength", "", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ProgressiveStringDecoder {
    private static final String EMPTY_STRING = "";
    private final CharsetDecoder decoder;
    private byte[] remainder;

    public ProgressiveStringDecoder(Charset charset) {
        Intrinsics.checkNotNullParameter(charset, "charset");
        CharsetDecoder newDecoder = charset.newDecoder();
        Intrinsics.checkNotNullExpressionValue(newDecoder, "newDecoder(...)");
        this.decoder = newDecoder;
    }

    public final String decodeNext(byte[] data, int initialLength) {
        Intrinsics.checkNotNullParameter(data, "data");
        byte[] bArr = this.remainder;
        if (bArr != null) {
            byte[] bArr2 = new byte[bArr.length + initialLength];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            System.arraycopy(data, 0, bArr2, bArr.length, initialLength);
            initialLength += bArr.length;
            data = bArr2;
        }
        ByteBuffer wrap = ByteBuffer.wrap(data, 0, initialLength);
        boolean z = false;
        int i = 0;
        CharBuffer charBuffer = null;
        while (!z && i < 4) {
            try {
                charBuffer = this.decoder.decode(wrap);
                z = true;
            } catch (CharacterCodingException unused) {
                i++;
                wrap = ByteBuffer.wrap(data, 0, initialLength - i);
            }
        }
        if (z && i > 0) {
            byte[] bArr3 = new byte[i];
            System.arraycopy(data, initialLength - i, bArr3, 0, i);
            this.remainder = bArr3;
        } else {
            this.remainder = null;
        }
        if (!z) {
            FLog.w(ReactConstants.TAG, "failed to decode string from byte array");
            return "";
        }
        if (charBuffer == null) {
            return "";
        }
        char[] array = charBuffer.array();
        Intrinsics.checkNotNullExpressionValue(array, "array(...)");
        return new String(array, 0, charBuffer.length());
    }
}
