package boundless.tools.peparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

import boundless.tools.peparser.binlib.BinLibReader;

public class PEParser {

	private List<PEExport> _exports;
	public COFFFileHeader coffFileHeader;
	public OptionalHeader32 optionalHeader;
	public OptionalHeaderWindows32 optionalWindowsHeader;

	public PEParser(FileInputStream inputPEFile) throws Exception {
		BinLibReader reader = new BinLibReader(inputPEFile);
		this.parse(reader);
		reader.close();
	}

	private void parse(BinLibReader input) throws Exception {
		// PE is in least bit order
		input.setByteOrder(ByteOrder.LITTLE_ENDIAN);

		if (input.toString("hex", 0, 4) != "4d5a9000") {
			throw new Exception("Not a Valid MsDOS\\PE file: " + input.toString("hex", 0, 4));
		}

		int currentOffset = input.read(BinLibTypes.UInt8, 0x3c);

		this.coffFileHeader = input.read(COFFFileHeader.class, currentOffset);

		if (this.coffFileHeader.signature != "50450000") {
			throw new Error("Not a Valid PE file " + this.coffFileHeader.signature + " : " + currentOffset);
		}

		currentOffset += input.sizeOf(COFFFileHeader.class);

		short optionalHeaderMagic = input.read(BinLibTypes.UInt16, currentOffset);
		currentOffset += input.sizeOf(BinLibTypes.UInt16);

		// into arch land

		if (optionalHeaderMagic != 0x10b) {
			throw new Exception("Invalid OptionalHeaderMagic");
		}

		this.optionalHeader = input.read(OptionalHeader32.class, currentOffset);
		currentOffset += input.sizeOf(OptionalHeader32.class);
		this.optionalWindowsHeader = input.read(OptionalHeaderWindows32.class, currentOffset);
		currentOffset += input.sizeOf(OptionalHeaderWindows32.class);

		/*
	        for (var i in tables) {
	                var dir = file.read(dataDirectory, currentOffset);
	                if (dir.virtualAddress === 0 && dir.size === 0) {

	                } else {
	                        try {
	                                dir = tables[i](file, dir.virtualAddress, dir);
	                        } catch (e) {
	                                console.log("Error in: " + i);
	                        }

	                        ret.dataDirectorys[i] = dir;
	                }
	                currentOffset += binLib.sizeOf(dataDirectory);
	        }
		 */
	}

	public List<PEExport> getExports() {
		return this._exports;
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: java PEParser [peFile]");
			System.exit(1);
		}

		try {
			PEParser parser = new PEParser(new FileInputStream(new File(args[2])));
			for (PEExport exp : parser.getExports()) {
				System.out.println(exp.name);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
