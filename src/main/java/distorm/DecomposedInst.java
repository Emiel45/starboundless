package distorm;

public class DecomposedInst {
    private class ImmVariant {
        private long mValue;
        private int mSize;

        public long getImm() {
            return mValue;
        }

        public int getSize() {
            return mSize;
        }
    }

    private class DispVariant {
        private long mDisplacement;
        private int mSize;

        public long getDisplacement() {
            return mDisplacement;
        }

        public int getSize() {
            return mSize;
        }
    }

    private long addr;
    private int size;
    private int flags;
    private int segment;
    private int base, scale;
    private int opcode;
    public Operand[] operands;
    public DispVariant disp;
    public ImmVariant imm;
    private int unusedPrefixesMask;
    private int meta;
    private int registersMask;
    private int modifiedFlagsMask;
    private int testedFlagsMask;
    private int undefinedFlagsMask;

    public long getAddress() {
        return addr;
    }

    public int getSize() {
        return size;
    }

    public OpcodeEnum getOpcode() {
        return Opcodes.lookup(opcode);
    }

    public int getSegment() {
        return segment & 0x7f;
    }

    public boolean isSegmentDefault() {
        return (segment & 0x80) == 0x80;
    }

    public int getBase() {
        return base;
    }

    public int getScale() {
        return scale;
    }

    public int getUnusedPrefixesMask() {
        return unusedPrefixesMask;
    }

    public int getMeta() {
        return meta;
    }

    public int getRegistersMask() {
        return registersMask;
    }

    public int getModifiedFlagsMask() {
        return modifiedFlagsMask;
    }

    public int getTestedFlagsMask() {
        return testedFlagsMask;
    }

    public int getUndefinedFlagsMask() {
        return undefinedFlagsMask;
    }
}