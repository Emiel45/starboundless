package boundless.tools.peparser;

import boundless.tools.peparser.binlib.IBinLibType;

public class COFFFileHeader implements IBinLibType {

	public String signature;
	public short machine;
	public short numberOfSections;
	public int timeDateStamp;
	public int pointerToSymbolTable;
	public int numberOfSymbols;
	public short sizeOfOptionalHeader;
	public short characteristics;

}
