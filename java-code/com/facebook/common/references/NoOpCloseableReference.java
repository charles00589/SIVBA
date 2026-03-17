package com.facebook.common.references;

/* loaded from: classes.dex */
public class NoOpCloseableReference<T> extends CloseableReference<T> {
    @Override // com.facebook.common.references.CloseableReference
    /* renamed from: clone */
    public CloseableReference<T> mo90clone() {
        return this;
    }

    @Override // com.facebook.common.references.CloseableReference
    public CloseableReference<T> cloneOrNull() {
        return this;
    }

    @Override // com.facebook.common.references.CloseableReference, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    @Override // com.facebook.common.references.CloseableReference
    public boolean isValid() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NoOpCloseableReference(T t) {
        super(t, null, null, null, false);
    }
}
