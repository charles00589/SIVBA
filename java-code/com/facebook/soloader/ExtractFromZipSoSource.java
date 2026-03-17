package com.facebook.soloader;

import android.content.Context;
import com.facebook.soloader.UnpackingSoSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class ExtractFromZipSoSource extends UnpackingSoSource {
    private static final String TAG = "soloader.ExtractFromZipSoSource";
    protected final File mZipFileName;
    protected final String mZipSearchPattern;

    public ExtractFromZipSoSource(Context context, String str, File file, String str2) {
        super(context, str);
        this.mZipFileName = file;
        this.mZipSearchPattern = str2;
    }

    public ExtractFromZipSoSource(Context context, File file, File file2, String str) {
        super(context, file);
        this.mZipFileName = file2;
        this.mZipSearchPattern = str;
    }

    @Override // com.facebook.soloader.DirectorySoSource, com.facebook.soloader.SoSource
    public String getName() {
        return "ExtractFromZipSoSource";
    }

    public boolean hasZippedLibs() throws IOException {
        ZipUnpacker zipUnpacker = new ZipUnpacker(this);
        try {
            boolean z = zipUnpacker.computeDsosFromZip().length != 0;
            zipUnpacker.close();
            return z;
        } catch (Throwable th) {
            try {
                zipUnpacker.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.soloader.UnpackingSoSource
    public UnpackingSoSource.Unpacker makeUnpacker() throws IOException {
        return new ZipUnpacker(this);
    }

    /* loaded from: classes.dex */
    protected class ZipUnpacker extends UnpackingSoSource.Unpacker {

        @Nullable
        protected ZipDso[] mDsos;
        private final UnpackingSoSource mSoSource;
        private final ZipFile mZipFile;

        ZipUnpacker(UnpackingSoSource unpackingSoSource) throws IOException {
            this.mZipFile = new ZipFile(ExtractFromZipSoSource.this.mZipFileName);
            this.mSoSource = unpackingSoSource;
        }

        ZipDso[] computeDsosFromZip() {
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            HashMap hashMap = new HashMap();
            Pattern compile = Pattern.compile(ExtractFromZipSoSource.this.mZipSearchPattern);
            String[] supportedAbis = SysUtil.getSupportedAbis();
            Enumeration<? extends ZipEntry> entries = this.mZipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry nextElement = entries.nextElement();
                Matcher matcher = compile.matcher(nextElement.getName());
                if (matcher.matches()) {
                    int groupCount = matcher.groupCount();
                    String group = matcher.group(groupCount - 1);
                    String group2 = matcher.group(groupCount);
                    int findAbiScore = SysUtil.findAbiScore(supportedAbis, group);
                    if (findAbiScore >= 0) {
                        linkedHashSet.add(group);
                        ZipDso zipDso = (ZipDso) hashMap.get(group2);
                        if (zipDso == null || findAbiScore < zipDso.abiScore) {
                            hashMap.put(group2, new ZipDso(group2, nextElement, findAbiScore));
                        }
                    }
                }
            }
            this.mSoSource.setSoSourceAbis((String[]) linkedHashSet.toArray(new String[linkedHashSet.size()]));
            ZipDso[] zipDsoArr = (ZipDso[]) hashMap.values().toArray(new ZipDso[hashMap.size()]);
            Arrays.sort(zipDsoArr);
            return zipDsoArr;
        }

        ZipDso[] getExtractableDsosFromZip() {
            ZipDso[] zipDsoArr = this.mDsos;
            if (zipDsoArr != null) {
                return zipDsoArr;
            }
            ZipDso[] computeDsosFromZip = computeDsosFromZip();
            this.mDsos = computeDsosFromZip;
            return computeDsosFromZip;
        }

        @Override // com.facebook.soloader.UnpackingSoSource.Unpacker, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.mZipFile.close();
        }

        @Override // com.facebook.soloader.UnpackingSoSource.Unpacker
        public final UnpackingSoSource.Dso[] getDsos() throws IOException {
            return getExtractableDsosFromZip();
        }

        @Override // com.facebook.soloader.UnpackingSoSource.Unpacker
        public void unpack(File file) throws IOException {
            byte[] bArr = new byte[32768];
            for (ZipDso zipDso : getExtractableDsosFromZip()) {
                InputStream inputStream = this.mZipFile.getInputStream(zipDso.backingEntry);
                try {
                    UnpackingSoSource.InputDso inputDso = new UnpackingSoSource.InputDso(zipDso, inputStream);
                    inputStream = null;
                    try {
                        extractDso(inputDso, bArr, file);
                        inputDso.close();
                    } finally {
                    }
                } catch (Throwable th) {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    throw th;
                }
            }
        }
    }

    @Override // com.facebook.soloader.UnpackingSoSource
    protected String computeFileHash(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                CRC32 crc32 = new CRC32();
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        crc32.update(bArr, 0, read);
                    } else {
                        String valueOf = String.valueOf(crc32.getValue());
                        fileInputStream.close();
                        return valueOf;
                    }
                }
            } finally {
            }
        } catch (IOException e) {
            LogUtil.w(TAG, "Failed to compute file hash ", e);
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static final class ZipDso extends UnpackingSoSource.Dso implements Comparable<ZipDso> {
        final int abiScore;
        final ZipEntry backingEntry;

        ZipDso(String str, ZipEntry zipEntry, int i) {
            super(str, String.valueOf(zipEntry.getCrc()));
            this.backingEntry = zipEntry;
            this.abiScore = i;
        }

        @Override // java.lang.Comparable
        public int compareTo(ZipDso zipDso) {
            return this.name.compareTo(zipDso.name);
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ZipDso zipDso = (ZipDso) obj;
            return this.backingEntry.equals(zipDso.backingEntry) && this.abiScore == zipDso.abiScore;
        }

        public int hashCode() {
            return (this.abiScore * 31) + this.backingEntry.hashCode();
        }
    }

    @Override // com.facebook.soloader.DirectorySoSource, com.facebook.soloader.SoSource
    public String toString() {
        try {
            return this.mZipFileName.getCanonicalPath();
        } catch (IOException unused) {
            return this.mZipFileName.getName();
        }
    }
}
