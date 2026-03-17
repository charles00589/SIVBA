package com.facebook.soloader;

import android.content.Context;
import android.os.Parcel;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.SyncFailedException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class UnpackingSoSource extends DirectorySoSource implements AsyncInitSoSource {
    protected static final String DEPS_FILE_NAME = "dso_deps";
    protected static final String LOCK_FILE_NAME = "dso_lock";
    private static final byte STATE_CLEAN = 1;
    private static final byte STATE_DIRTY = 0;
    protected static final String STATE_FILE_NAME = "dso_state";
    private static final String TAG = "fb-UnpackingSoSource";

    @Nullable
    private String[] mAbis;
    protected final Context mContext;

    private static boolean forceRefresh(int i) {
        return (i & 2) != 0;
    }

    private static boolean rewriteStateAsync(int i) {
        return (i & 1) != 0;
    }

    protected abstract Unpacker makeUnpacker() throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public UnpackingSoSource(Context context, String str, boolean z) {
        super(getSoStorePath(context, str), z ? 1 : 0);
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UnpackingSoSource(Context context, String str) {
        this(context, str, true);
    }

    protected UnpackingSoSource(Context context, File file, boolean z) {
        super(file, z ? 1 : 0);
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UnpackingSoSource(Context context, File file) {
        this(context, file, true);
    }

    public static File getSoStorePath(Context context, String str) {
        return new File(context.getApplicationInfo().dataDir + "/" + str);
    }

    @Override // com.facebook.soloader.SoSource
    public String[] getSoSourceAbis() {
        String[] strArr = this.mAbis;
        return strArr == null ? super.getSoSourceAbis() : strArr;
    }

    public Dso[] getDsosBaseApk() throws IOException {
        Unpacker makeUnpacker = makeUnpacker();
        try {
            Dso[] dsos = makeUnpacker.getDsos();
            if (makeUnpacker != null) {
                makeUnpacker.close();
            }
            return dsos;
        } catch (Throwable th) {
            if (makeUnpacker != null) {
                try {
                    makeUnpacker.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void setSoSourceAbis(String[] strArr) {
        this.mAbis = strArr;
    }

    /* loaded from: classes.dex */
    public static class Dso {
        public final String hash;
        public final String name;

        public Dso(String str, String str2) {
            this.name = str;
            this.hash = str2;
        }
    }

    /* loaded from: classes.dex */
    protected static final class InputDso implements Closeable {
        private final InputStream content;
        private final Dso dso;

        public InputDso(Dso dso, InputStream inputStream) {
            this.dso = dso;
            this.content = inputStream;
        }

        public Dso getDso() {
            return this.dso;
        }

        public int available() throws IOException {
            return this.content.available();
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.content.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void writeState(File file, byte b, boolean z) throws IOException {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            try {
                randomAccessFile.seek(0L);
                randomAccessFile.write(b);
                randomAccessFile.setLength(randomAccessFile.getFilePointer());
                if (z) {
                    randomAccessFile.getFD().sync();
                }
                randomAccessFile.close();
            } finally {
            }
        } catch (SyncFailedException e) {
            LogUtil.w(TAG, "state file sync failed", e);
        }
    }

    private void deleteSoFiles() throws IOException {
        File[] listFiles = this.soDirectory.listFiles(new FilenameFilter() { // from class: com.facebook.soloader.UnpackingSoSource.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file, String str) {
                return (str.equals(UnpackingSoSource.STATE_FILE_NAME) || str.equals(UnpackingSoSource.LOCK_FILE_NAME) || str.equals(UnpackingSoSource.DEPS_FILE_NAME)) ? false : true;
            }
        });
        if (listFiles == null) {
            throw new IOException("unable to list directory " + this.soDirectory);
        }
        for (File file : listFiles) {
            LogUtil.v(TAG, "Deleting " + file);
            SysUtil.dumbDelete(file);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static abstract class Unpacker implements Closeable {
        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }

        public abstract Dso[] getDsos() throws IOException;

        public abstract void unpack(File file) throws IOException;

        public void extractDso(InputDso inputDso, byte[] bArr, File file) throws IOException {
            RandomAccessFile randomAccessFile;
            Throwable th;
            RandomAccessFile randomAccessFile2;
            LogUtil.i(UnpackingSoSource.TAG, "extracting DSO " + inputDso.getDso().name);
            File file2 = new File(file, inputDso.getDso().name);
            try {
                try {
                    RandomAccessFile randomAccessFile3 = new RandomAccessFile(file2, "rw");
                    try {
                        int available = inputDso.available();
                        if (available > 1) {
                            try {
                                randomAccessFile2 = randomAccessFile3;
                                try {
                                    SysUtil.fallocateIfSupported(randomAccessFile3.getFD(), available);
                                } catch (Throwable th2) {
                                    th = th2;
                                    th = th;
                                    randomAccessFile = randomAccessFile2;
                                    try {
                                        randomAccessFile.close();
                                        throw th;
                                    } catch (Throwable th3) {
                                        th.addSuppressed(th3);
                                        throw th;
                                    }
                                }
                            } catch (Throwable th4) {
                                th = th4;
                                randomAccessFile2 = randomAccessFile3;
                            }
                        } else {
                            randomAccessFile2 = randomAccessFile3;
                        }
                        try {
                            randomAccessFile = randomAccessFile2;
                            try {
                                SysUtil.copyBytes(randomAccessFile, inputDso.content, Integer.MAX_VALUE, bArr);
                                randomAccessFile.setLength(randomAccessFile.getFilePointer());
                                if (!file2.setExecutable(true, false)) {
                                    throw new IOException("cannot make file executable: " + file2);
                                }
                                randomAccessFile.close();
                                if (!file2.exists() || file2.setWritable(false)) {
                                    return;
                                }
                                LogUtil.e(SoLoader.TAG, "Error removing " + file2 + " write permission from directory " + file + " (writable: " + file.canWrite() + ")");
                            } catch (Throwable th5) {
                                th = th5;
                                th = th;
                                randomAccessFile.close();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            randomAccessFile = randomAccessFile2;
                        }
                    } catch (Throwable th7) {
                        th = th7;
                        randomAccessFile = randomAccessFile3;
                    }
                } catch (IOException e) {
                    LogUtil.e(UnpackingSoSource.TAG, "error extracting dso  " + file2 + " due to: " + e);
                    SysUtil.dumbDelete(file2);
                    throw e;
                }
            } catch (Throwable th8) {
                if (file2.exists() && !file2.setWritable(false)) {
                    LogUtil.e(SoLoader.TAG, "Error removing " + file2 + " write permission from directory " + file + " (writable: " + file.canWrite() + ")");
                }
                throw th8;
            }
        }
    }

    protected MessageDigest getHashingAlgorithm() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }

    protected String computeFileHash(File file) {
        try {
            MessageDigest hashingAlgorithm = getHashingAlgorithm();
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, hashingAlgorithm);
                    try {
                        do {
                        } while (digestInputStream.read(new byte[8192]) != -1);
                        byte[] digest = hashingAlgorithm.digest();
                        StringBuilder sb = new StringBuilder(digest.length * 2);
                        for (byte b : digest) {
                            sb.append(String.format("%02x", Byte.valueOf(b)));
                        }
                        String sb2 = sb.toString();
                        digestInputStream.close();
                        fileInputStream.close();
                        return sb2;
                    } finally {
                    }
                } finally {
                }
            } catch (IOException e) {
                LogUtil.w(TAG, "Failed to calculate hash for " + file.getName(), e);
                return "-1";
            }
        } catch (NoSuchAlgorithmException e2) {
            LogUtil.w(TAG, "Failed to calculate hash for " + file.getName(), e2);
            return "-1";
        }
    }

    private boolean libraryIsCorrupted(Dso dso, File file) {
        return (file.exists() && dso.hash.equals(computeFileHash(file))) ? false : true;
    }

    protected boolean depsChanged(byte[] bArr, byte[] bArr2) {
        return !Arrays.equals(bArr, bArr2);
    }

    protected boolean depsChanged(byte[] bArr) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(this.soDirectory, DEPS_FILE_NAME), "rw");
            try {
                if (randomAccessFile.length() != 0) {
                    int length = (int) randomAccessFile.length();
                    byte[] bArr2 = new byte[length];
                    if (randomAccessFile.read(bArr2) != length) {
                        LogUtil.v(TAG, "short read of so store deps file: marking unclean");
                        randomAccessFile.close();
                        return true;
                    }
                    boolean depsChanged = depsChanged(bArr2, bArr);
                    randomAccessFile.close();
                    return depsChanged;
                }
                randomAccessFile.close();
                return true;
            } finally {
            }
        } catch (IOException e) {
            LogUtil.w(TAG, "failed to compare whether deps changed", e);
            return true;
        }
    }

    private boolean refreshLocked(final FileLocker fileLocker, int i) throws IOException {
        byte b;
        RandomAccessFile randomAccessFile;
        final File file = new File(this.soDirectory, STATE_FILE_NAME);
        byte[] depsBlock = getDepsBlock();
        if (forceRefresh(i) || depsChanged(depsBlock)) {
            b = 0;
        } else {
            randomAccessFile = new RandomAccessFile(file, "rw");
            try {
                if (randomAccessFile.length() == 1) {
                    try {
                        b = randomAccessFile.readByte();
                    } catch (IOException e) {
                        LogUtil.v(TAG, "dso store " + this.soDirectory + " regeneration interrupted: " + e.getMessage());
                    }
                    if (b == 1) {
                        LogUtil.v(TAG, "dso store " + this.soDirectory + " regeneration not needed: state file clean");
                        randomAccessFile.close();
                    }
                }
                b = 0;
                randomAccessFile.close();
            } catch (Throwable th) {
                throw th;
            }
        }
        if (b == 1) {
            return false;
        }
        final boolean z = (i & 4) == 0;
        LogUtil.v(TAG, "so store dirty: regenerating");
        writeState(file, (byte) 0, z);
        deleteSoFiles();
        Unpacker makeUnpacker = makeUnpacker();
        try {
            makeUnpacker.unpack(this.soDirectory);
            if (makeUnpacker != null) {
                makeUnpacker.close();
            }
            randomAccessFile = new RandomAccessFile(new File(this.soDirectory, DEPS_FILE_NAME), "rw");
            try {
                randomAccessFile.write(depsBlock);
                randomAccessFile.setLength(randomAccessFile.getFilePointer());
                randomAccessFile.close();
                Runnable runnable = new Runnable() { // from class: com.facebook.soloader.UnpackingSoSource.2
                    @Override // java.lang.Runnable
                    public void run() {
                        LogUtil.v(UnpackingSoSource.TAG, "starting syncer worker");
                        try {
                            try {
                                if (z) {
                                    SysUtil.fsyncAll(UnpackingSoSource.this.soDirectory);
                                }
                                UnpackingSoSource.writeState(file, UnpackingSoSource.STATE_CLEAN, z);
                            } finally {
                                LogUtil.v(UnpackingSoSource.TAG, "releasing dso store lock for " + UnpackingSoSource.this.soDirectory + " (from syncer thread)");
                                fileLocker.close();
                            }
                        } catch (IOException e2) {
                            throw new RuntimeException(e2);
                        }
                    }
                };
                if (rewriteStateAsync(i)) {
                    new Thread(runnable, "SoSync:" + this.soDirectory.getName()).start();
                } else {
                    runnable.run();
                }
                return true;
            } finally {
                try {
                    randomAccessFile.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
        } catch (Throwable th3) {
            if (makeUnpacker != null) {
                try {
                    makeUnpacker.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
            }
            throw th3;
        }
    }

    @Override // com.facebook.soloader.AsyncInitSoSource
    public void waitUntilInitCompleted() {
        try {
            FileLocker orCreateLockOnDir = SysUtil.getOrCreateLockOnDir(this.soDirectory, new File(this.soDirectory, LOCK_FILE_NAME));
            if (orCreateLockOnDir != null) {
                orCreateLockOnDir.close();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Encountered exception during wait for unpacking trying to acquire file lock for " + getClass().getName() + " (" + this.soDirectory + "): ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] getDepsBlock() throws IOException {
        Parcel obtain = Parcel.obtain();
        Unpacker makeUnpacker = makeUnpacker();
        try {
            Dso[] dsos = makeUnpacker.getDsos();
            obtain.writeInt(dsos.length);
            for (Dso dso : dsos) {
                obtain.writeString(dso.name);
                obtain.writeString(dso.hash);
            }
            if (makeUnpacker != null) {
                makeUnpacker.close();
            }
            byte[] marshall = obtain.marshall();
            obtain.recycle();
            return marshall;
        } catch (Throwable th) {
            if (makeUnpacker != null) {
                try {
                    makeUnpacker.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // com.facebook.soloader.SoSource
    public void prepare(int i) throws IOException {
        SysUtil.mkdirOrThrow(this.soDirectory);
        if (!this.soDirectory.canWrite() && !this.soDirectory.setWritable(true)) {
            throw new IOException("error adding " + this.soDirectory.getCanonicalPath() + " write permission");
        }
        FileLocker fileLocker = null;
        try {
            try {
                FileLocker orCreateLockOnDir = SysUtil.getOrCreateLockOnDir(this.soDirectory, new File(this.soDirectory, LOCK_FILE_NAME));
                try {
                    LogUtil.v(TAG, "locked dso store " + this.soDirectory);
                    if (!this.soDirectory.canWrite() && !this.soDirectory.setWritable(true)) {
                        throw new IOException("error adding " + this.soDirectory.getCanonicalPath() + " write permission");
                    }
                    if (!refreshLocked(orCreateLockOnDir, i)) {
                        LogUtil.i(TAG, "dso store is up-to-date: " + this.soDirectory);
                        fileLocker = orCreateLockOnDir;
                    }
                    if (fileLocker == null) {
                        LogUtil.v(TAG, "not releasing dso store lock for " + this.soDirectory + " (syncer thread started)");
                    } else {
                        LogUtil.v(TAG, "releasing dso store lock for " + this.soDirectory);
                        fileLocker.close();
                    }
                    if (this.soDirectory.canWrite() && !this.soDirectory.setWritable(false)) {
                        throw new IOException("error removing " + this.soDirectory.getCanonicalPath() + " write permission");
                    }
                } catch (Throwable th) {
                    th = th;
                    fileLocker = orCreateLockOnDir;
                    if (fileLocker == null) {
                        LogUtil.v(TAG, "not releasing dso store lock for " + this.soDirectory + " (syncer thread started)");
                    } else {
                        LogUtil.v(TAG, "releasing dso store lock for " + this.soDirectory);
                        fileLocker.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                if (this.soDirectory.canWrite() && !this.soDirectory.setWritable(false)) {
                    throw new IOException("error removing " + this.soDirectory.getCanonicalPath() + " write permission");
                }
                throw th2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    @Override // com.facebook.soloader.DirectorySoSource, com.facebook.soloader.SoSource
    @Nullable
    public String getLibraryPath(String str) throws IOException {
        File soFileByName = getSoFileByName(str);
        if (soFileByName == null) {
            return null;
        }
        return soFileByName.getCanonicalPath();
    }
}
