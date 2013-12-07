package distorm;

public class DecodedResult {
	public DecodedResult(int maxInstructions) {
		this.maxInstructions = maxInstructions;
		instructions = null;
	}

	public DecodedInst[] instructions;
	private int maxInstructions;
}