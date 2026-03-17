package com.facebook.soloader;

import com.facebook.soloader.MinElf;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.UShort;

/* loaded from: classes.dex */
public class Manifest {
    public final String arch;
    public final List<String> libs;

    Manifest(String str, List<String> list) {
        this.arch = str;
        this.libs = Collections.unmodifiableList(list);
    }

    public static Manifest read(InputStream inputStream) throws IOException {
        return read(new DataInputStream(inputStream));
    }

    public static Manifest read(DataInputStream dataInputStream) throws IOException {
        String readArch = readArch(dataInputStream);
        int readShort = dataInputStream.readShort() & UShort.MAX_VALUE;
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < readShort; i++) {
            arrayList.add(readLib(dataInputStream));
        }
        return new Manifest(readArch, arrayList);
    }

    private static String readArch(DataInputStream dataInputStream) throws IOException {
        byte readByte = dataInputStream.readByte();
        if (readByte == 1) {
            return MinElf.ISA.AARCH64;
        }
        if (readByte == 2) {
            return MinElf.ISA.ARM;
        }
        if (readByte == 3) {
            return MinElf.ISA.X86_64;
        }
        if (readByte == 4) {
            return MinElf.ISA.X86;
        }
        throw new RuntimeException("Unrecognized arch id: " + ((int) readByte));
    }

    private static String readLib(DataInputStream dataInputStream) throws IOException {
        byte[] bArr = new byte[dataInputStream.readShort() & UShort.MAX_VALUE];
        dataInputStream.readFully(bArr);
        return new String(bArr, StandardCharsets.UTF_8);
    }
}
