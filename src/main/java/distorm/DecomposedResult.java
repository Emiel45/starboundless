package distorm;

public class DecomposedResult {
	public DecomposedResult(int maxInstructions) {
		this.maxInstructions = maxInstructions;
	}

	public DecomposedInst[] instructions;
	private int maxInstructions;
}