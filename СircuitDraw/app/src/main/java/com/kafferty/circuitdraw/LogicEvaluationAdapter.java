package com.kafferty.circuitdraw;
import android.app.Activity;
import android.widget.Toast;


public class LogicEvaluationAdapter {

	private Activity activity;
	private LogicElementInput[] inputElements;
	private LogicElementOutput[] outputElements;
	private int NUM_ROWS;
	
	public LogicEvaluationAdapter (Activity activity, LogicElementInput[] inputElements, LogicElementOutput[] outputElements, int NUM_ROWS)
	{
		this.activity = activity;
		this.inputElements = inputElements;
		this.outputElements = outputElements;
		this.NUM_ROWS = NUM_ROWS;
	}
	
	public TruthTableData evaluateLogic()
	{
		// Для создания таблицы истинности создаем boolean массив. (2^n, где n - кол-во входов X)
		boolean[][] truthTableInputs;
		boolean[][] truthTableOutputs;
		
		boolean[] inputAtIndexIsInUse;
		boolean[] outputAtIndexIsInUse;
		
		int numInputsInUse = 0;
		int numOutputsInUse = 0;
		int numTruthTableRows;
		
		int tmpIdxGetInputInUseList = 0;
		int tmpIdxGetOutputInUseList = 0;
		
		LogicElementInput[] inputsInUse;
		LogicElementOutput[] outputsInUse;
		
		//Создаем массив, сообщающий, какие входные и выходные элементы сейчас используются.
		
		for (int i = 0; i < NUM_ROWS; i++)
		{
			numInputsInUse += (inputElements[i].isInUse() ? 1:0);
			numOutputsInUse += (outputElements[i].hasInput() ? 1:0);
		}
		
		if (0 == numInputsInUse || 0 == numOutputsInUse)
		{
			Toast.makeText(activity, R.string.error_no_valid_connections, Toast.LENGTH_SHORT).show();
			return null;
		}
		
		inputsInUse = new LogicElementInput[numInputsInUse];
		outputsInUse = new LogicElementOutput[numOutputsInUse];
		
		inputAtIndexIsInUse = new boolean[NUM_ROWS];
		outputAtIndexIsInUse = new boolean[NUM_ROWS];
		
		numTruthTableRows = 1 << numInputsInUse;
		
		for (int i = 0; i < NUM_ROWS; i++)
		{
			if (inputElements[i].isInUse())
			{
				inputsInUse[tmpIdxGetInputInUseList] = inputElements[i];
				inputAtIndexIsInUse[i] = true;
				tmpIdxGetInputInUseList++;
			}
			if (outputElements[i].hasInput())
			{
				outputsInUse[tmpIdxGetOutputInUseList] = outputElements[i];
				outputAtIndexIsInUse[i] = true;
				tmpIdxGetOutputInUseList++;
			}
		}

		// Когда мы знаем количество входов, создать массив всех возможных дискретных входов (2 ^ numInputsInUse ) .
		// Для каждого из 2 ^ N входов :
		// Установить входы логических элементов.
		// Получить значение выходного сигнала для каждого выходного элемента для этой заданной комбинации входного сигнала.
		
		truthTableInputs = generateTruthTableInputs(numInputsInUse, numTruthTableRows);
		truthTableOutputs = new boolean[numTruthTableRows][numOutputsInUse];
		
		for (int i = 0; i < numTruthTableRows; i++)
		{
			for (int j = 0; j < numInputsInUse; j++)
			{
				inputsInUse[j].setLogicValue(truthTableInputs[i][j]);
			}
			
			for (int j = 0; j < numOutputsInUse; j++)
			{
				truthTableOutputs[i][j] = outputsInUse[j].getLogicValue();
			}
		}
		return new TruthTableData(numInputsInUse, numOutputsInUse, numTruthTableRows, truthTableInputs, truthTableOutputs, inputAtIndexIsInUse, outputAtIndexIsInUse);
	}
	
	private boolean[][] generateTruthTableInputs (int numInputsInUse, int numTruthTableRows)
	{
		//Создаем таблицу истинности.
		
		boolean[][] truthTableInputs = new boolean[numTruthTableRows][numInputsInUse];
		
		for (int idx = 0; idx < numTruthTableRows; idx++)
		{
			truthTableInputs[idx] = integerToBooleanArray(idx, numInputsInUse);
		}
		
		return truthTableInputs;
	}
	
	private boolean[] integerToBooleanArray (int num, int numInputsInUse)
	{
		
		boolean[] truthTableRow = new boolean[numInputsInUse];
		String numAsBinaryString = String.format("%" + numInputsInUse + "s", Integer.toBinaryString(num)).replace(' ', '0');
		for (int idx = 0; idx < numInputsInUse; idx++)
		{
			truthTableRow[idx] = numAsBinaryString.substring(idx, idx + 1).equals("1") ? true : false;
		}
		
		return truthTableRow;
	}

}
