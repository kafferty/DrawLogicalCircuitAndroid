package com.ericmpayne.cl_circuitdraw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity extends Activity {

	Button btnExport;
	Button btnSave;
	Button btnTable;
	Button btnLoad;

	private RelativeLayout L1;

	private TruthTableData tt;
	private TruthTableAdapter ttAdapter;
	private int NUM_ROWS = 4;
	private int NUM_COLUMNS = 7;
	private int countForExport = 0;
	final String LOG_TAG = "myLogs";

	// Инициализируем в  initializeParametersFromScreenSize()
	public static int LOGIC_ELEMENT_HEIGHT;
	public static int LOGIC_ELEMENT_WIDTH;
	public static int LOGIC_ELEMENT_MARGIN_SIZE;
	private static double SCREEN_USE_PERCENTAGE;

	private int[] INPUT_ICONS = { R.drawable.icon_x0,
								  R.drawable.icon_x1,
								  R.drawable.icon_x2,
								  R.drawable.icon_x3 };
	
	private int[] OUTPUT_ICONS = { R.drawable.icon_y0,
								   R.drawable.icon_y1,
								   R.drawable.icon_y2,
								   R.drawable.icon_y3 };
	
	private LogicElementCreationAdapter elementCreator = null;
	private RelativeLayout topLayout;
	private GridLayout mainGrid;
	private DrawingOverlayView wireOverlay;
	private LogicEvaluationAdapter logicEvaluator;

	
	private LogicElementInput[] inputElements;
	private LogicElementOutput[] outputElements;

	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		main();
		L1 = (RelativeLayout) findViewById(R.id.activity_main_relativelayout_top);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		getActionBar().setTitle(R.string.action_bar_title);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_restart:
	    	this.recreate();
	    	return true;
			case R.id.action_about:
				Intent intent = new Intent(MainActivity.this, About.class);
				startActivity(intent);

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void main()
	{
		initializeParametersFromScreenSize();
		initializeLayouts();
		initializeWireOverlay();
		initializeLogicElementCreationAdapter();
		initializeButtons();
		initializeLogicEvaluationAdapter();
		initializeBtn();
	}

	private void initializeBtn() {
		btnExport = (Button) findViewById(R.id.export);
		btnTable = (Button) findViewById(R.id.table);
		btnSave = (Button) findViewById(R.id.save);
		btnLoad = (Button) findViewById(R.id.load);


		final MainActivity mA = this;

		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Save();
			}
		});

		btnLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String filename = "myCircuit.txt";
					BufferedReader br = new BufferedReader(new InputStreamReader(
							openFileInput(filename)));
					String str = "";
					while ((str = br.readLine()) != null) {
						Log.d(LOG_TAG, str);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		btnTable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tt = logicEvaluator.evaluateLogic();
				if (null != tt)
				{
					ttAdapter = new TruthTableAdapter(mA,tt,LOGIC_ELEMENT_WIDTH,NUM_ROWS);
					ttAdapter.showTable();
				}
			}
		});

		btnExport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Export();
		}

	});

	}

	private void Export() {
		countForExport++;
		View v1 = L1.getRootView();
		v1.setDrawingCacheEnabled(true);
		Bitmap bm = v1.getDrawingCache();
		String filename = "circuit" + countForExport + ".png";
		File sd = Environment.getExternalStorageDirectory();
		File dest = new File(sd, filename);

		try {
			FileOutputStream out = new FileOutputStream(dest);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Toast.makeText(this, "Скрин схемы сохранен на SD карту", Toast.LENGTH_SHORT).show();
	}

	private void Save() {
		String filename = "myCircuit.txt";
		countForExport++;


		try {
			// отрываем поток для записи
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					openFileOutput(filename, MODE_PRIVATE)));
			// пишем данные
			for (int i = 0; i < elementCreator.getAllLogicElement().size(); i++) {
				bw.write(elementCreator.getAllLogicElement().get(i).getElementName());
				bw.write(" " + (int) elementCreator.getAllLogicElement().get(i).getX());
				bw.write(" " + (int) elementCreator.getAllLogicElement().get(i).getY());
				bw.write("\n");
			}
			// закрываем поток
			for (int i = 0; i < elementCreator.wireCoordinates.size(); i++) {
				for (int k = 0; i < elementCreator.wireCoordinates.get(i).size())
				bw.write(elementCreator.wireCoordinates.get(i).get(1));
			}
			bw.close();
			Log.d(LOG_TAG, "Файл записан");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeLogicEvaluationAdapter()
	{
		logicEvaluator = new LogicEvaluationAdapter(this, inputElements, outputElements, NUM_ROWS);
	}
	
	
	private void initializeParametersFromScreenSize()
	{
		// Определяем размеры каждого логического элемента исходя из размеров экрана
		
		Display display;
		Point size;
		int screenWidth;
		int screenHeight;
		int maxButtonHeight;
		int maxButtonWidth;
		
		SCREEN_USE_PERCENTAGE = 0.70;
		LOGIC_ELEMENT_MARGIN_SIZE = 1;
		
		display = getWindowManager().getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		
		screenWidth = (int) (SCREEN_USE_PERCENTAGE*size.x);
		screenHeight = (int) (SCREEN_USE_PERCENTAGE*size.y);
		
		maxButtonHeight = (int) (screenHeight / NUM_ROWS);
		maxButtonWidth = (int) (screenWidth / NUM_COLUMNS);
		
		LOGIC_ELEMENT_HEIGHT = Math.min(maxButtonHeight, maxButtonWidth);
		LOGIC_ELEMENT_WIDTH = LOGIC_ELEMENT_HEIGHT;	
	}
	
	private void initializeLogicElementCreationAdapter()
	{
		elementCreator = new LogicElementCreationAdapter(this,wireOverlay,mainGrid,LOGIC_ELEMENT_WIDTH,LOGIC_ELEMENT_HEIGHT,LOGIC_ELEMENT_MARGIN_SIZE);
	}

	private void initializeLayouts()
	{
		topLayout = (RelativeLayout) findViewById(R.id.activity_main_relativelayout_top);

		mainGrid = (GridLayout) findViewById(R.id.activity_main_gridlayout);
		mainGrid.setColumnCount(NUM_COLUMNS);
	}
	
	private void initializeButtons()
	{
		// Добавляем входные элементы, исходные и выходные элементы.
		// Добавляем onTouch listeners для каждого из этих элементов


		LogicElement b = null;
		GridLayout.LayoutParams p = null;
		
		inputElements = new LogicElementInput[NUM_ROWS];
		outputElements = new LogicElementOutput[NUM_ROWS];

		for (int i = 0; i < NUM_ROWS*NUM_COLUMNS; i++)
		{
			// Входные элементы слева, выходные справа и исходные в центре.
			if (0 == i % NUM_COLUMNS)
			{
				b = new LogicElementInput(this,null,R.style.style_logic_element, INPUT_ICONS[i/NUM_COLUMNS]);
				inputElements[i/NUM_COLUMNS] = (LogicElementInput) b;
			}
			else if (0 == (i+1) % NUM_COLUMNS)
			{
				b = new LogicElementOutput(this,null,R.style.style_logic_element, OUTPUT_ICONS[i/NUM_COLUMNS]);
				outputElements[i/NUM_COLUMNS] = (LogicElementOutput) b;
			}
			else
			{
				b = new LogicElementBlank(this,null,R.style.style_logic_element);
			}
			
			p = new GridLayout.LayoutParams();	
			p.height = LOGIC_ELEMENT_HEIGHT;
	        p.width = LOGIC_ELEMENT_WIDTH;
	        p.setMargins(LOGIC_ELEMENT_MARGIN_SIZE, LOGIC_ELEMENT_MARGIN_SIZE, LOGIC_ELEMENT_MARGIN_SIZE, LOGIC_ELEMENT_MARGIN_SIZE);
	        p.setGravity(Gravity.CENTER_HORIZONTAL);
	        
	        b.setLayoutParams(p);
			b.setBackgroundResource(R.drawable.logic_element_drawable);
			b.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View arg0, MotionEvent arg1) {
					return elementCreator.logicElementOnTouchHandler((LogicElement) arg0, arg1);
				}});
	        
	        mainGrid.addView(b);
		}
		
	}
	
	private void initializeWireOverlay()
	{
		wireOverlay = new DrawingOverlayView(this);
		topLayout.addView(wireOverlay);
	}
}