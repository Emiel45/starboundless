package boundless.tools.peparser.binlib;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import boundless.tools.peparser.BinLibTypes;
import boundless.tools.peparser.COFFFileHeader;

public class BinLibReader {

	private BufferedInputStream _file;
	
	public BinLibReader(FileInputStream file) {
		this._file = new BufferedInputStream(file);
		
	}
	
	public void close() throws IOException {
		_file.close();
	}

	public void setByteOrder(ByteOrder littleEndian) {
		// TODO Auto-generated method stub
		
	}

	public String toString(String encoding, int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T read(BinLibTypes type, int offset) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public <T extends IBinLibType> T read(Class<T> structTemplate, int currentOffset) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends IBinLibType> int sizeOf(Class<T> structTemplate) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int sizeOf(BinLibTypes uint8) {
		// TODO Auto-generated method stub
		return 0;
	}

}
