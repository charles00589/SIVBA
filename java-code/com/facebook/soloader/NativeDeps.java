package com.facebook.soloader;

import android.content.Context;
import android.os.StrictMode;
import com.facebook.soloader.MinElf;
import com.facebook.soloader.observer.ObserverHolder;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public final class NativeDeps {
    private static final float HASHMAP_LOAD_FACTOR = 1.0f;
    private static final int INITIAL_HASH = 5381;
    private static final int LIB_PREFIX_LEN;
    private static final int LIB_PREFIX_SUFFIX_LEN;
    private static final int LIB_SUFFIX_LEN;
    private static final String LOG_TAG = "NativeDeps";
    private static final HashSet<String> STANDARD_SYSTEM_LIBS;
    private static final int WAITING_THREADS_WARNING_THRESHOLD = 3;

    @Nullable
    private static byte[] sEncodedDeps;
    private static volatile boolean sInitialized;
    private static Map<Integer, List<Integer>> sPrecomputedDeps;
    private static List<Integer> sPrecomputedLibs;
    private static volatile boolean sUseDepsFileAsync;
    private static final ReentrantReadWriteLock sWaitForDepsFileLock;

    static {
        int length = "lib".length();
        LIB_PREFIX_LEN = length;
        int length2 = ".so".length();
        LIB_SUFFIX_LEN = length2;
        LIB_PREFIX_SUFFIX_LEN = length + length2;
        sInitialized = false;
        sUseDepsFileAsync = false;
        sWaitForDepsFileLock = new ReentrantReadWriteLock();
        STANDARD_SYSTEM_LIBS = new HashSet<String>() { // from class: com.facebook.soloader.NativeDeps.1
            {
                add("libEGL.so");
                add("libGLESv2.so");
                add("libGLESv3.so");
                add("libOpenSLES.so");
                add("libandroid.so");
                add("libc.so");
                add("libdl.so");
                add("libjnigraphics.so");
                add("liblog.so");
                add("libm.so");
                add("libstdc++.so");
                add("libz.so");
            }
        };
    }

    public static void loadDependencies(String str, ElfByteChannel elfByteChannel, int i, StrictMode.ThreadPolicy threadPolicy) throws IOException {
        String[] dependencies = getDependencies(str, elfByteChannel);
        LogUtil.d(SoLoader.TAG, "Loading " + str + "'s dependencies: " + Arrays.toString(dependencies));
        for (String str2 : dependencies) {
            if (!str2.startsWith("/") && !STANDARD_SYSTEM_LIBS.contains(str2)) {
                SoLoader.loadDependency(str2, i, threadPolicy);
            }
        }
    }

    public static String[] getDependencies(String str, ElfByteChannel elfByteChannel) throws IOException {
        if (SoLoader.SYSTRACE_LIBRARY_LOADING) {
            Api18TraceUtils.beginTraceSection("soloader.NativeDeps.getDependencies[", str, "]");
        }
        ObserverHolder.onGetDependenciesStart();
        try {
            try {
                String[] awaitGetDepsFromPrecomputedDeps = awaitGetDepsFromPrecomputedDeps(str);
                if (awaitGetDepsFromPrecomputedDeps != null) {
                    return awaitGetDepsFromPrecomputedDeps;
                }
                String[] extract_DT_NEEDED = MinElf.extract_DT_NEEDED(elfByteChannel);
                ObserverHolder.onGetDependenciesEnd(null);
                if (SoLoader.SYSTRACE_LIBRARY_LOADING) {
                    Api18TraceUtils.endSection();
                }
                return extract_DT_NEEDED;
            } catch (MinElf.ElfError e) {
                throw SoLoaderULErrorFactory.create(str, e);
            } catch (Error e2) {
                e = e2;
                throw e;
            } catch (RuntimeException e3) {
                e = e3;
                throw e;
            }
        } finally {
            ObserverHolder.onGetDependenciesEnd(null);
            if (SoLoader.SYSTRACE_LIBRARY_LOADING) {
                Api18TraceUtils.endSection();
            }
        }
    }

    @Nullable
    private static String[] awaitGetDepsFromPrecomputedDeps(String str) {
        if (sInitialized) {
            return tryGetDepsFromPrecomputedDeps(str);
        }
        if (!sUseDepsFileAsync) {
            return null;
        }
        ReentrantReadWriteLock reentrantReadWriteLock = sWaitForDepsFileLock;
        reentrantReadWriteLock.readLock().lock();
        try {
            String[] tryGetDepsFromPrecomputedDeps = tryGetDepsFromPrecomputedDeps(str);
            reentrantReadWriteLock.readLock().unlock();
            return tryGetDepsFromPrecomputedDeps;
        } catch (Throwable th) {
            sWaitForDepsFileLock.readLock().unlock();
            throw th;
        }
    }

    public static boolean useDepsFile(final Context context, boolean z, final boolean z2) {
        if (!z) {
            return useDepsFileFromApkSync(context, z2);
        }
        new Thread(new Runnable() { // from class: com.facebook.soloader.NativeDeps.2
            @Override // java.lang.Runnable
            public void run() {
                NativeDeps.sWaitForDepsFileLock.writeLock().lock();
                boolean unused = NativeDeps.sUseDepsFileAsync = true;
                try {
                    NativeDeps.useDepsFileFromApkSync(context, z2);
                } finally {
                    int readLockCount = NativeDeps.sWaitForDepsFileLock.getReadLockCount();
                    if (readLockCount >= 3) {
                        LogUtil.w(NativeDeps.LOG_TAG, "NativeDeps initialization finished with " + Integer.toString(readLockCount) + " threads waiting.");
                    }
                    NativeDeps.sWaitForDepsFileLock.writeLock().unlock();
                    boolean unused2 = NativeDeps.sUseDepsFileAsync = false;
                }
            }
        }, "soloader-nativedeps-init").start();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean useDepsFileFromApkSync(Context context, boolean z) {
        boolean z2;
        try {
            z2 = initDeps(context, z);
        } catch (IOException unused) {
            z2 = false;
        }
        if (!z2 && z) {
            try {
                NativeDepsUnpacker.ensureNativeDepsAvailable(context);
                z2 = initDeps(context, z);
            } catch (IOException unused2) {
            }
        }
        if (!z2) {
            LogUtil.w(LOG_TAG, "Failed to extract native deps from APK, falling back to using MinElf to get library dependencies.");
        }
        return z2;
    }

    private static boolean initDeps(Context context, boolean z) throws IOException {
        byte[] readNativeDepsFromApk;
        byte[] bArr;
        verifyUninitialized();
        if (z) {
            bArr = SysUtil.makeApkDepBlock(new File(context.getApplicationInfo().sourceDir), context);
            readNativeDepsFromApk = NativeDepsUnpacker.readNativeDepsFromDisk(context);
        } else {
            readNativeDepsFromApk = NativeDepsUnpacker.readNativeDepsFromApk(context);
            bArr = null;
        }
        return processDepsBytes(bArr, readNativeDepsFromApk);
    }

    private static void indexLib(int i, int i2) {
        sPrecomputedLibs.add(Integer.valueOf(i2));
        List<Integer> list = sPrecomputedDeps.get(Integer.valueOf(i));
        if (list == null) {
            list = new ArrayList<>();
            sPrecomputedDeps.put(Integer.valueOf(i), list);
        }
        list.add(Integer.valueOf(i2));
    }

    private static void indexDepsBytes(byte[] bArr, int i) {
        int i2;
        byte b;
        boolean z = true;
        int i3 = 0;
        int i4 = 0;
        while (true) {
            if (z) {
                i3 = INITIAL_HASH;
                i2 = i;
                while (true) {
                    try {
                        b = bArr[i2];
                        if (b <= 32) {
                            break;
                        }
                        i3 = (i3 << 5) + i3 + b;
                        i2++;
                    } catch (IndexOutOfBoundsException unused) {
                        if (z) {
                            return;
                        } else {
                            return;
                        }
                    }
                }
                indexLib(i3, i);
                z = b != 32;
            } else {
                while (bArr[i] != 10) {
                    try {
                        i++;
                    } catch (IndexOutOfBoundsException unused2) {
                        i = i4;
                        if (z || i == bArr.length) {
                            return;
                        }
                        indexLib(i3, i);
                        return;
                    }
                }
                z = true;
                int i5 = i4;
                i2 = i;
                i = i5;
            }
            int i6 = i2 + 1;
            i4 = i;
            i = i6;
        }
    }

    private static int verifyBytesAndGetOffset(@Nullable byte[] bArr, @Nullable byte[] bArr2) {
        if (bArr == null || bArr.length == 0 || bArr2.length < bArr.length + 4) {
            return -1;
        }
        if (bArr2.length != bArr.length + 4 + ByteBuffer.wrap(bArr2, bArr.length, 4).getInt()) {
            return -1;
        }
        for (int i = 0; i < bArr.length; i++) {
            if (bArr[i] != bArr2[i]) {
                return -1;
            }
        }
        return bArr.length + 4;
    }

    private static int findNextLine(byte[] bArr, int i) {
        while (i < bArr.length && bArr[i] != 10) {
            i++;
        }
        return i < bArr.length ? i + 1 : i;
    }

    private static int parseLibCount(byte[] bArr, int i, int i2) {
        try {
            return Integer.parseInt(new String(bArr, i, i2));
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    static boolean processDepsBytes(byte[] bArr, byte[] bArr2) throws IOException {
        int i;
        int parseLibCount;
        if (bArr != null) {
            i = verifyBytesAndGetOffset(bArr, bArr2);
            if (i == -1) {
                return false;
            }
        } else {
            i = 0;
        }
        int findNextLine = findNextLine(bArr2, i);
        if (findNextLine >= bArr2.length || (parseLibCount = parseLibCount(bArr2, i, (findNextLine - i) - 1)) <= 0) {
            return false;
        }
        sPrecomputedDeps = new HashMap(((int) (parseLibCount / HASHMAP_LOAD_FACTOR)) + 1, HASHMAP_LOAD_FACTOR);
        sPrecomputedLibs = new ArrayList(parseLibCount);
        indexDepsBytes(bArr2, findNextLine);
        if (sPrecomputedLibs.size() != parseLibCount) {
            return false;
        }
        sEncodedDeps = bArr2;
        sInitialized = true;
        return true;
    }

    private static boolean libIsAtOffset(String str, int i) {
        int i2;
        int i3 = LIB_PREFIX_LEN;
        while (true) {
            int length = str.length();
            i2 = LIB_SUFFIX_LEN;
            if (i3 >= length - i2 || i >= sEncodedDeps.length || (str.codePointAt(i3) & 255) != sEncodedDeps[i]) {
                break;
            }
            i3++;
            i++;
        }
        return i3 == str.length() - i2;
    }

    private static int hashLib(String str) {
        int i = INITIAL_HASH;
        for (int i2 = LIB_PREFIX_LEN; i2 < str.length() - LIB_SUFFIX_LEN; i2++) {
            i = str.codePointAt(i2) + (i << 5) + i;
        }
        return i;
    }

    private static int getOffsetForLib(String str) {
        List<Integer> list = sPrecomputedDeps.get(Integer.valueOf(hashLib(str)));
        if (list == null) {
            return -1;
        }
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            if (libIsAtOffset(str, intValue)) {
                return intValue;
            }
        }
        return -1;
    }

    @Nullable
    private static String getLibString(int i) {
        if (i >= sPrecomputedLibs.size()) {
            return null;
        }
        int intValue = sPrecomputedLibs.get(i).intValue();
        int i2 = intValue;
        while (true) {
            byte[] bArr = sEncodedDeps;
            if (i2 >= bArr.length || bArr[i2] <= 32) {
                break;
            }
            i2++;
        }
        int i3 = (i2 - intValue) + LIB_PREFIX_SUFFIX_LEN;
        char[] cArr = new char[i3];
        cArr[0] = 'l';
        cArr[1] = 'i';
        cArr[2] = 'b';
        for (int i4 = 0; i4 < i3 - LIB_PREFIX_SUFFIX_LEN; i4++) {
            cArr[LIB_PREFIX_LEN + i4] = (char) sEncodedDeps[intValue + i4];
        }
        cArr[i3 - 3] = '.';
        cArr[i3 - 2] = 's';
        cArr[i3 - 1] = 'o';
        return new String(cArr);
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x003d, code lost:
    
        return null;
     */
    @Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static String[] getDepsForLibAtOffset(int i, int i2) {
        byte b;
        ArrayList arrayList = new ArrayList();
        int i3 = (i + i2) - LIB_PREFIX_SUFFIX_LEN;
        int i4 = 0;
        boolean z = false;
        while (true) {
            byte[] bArr = sEncodedDeps;
            if (i3 >= bArr.length || (b = bArr[i3]) == 10) {
                break;
            }
            if (b == 32) {
                if (z) {
                    String libString = getLibString(i4);
                    if (libString == null) {
                        return null;
                    }
                    arrayList.add(libString);
                    i4 = 0;
                    z = false;
                } else {
                    continue;
                }
            } else {
                if (b < 48 || b > 57) {
                    break;
                }
                i4 = (i4 * 10) + (b - 48);
                z = true;
            }
            i3++;
        }
        if (z) {
            String libString2 = getLibString(i4);
            if (libString2 == null) {
                return null;
            }
            arrayList.add(libString2);
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    @Nullable
    static String[] tryGetDepsFromPrecomputedDeps(String str) {
        int offsetForLib;
        if (sInitialized && str.length() > LIB_PREFIX_SUFFIX_LEN && (offsetForLib = getOffsetForLib(str)) != -1) {
            return getDepsForLibAtOffset(offsetForLib, str.length());
        }
        return null;
    }

    private static void verifyUninitialized() {
        if (sInitialized) {
            synchronized (NativeDeps.class) {
                if (sInitialized) {
                    throw new IllegalStateException("Trying to initialize NativeDeps but it was already initialized");
                }
            }
        }
    }
}
