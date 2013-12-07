package distorm;

public class Operand {
	public enum OperandType {
		None, Reg, Imm, Imm1, Imm2, Disp, Smem, Mem, Pc, Ptr
	}

	private int type;
	private int index;
	private int size;

    private Operand() { }

	public OperandType getType() {
		return OperandType.values()[type];
	}

	public int getIndex() {
		return index;
	}

	public int getSize() {
		return size;
	}
}