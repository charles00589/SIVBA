package com.facebook.soloader;

import android.content.Context;
import android.os.Parcel;
import android.os.StrictMode;
import com.facebook.soloader.ExtractFromZipSoSource;
import com.facebook.soloader.UnpackingSoSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/* loaded from: classes.dex */
public class BackupSoSource extends UnpackingSoSource implements RecoverableSoSource {
    private static final byte APK_SO_SOURCE_SIGNATURE_VERSION = 3;
    private static final byte LIBS_DIR_DOESNT_EXIST = 1;
    private static final byte LIBS_DIR_SNAPSHOT = 2;
    private static final String TAG = "BackupSoSource";
    private static final String ZIP_SEARCH_PATTERN = "^lib/([^/]+)/([^/]+\\.so)$";
    protected boolean mInitialized;
    private final ArrayList<ExtractFromZipSoSource> mZipSources;

    public BackupSoSource(Context context, String str, boolean z) {
        super(context, str, z);
        ArrayList<ExtractFromZipSoSource> arrayList = new ArrayList<>();
        this.mZipSources = arrayList;
        this.mInitialized = false;
        arrayList.add(new ExtractFromZipSoSource(context, str, new File(context.getApplicationInfo().sourceDir), ZIP_SEARCH_PATTERN));
        addBackupsFromSplitApks(context, str);
    }

    public BackupSoSource(Context context, String str) {
        this(context, str, true);
    }

    private void addBackupsFromSplitApks(Context context, String str) {
        if (context.getApplicationInfo().splitSourceDirs == null) {
            return;
        }
        try {
            for (String str2 : context.getApplicationInfo().splitSourceDirs) {
                ExtractFromZipSoSource extractFromZipSoSource = new ExtractFromZipSoSource(context, str, new File(str2), ZIP_SEARCH_PATTERN);
                if (extractFromZipSoSource.hasZippedLibs()) {
                    LogUtil.w(TAG, "adding backup source from split: " + extractFromZipSoSource.toString());
                    this.mZipSources.add(extractFromZipSoSource);
                }
            }
        } catch (IOException e) {
            LogUtil.w(TAG, "failed to read split apks", e);
        }
    }

    @Override // com.facebook.soloader.DirectorySoSource, com.facebook.soloader.SoSource
    public String getName() {
        return TAG;
    }

    @Override // com.facebook.soloader.UnpackingSoSource
    protected UnpackingSoSource.Unpacker makeUnpacker() throws IOException {
        return new ApkUnpacker();
    }

    @Override // com.facebook.soloader.DirectorySoSource, com.facebook.soloader.SoSource
    public int loadLibrary(String str, int i, StrictMode.ThreadPolicy threadPolicy) throws IOException {
        if (this.mInitialized) {
            return super.loadLibrary(str, i, threadPolicy);
        }
        return 0;
    }

