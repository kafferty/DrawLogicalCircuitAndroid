package com.ericmpayne.cl_circuitdraw;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private static int NUM_ROWS = 4;
	private static int NUM_COLUMNS = 7;

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		getActionBar().setTitle(R.string.action_bar_title);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		TruthTableData tt;
		TruthTableAdapter ttAdapter;
		
	    switch (item.getItemId()) {
	    case R.id.action_evaluate_logic:
	    	tt = logicEvaluator.evaluateLogic();
	    	if (null != tt)
	    	{
	    		ttAdapter = new TruthTableAdapter(this,tt,LOGIC_ELEMENT_WIDTH,NUM_ROWS);
		    	ttAdapter.showTable();
	    	}
	        return true;
	    case R.id.action_restart:
	    	this.recreate();
	    	return true;
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
