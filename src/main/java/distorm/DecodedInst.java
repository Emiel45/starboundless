package distorm;

public class DecodedInst {
    private String mnemonic;
    private String operands;
    private String hex;
    private int size;
    private long offset;

	private DecodedInst() { }

	public String getMnemonic() {
		return mnemonic;
	}
	
	public String getOperands() {
		return operands;
	}
	
	public String getHex() {
		return hex;
	}
	
	public int getSize() {
		return size;
	}
	
	public long getOffset() {
		return offset;
	}
}