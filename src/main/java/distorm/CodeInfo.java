package distorm;

import java.nio.ByteBuffer;

public class CodeInfo {
    private long codeOffset;
    private long nextOffset;
    private ByteBuffer code;
    private int decodeType;
    private int features;

    public CodeInfo(long codeOffset, ByteBuffer code, DecodeType decodeType, int features) {
        this.codeOffset = codeOffset;
        this.code = code;
        this.decodeType = decodeType.ordinal();
        this.features = features;
    }

    public CodeInfo(long codeOffset, byte[] rawCode, DecodeType decodeType, int features) {
        this.code = ByteBuffer.allocateDirect(rawCode.length);
        code.put(rawCode);

        this.codeOffset = codeOffset;
        this.decodeType = decodeType.ordinal();
        this.features = features;
    }
}