package com.kafferty.circuitdraw;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TruthTableAdapter {

	private int TT_ELEMENT_PADDING = 5;
	private int TT_SEPARATOR_PADDING = 20;
	
	private Activity activity;
	private TruthTableData tt;
	private TableLayout table;

	private String COLOUR_TABLE_ONE = "#b8e4d0";
	private String COLOUR_TABLE_TWO = "#dbe5f1";
	private String COLOUR_TABLE_HEADER = "#4f81bd";
	
	AlertDialog.Builder builder = null;
	final LayoutInflater inflater;
	
	public TruthTableAdapter(Activity activity, TruthTableData tt, int LOGIC_ELEMENT_WIDTH, int NUM_ROWS) {
		this.activity = activity;
		this.tt = tt;
		
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void showTable()
	{
		// Выводим двоичную таблицу истинности в таблицу в диалоге
		AlertDialog alertDialog;
		TableRow tr;
		TextView tv;
		
		boolean[][] inputs = tt.getInputs();
		boolean[][] outputs = tt.getOutputs();
		boolean[] inputIndices = tt.getInputIndices();
		boolean[] outputIndices = tt.getOutputIndices(); 
		
		table = (TableLayout) inflater.inflate(R.layout.truth_table, (ViewGroup) activity.findViewById(R.layout.activity_main));
		builder = new AlertDialog.Builder(activity).setView(table);
		
		// Создаем первую строчку с именами входов и выходов
		tr = new TableRow(activity);
		for (int i = 0; i < inputIndices.length; i++)
		{
			if (inputIndices[i])
			{
				tv = new TextView(activity);
				tv.setText("x[" + i + "]");
				tv.setPadding(TT_ELEMENT_PADDING, TT_ELEMENT_PADDING, TT_ELEMENT_PADDING, TT_ELEMENT_PADDING);
				tv.setGravity(Gravity.RIGHT);
				tr.addView(tv);
			}
		}
		//Сепаратор
		tv = new TextView(activity);
		tv.setPadding(TT_SEPARATOR_PADDING, 0, TT_SEPARATOR_PADDING, 0);
		tr.addView(tv);
		
		for (int i = 0; i < outputIndices.length; i++)
		{
			if (outputIndices[i])
			{
				tv = new TextView(activity);
				tv.setText("y[" + i + "]");
				tv.setPadding(TT_ELEMENT_PADDING, TT_ELEMENT_PADDING, TT_ELEMENT_PADDING, TT_ELEMENT_PADDING);
				tv.setGravity(Gravity.RIGHT);
				tr.addView(tv);
			}
		}
		
		tr.setBackgroundColor(Color.parseColor(COLOUR_TABLE_HEADER));
		table.addView(tr);
		
		// Создание всех строчек, включащих входы и выходы.
		for (int i = 0; i < tt.getNumRows(); i++)
		{
			tr = new TableRow(activity);
			for (int j = 0; j < inputs[i].length; j++)
			{
				tv = new TextView(activity);
				tv.setText(inputs[i][j] ? "1" : "0");
				tv.setPadding(TT_ELEMENT_PADDING, 0, TT_ELEMENT_PADDING, 0);
				tv.setGravity(Gravity.RIGHT);
				tr.addView(tv);
			}
				// Сепаратор
				tv = new TextView(activity);
				tv.setPadding(TT_ELEMENT_PADDING, 0, TT_ELEMENT_PADDING, 0);
				tr.addView(tv);
			
			for (int j = 0; j < outputs[i].length; j++)
			{
				tv = new TextView(activity);
				tv.setText(outputs[i][j] ? "1" : "0");
				tv.setPadding(TT_ELEMENT_PADDING, 0, TT_ELEMENT_PADDING, 0);
				tv.setGravity(Gravity.RIGHT);
				tr.addView(tv);
			}
			
			if (0 == i % 2)
			{
				tr.setBackgroundColor(Color.parseColor(COLOUR_TABLE_ONE));
			}
			else
			{
				tr.setBackgroundColor(Color.parseColor(COLOUR_TABLE_TWO));
			}

			table.addView(tr);
		}
				
		alertDialog = builder.create();
		alertDialog.show();
	}
	
}