    @Override // com.facebook.soloader.UnpackingSoSource, com.facebook.soloader.SoSource
    public void prepare(int i) throws IOException {
        if ((i & 8) != 0) {
            return;
        }
        super.prepare(i);
        this.mInitialized = true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x001a, code lost:
    
        com.facebook.soloader.LogUtil.e(com.facebook.soloader.SoLoader.TAG, "Found " + r9 + " in " + getName());
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x003e, code lost:
    
        r9 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean peekAndPrepareSoSource(String str, int i) throws IOException {
        boolean z;
        UnpackingSoSource.Unpacker makeUnpacker = makeUnpacker();
        try {
            UnpackingSoSource.Dso[] dsos = makeUnpacker.getDsos();
            int length = dsos.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    z = false;
                    break;
                }
                if (dsos[i2].name.equals(str)) {
                    break;
                }
                i2++;
            }
            if (makeUnpacker != null) {
                makeUnpacker.close();
            }
            if (!z) {
                return false;
            }
            LogUtil.e(SoLoader.TAG, "Preparing " + getName());
            prepare(i);
            return true;
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

    @Override // com.facebook.soloader.UnpackingSoSource
    public UnpackingSoSource.Dso[] getDsosBaseApk() throws IOException {
        UnpackingSoSource.Unpacker makeUnpacker = this.mZipSources.get(0).makeUnpacker();
        try {
            UnpackingSoSource.Dso[] dsos = makeUnpacker.getDsos();
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

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class ApkUnpacker extends UnpackingSoSource.Unpacker {
        protected ApkUnpacker() {
        }

        @Override // com.facebook.soloader.UnpackingSoSource.Unpacker
        public UnpackingSoSource.Dso[] getDsos() throws IOException {
            ArrayList arrayList = new ArrayList();
            Iterator it = BackupSoSource.this.mZipSources.iterator();
            while (it.hasNext()) {
                UnpackingSoSource.Unpacker makeUnpacker = ((ExtractFromZipSoSource) it.next()).makeUnpacker();
                try {
                    arrayList.addAll(Arrays.asList(makeUnpacker.getDsos()));
                    if (makeUnpacker != null) {
                        makeUnpacker.close();
                    }
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
            return (UnpackingSoSource.Dso[]) arrayList.toArray(new UnpackingSoSource.Dso[arrayList.size()]);
        }

        @Override // com.facebook.soloader.UnpackingSoSource.Unpacker
        public void unpack(File file) throws IOException {
            Iterator it = BackupSoSource.this.mZipSources.iterator();
            while (it.hasNext()) {
                ExtractFromZipSoSource.ZipUnpacker zipUnpacker = (ExtractFromZipSoSource.ZipUnpacker) ((ExtractFromZipSoSource) it.next()).makeUnpacker();
                try {
                    zipUnpacker.unpack(file);
                    if (zipUnpacker != null) {
                        zipUnpacker.close();
                    }
                } catch (Throwable th) {
                    if (zipUnpacker != null) {
                        try {
                            zipUnpacker.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.soloader.UnpackingSoSource
    public byte[] getDepsBlock() throws IOException {
        Parcel obtain = Parcel.obtain();
        try {
            obtain.writeByte(APK_SO_SOURCE_SIGNATURE_VERSION);
            obtain.writeInt(SysUtil.getAppVersionCode(this.mContext));
            obtain.writeInt(this.mZipSources.size());
            Iterator<ExtractFromZipSoSource> it = this.mZipSources.iterator();
            while (it.hasNext()) {
                obtain.writeByteArray(it.next().getDepsBlock());
            }
            String str = this.mContext.getApplicationInfo().sourceDir;
            if (str == null) {
                obtain.writeByte(LIBS_DIR_DOESNT_EXIST);
                return obtain.marshall();
            }
            File canonicalFile = new File(str).getCanonicalFile();
            if (!canonicalFile.exists()) {
                obtain.writeByte(LIBS_DIR_DOESNT_EXIST);
                return obtain.marshall();
            }
            obtain.writeByte(LIBS_DIR_SNAPSHOT);
            obtain.writeString(canonicalFile.getPath());
            obtain.writeLong(canonicalFile.lastModified());
            return obtain.marshall();
        } finally {
            obtain.recycle();
        }
    }

    @Override // com.facebook.soloader.RecoverableSoSource
    public SoSource recover(Context context) {
        BackupSoSource backupSoSource = new BackupSoSource(context, this.soDirectory.getName());
        try {
            backupSoSource.prepare(0);
            return backupSoSource;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.facebook.soloader.DirectorySoSource, com.facebook.soloader.SoSource
    public String toString() {
        String name;
        try {
            name = String.valueOf(this.soDirectory.getCanonicalPath());
        } catch (IOException unused) {
            name = this.soDirectory.getName();
        }
        return getName() + "[root = " + name + " flags = " + this.flags + " apks = " + this.mZipSources.toString() + "]";
    }
}
