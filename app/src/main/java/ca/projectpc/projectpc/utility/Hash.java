package ca.projectpc.projectpc.utility;

public class Hash {
    public static final int FNV1A_Prime32 = 0x01000193;
    public static final int FNV1A_Offset32 = 0x811C9DC5;

    public static int FNV1A_32(byte[] buffer, int prime, int offset) {
        int hash = offset;
        for (byte b : buffer) {
            hash *= prime;
            hash ^= b;
        }
        return hash;
    }

    public static int FNV1A_32(byte[] buffer) {
        return FNV1A_32(buffer, FNV1A_Prime32, FNV1A_Offset32);
    }
}
