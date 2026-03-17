package com.facebook.soloader.recovery;

import com.facebook.soloader.BackupSoSource;
import com.facebook.soloader.LogUtil;
import com.facebook.soloader.SoLoader;
import com.facebook.soloader.SoLoaderDSONotFoundError;
import com.facebook.soloader.SoLoaderULError;
import com.facebook.soloader.SoSource;
import java.io.IOException;

/* loaded from: classes.dex */
public class ReunpackBackupSoSources implements RecoveryStrategy {
    private int mRecoveryFlags;

    public ReunpackBackupSoSources() {
        this(0);
    }

    public ReunpackBackupSoSources(int i) {
        this.mRecoveryFlags = i;
    }

    @Override // com.facebook.soloader.recovery.RecoveryStrategy
    public boolean recover(UnsatisfiedLinkError unsatisfiedLinkError, SoSource[] soSourceArr) {
        if (!(unsatisfiedLinkError instanceof SoLoaderULError)) {
            return false;
        }
        SoLoaderULError soLoaderULError = (SoLoaderULError) unsatisfiedLinkError;
        String soName = soLoaderULError.getSoName();
        String message = soLoaderULError.getMessage();
        if (soName == null) {
            LogUtil.e(SoLoader.TAG, "No so name provided in ULE, cannot recover");
            return false;
        }
        if (soLoaderULError instanceof SoLoaderDSONotFoundError) {
            if ((this.mRecoveryFlags & 1) == 0) {
                return false;
            }
            logRecovery(soLoaderULError, soName);
            return recoverDSONotFoundError(soSourceArr, soName, 0);
        }
        if (message == null || !(message.contains("/app/") || message.contains("/mnt/"))) {
            return false;
        }
        logRecovery(soLoaderULError, soName);
        return lazyPrepareBackupSoSource(soSourceArr, soName);
    }

    private boolean recoverDSONotFoundError(SoSource[] soSourceArr, String str, int i) {
        try {
            for (SoSource soSource : soSourceArr) {
                if ((soSource instanceof BackupSoSource) && ((BackupSoSource) soSource).peekAndPrepareSoSource(str, i)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            LogUtil.e(SoLoader.TAG, "Failed to run recovery for backup so source due to: " + e);
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x002f, code lost:
    
        r8 = r7.length;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0030, code lost:
    
        if (r2 >= r8) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0032, code lost:
    
        r0 = r7[r2];
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0036, code lost:
    
        if ((r0 instanceof com.facebook.soloader.DirectorySoSource) != false) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x003b, code lost:
    
        if ((r0 instanceof com.facebook.soloader.BackupSoSource) == false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x003e, code lost:
    
        ((com.facebook.soloader.DirectorySoSource) r0).setExplicitDependencyResolution();
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0043, code lost:
    
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0046, code lost:
    
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0048, code lost:
    
        r7 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0049, code lost:
    
        com.facebook.soloader.LogUtil.e(com.facebook.soloader.SoLoader.TAG, "Encountered an exception while reunpacking BackupSoSource " + r4.getName() + " for library " + r8 + ": ", r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x006f, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0010, code lost:
    
        r4 = (com.facebook.soloader.BackupSoSource) r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0012, code lost:
    
        com.facebook.soloader.LogUtil.e(com.facebook.soloader.SoLoader.TAG, "Preparing BackupSoSource for the first time " + r4.getName());
        r4.prepare(0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean lazyPrepareBackupSoSource(SoSource[] soSourceArr, String str) {
        int length = soSourceArr.length;
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                break;
            }
            SoSource soSource = soSourceArr[i2];
            if (soSource instanceof BackupSoSource) {
                break;
            }
            i2++;
        }
    }

    private void logRecovery(Error error, String str) {
        LogUtil.e(SoLoader.TAG, "Reunpacking BackupSoSources due to " + error + ", retrying for specific library " + str);
    }
}
