package com.ericmpayne.cl_circuitdraw;

public class TruthTableData {
	
	private int numInputs;
	private int numOutputs;
	private int numRows;
	
	private boolean[][] inputs;
	private boolean[][] outputs;
	
	private boolean[] inputIndices;
	private boolean[] outputIndices;

	public TruthTableData(int numInputs, int numOutputs, int numRows, boolean[][] inputs, boolean[][] outputs, boolean[] inputIndices, boolean[] outputIndices)
	{
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
		this.numRows = numRows;
		this.inputs = inputs;
		this.outputs = outputs;
		this.inputIndices = inputIndices;
		this.outputIndices = outputIndices;
	}

	public int getNumInputs()
	{
		return numInputs;
	}

	public int getNumOutputs()
	{
		return numOutputs;
	}

	public int getNumRows()
	{
		return numRows;
	}

	public boolean[][] getInputs()
	{
		return inputs;
	}

	public boolean[][] getOutputs()
	{
		return outputs;
	}

	public boolean[] getInputIndices()
	{
		return inputIndices;
	}

	public boolean[] getOutputIndices()
	{
		return outputIndices;
	}
	
}
