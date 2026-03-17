package com.facebook.soloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class SoFileLoaderImpl implements SoFileLoader {
    private static final String TAG = "SoFileLoaderImpl";

    @Nullable
    private final Runtime mRuntime = null;

    @Nullable
    private final Method mNativeLoadRuntimeMethod = null;

    @Nullable
    private final String mLocalLdLibraryPath = null;

    @Nullable
    private final String mLocalLdLibraryPathNoZips = null;

    @Override // com.facebook.soloader.SoFileLoader
    public void loadBytes(String str, ElfByteChannel elfByteChannel, int i) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x002e, code lost:
    
        if (r3 == null) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0030, code lost:
    
        com.facebook.soloader.LogUtil.e(com.facebook.soloader.SoFileLoaderImpl.TAG, "Error when loading library: " + r3 + ", library hash is " + getLibHash(r8) + ", LD_LIBRARY_PATH is " + r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x005c, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:?, code lost:
    
        return;
     */
    @Override // com.facebook.soloader.SoFileLoader
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void load(String str, int i) {
        if (this.mNativeLoadRuntimeMethod == null) {
            System.load(str);
            return;
        }
        String str2 = (i & 4) == 4 ? this.mLocalLdLibraryPath : this.mLocalLdLibraryPathNoZips;
        try {
            try {
                synchronized (this.mRuntime) {
                    try {
                        String str3 = (String) this.mNativeLoadRuntimeMethod.invoke(this.mRuntime, str, SoLoader.class.getClassLoader(), str2);
                        try {
                            if (str3 != null) {
                                try {
                                    throw new SoLoaderULError(str, "nativeLoad() returned error for " + str + ": " + str3);
                                } catch (Throwable th) {
                                    th = th;
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                    }
                }
                throw th;
            } catch (Throwable th4) {
                if (0 != 0) {
                    LogUtil.e(TAG, "Error when loading library: " + ((String) null) + ", library hash is " + getLibHash(str) + ", LD_LIBRARY_PATH is " + str2);
                }
                throw th4;
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("nativeLoad() error during invocation for " + str + ": " + e);
        }
    }

    private String getLibHash(String str) {
        try {
            File file = new File(str);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read > 0) {
                        messageDigest.update(bArr, 0, read);
                    } else {
                        String format = String.format("%32x", new BigInteger(1, messageDigest.digest()));
                        fileInputStream.close();
                        return format;
                    }
                }
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException | SecurityException | NoSuchAlgorithmException e) {
            return e.toString();
        }
    }
}
