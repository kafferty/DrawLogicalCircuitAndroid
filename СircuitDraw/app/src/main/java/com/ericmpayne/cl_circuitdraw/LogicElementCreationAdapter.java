package com.ericmpayne.cl_circuitdraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

public class LogicElementCreationAdapter {
	
	// Замена пустых элементов логическими элементами
	
	private static final int INVALID_TOUCH_POINTER_ID = -1;
	
	
	private int[] ID_MENU_BUTTON_LIST = {R.id.menu_button_not,
										 R.id.menu_button_and,
										 R.id.menu_button_or,
										 R.id.menu_button_cancel};
	
	private int LOGIC_ELEMENT_WIDTH;
	private int LOGIC_ELEMENT_HEIGHT;
	private int LOGIC_ELEMENT_MARGIN_SIZE;
	
	private LayoutInflater inflater;
	private Activity activity;
	private AlertDialog alertDialog;
	private DrawingOverlayView wireOverlay;
	private GridLayout mainGrid;
	
	private int touchEventPointerId;//Используется для предотвращения любого рода рисования
	private int[] initialLineDrawStartCoordinates; // Координаты того, где мы начинаем рисовать. Используется тогда, когда предыдущий провод был нарисован
	public LogicElementCreationAdapter(Activity activity, DrawingOverlayView wireOverlay, GridLayout mainGrid, int LOGIC_ELEMENT_WIDTH, int LOGIC_ELEMENT_HEIGHT, int LOGIC_ELEMENT_MARGIN_SIZE)
	{
		this.activity = activity;
		this.wireOverlay = wireOverlay;
		this.mainGrid = mainGrid;
		this.LOGIC_ELEMENT_WIDTH = LOGIC_ELEMENT_WIDTH;
		this.LOGIC_ELEMENT_HEIGHT = LOGIC_ELEMENT_HEIGHT;
		this.LOGIC_ELEMENT_MARGIN_SIZE = LOGIC_ELEMENT_MARGIN_SIZE;
		
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public LogicElement createElement(LogicElement elementToUpdate)
	{
		// Создание меню для выбора логического элемента
		
		View menu = null;
		AlertDialog.Builder builder = null;
		View menuButton = null;
		final LogicElement elementToUpdateLocal = elementToUpdate;
		touchEventPointerId = INVALID_TOUCH_POINTER_ID;
		
		menu = inflater.inflate(R.layout.logic_element_select, (ViewGroup) activity.findViewById(R.layout.activity_main));
		builder = new AlertDialog.Builder(activity).setView(menu); 
		alertDialog = builder.create();

		for (int i = 0; i < ID_MENU_BUTTON_LIST.length; i++)
		{
			menuButton = (View) menu.findViewById(ID_MENU_BUTTON_LIST[i]);
			menuButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					logicElementCreationMenuClickHandler(v,elementToUpdateLocal);
				}
	        });
		}
		alertDialog.show();
		return null;
	}
	
	private void logicElementCreationMenuClickHandler(View clickedView, LogicElement elementToUpdate)
	{
		// Получаем выбранный пользователем логический элемент и располагаем его на нужном месте.
		
		ViewGroup parent = (ViewGroup) elementToUpdate.getParent();
		LogicElement newElement = null;
		
		switch (clickedView.getId())
		{
			case R.id.menu_button_not:
				newElement = new LogicElementNot(elementToUpdate.getContext(),null,R.style.style_logic_element);
				break;
			case R.id.menu_button_and:
				newElement = new LogicElementAnd(elementToUpdate.getContext(),null,R.style.style_logic_element);
				break;
			case R.id.menu_button_or:
				newElement = new LogicElementOr(elementToUpdate.getContext(),null,R.style.style_logic_element);
				break;
			case R.id.menu_button_cancel:
				break;
			default:
				break;
		}
		
		if (null != newElement) // Если пользователь не нажал отмену
		{
			GridLayout.LayoutParams p = new GridLayout.LayoutParams();
			
			p.height = LOGIC_ELEMENT_HEIGHT;
	        p.width = LOGIC_ELEMENT_WIDTH;
	        p.setMargins(LOGIC_ELEMENT_MARGIN_SIZE,
	        			 LOGIC_ELEMENT_MARGIN_SIZE,
	        			 LOGIC_ELEMENT_MARGIN_SIZE,
	        			 LOGIC_ELEMENT_MARGIN_SIZE);
			
	        newElement.setLayoutParams(p);
	        newElement.setBackgroundResource(R.drawable.logic_element_drawable);
	        newElement.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View arg0, MotionEvent arg1) {
					return logicElementOnTouchHandler((LogicElement) arg0, arg1);
				}});
			parent.addView(newElement, parent.indexOfChild(elementToUpdate));
			parent.removeView(elementToUpdate);
		}
			
		alertDialog.dismiss();
	}
		

	public boolean logicElementOnTouchHandler(LogicElement elementToUpdate, MotionEvent event)
	{
		//Если мы прикасаемся к неинциализированному элементу, то выбираем его логический тип
		//Если мы прикасаемся к инициализированному элементу, то соединяем его с другим элементов
		//Соединяем элементы только слева направо.
		
		int[] viewCoordinates = new int[2];
		elementToUpdate.getLocationInWindow(viewCoordinates);
		viewCoordinates[1] -= LOGIC_ELEMENT_HEIGHT;
		
		Quadrant touchQuadrant; // Часть элемента, который выбрал пользователем.
		GateLocation locationForWire = null; // Расположение для провода
		int[] touchCoordinates = new int[2];
		touchCoordinates[0] = (int) event.getRawX();
		touchCoordinates[1] = (int) event.getRawY() - LOGIC_ELEMENT_HEIGHT;
		
		LogicElement connectingElement;
		int[] connectingCoordinates; //Инициализируем, когда знаем, с каким элементов соединяем.
		int[][] leftWireExtensionPoints = null;
		int[][] rightWireExtensionPoints = null;
		
		boolean elementConnectionSuccess = false;

		if (event.getPointerId(0) == touchEventPointerId || INVALID_TOUCH_POINTER_ID == touchEventPointerId)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					// Если инициализирован элемент, рисуем путь
					if (elementToUpdate.hasLogicType())
					{
						touchQuadrant = getQuadrantOfTouchEvent(touchCoordinates[0],touchCoordinates[1],viewCoordinates[0],viewCoordinates[1],MainActivity.LOGIC_ELEMENT_WIDTH,MainActivity.LOGIC_ELEMENT_HEIGHT);
						wireOverlay.startNewPathAt(touchCoordinates[0],touchCoordinates[1]);
						touchEventPointerId = event.getPointerId(0);
						getLogicElementAtCoordinates(touchCoordinates[0], touchCoordinates[1]); // Обновление координат.
						initialLineDrawStartCoordinates = touchCoordinates;
					}
					break;	
					
				case MotionEvent.ACTION_MOVE:
					// Если инициализировали элемент, то продолжать рисовать путь.
					if (elementToUpdate.hasLogicType())
					{
						wireOverlay.continuePathAlong(touchCoordinates[0],touchCoordinates[1]);
					}
					break;
					
				case MotionEvent.ACTION_UP:
					// Если инициализировали элемент, то конец. Проверяем на наличие логических ошибок и ограничений. Если их нет, то рисуем.
					// Если элемент неинциализирован, то открываем меню и выбираем элемент.
					if (elementToUpdate.hasLogicType())
					{
						connectingCoordinates = new int[2];
						connectingElement = getLogicElementAtCoordinates(touchCoordinates[0],touchCoordinates[1]);
						connectingCoordinates[1] -= MainActivity.LOGIC_ELEMENT_HEIGHT;
						
						if (null != connectingElement)
						{
							connectingElement.getLocationInWindow(connectingCoordinates);
						}
						
						// Проверяем на ошибки
						//Устанавливаем true, если линия нарисована
						do
						{
							if (null == connectingElement)
							{
								elementConnectionSuccess = false;
								break;
							}
							
							if (connectingElement == elementToUpdate)
							{
								Toast.makeText(activity, R.string.error_element_connected_to_self, Toast.LENGTH_SHORT).show();
								elementConnectionSuccess = false;
								break;
							}
							
							if (!connectingElement.hasLogicType())
							{
								Toast.makeText(activity, R.string.error_no_logic_type, Toast.LENGTH_SHORT).show();
								elementConnectionSuccess = false;
								break;
							}
							
							if (viewCoordinates[0] >= connectingCoordinates[0])
							{
								Toast.makeText(activity, R.string.error_elements_left_to_right, Toast.LENGTH_SHORT).show();
								elementConnectionSuccess = false;
								break;
							}
							
							touchQuadrant = getQuadrantOfTouchEvent(touchCoordinates[0],touchCoordinates[1],connectingCoordinates[0],connectingCoordinates[1],MainActivity.LOGIC_ELEMENT_WIDTH,MainActivity.LOGIC_ELEMENT_HEIGHT);
							
							if (GateLocation.NONE == (locationForWire = connectingElement.addInput(elementToUpdate, touchQuadrant)))
							{
								Toast.makeText(activity, R.string.error_inputs_full, Toast.LENGTH_SHORT).show();
								elementConnectionSuccess = false;
								break;
							}
							
							if (elementToUpdate instanceof LogicElementInput)
							{
								((LogicElementInput) elementToUpdate).setInUse(true);
							}

							elementConnectionSuccess = true;
							
						} while (false);
						
						if (!(elementToUpdate instanceof LogicElementInput || elementToUpdate instanceof LogicElementOutput))
						{
							leftWireExtensionPoints = new int[2][];
							leftWireExtensionPoints[0] = initialLineDrawStartCoordinates;
							leftWireExtensionPoints[1] = getWireEndCoordinatesFromGateLocation(elementToUpdate, GateLocation.RIGHT_CENTRE);
						}
						
						if (!(connectingElement instanceof LogicElementInput || connectingElement instanceof LogicElementOutput))
						{
							rightWireExtensionPoints = new int[2][];
							rightWireExtensionPoints[0] = touchCoordinates;
							rightWireExtensionPoints[1] = getWireEndCoordinatesFromGateLocation(connectingElement, locationForWire);
						}
						
						wireOverlay.endPathAndDrawAt(elementConnectionSuccess, leftWireExtensionPoints, rightWireExtensionPoints);
						touchEventPointerId = INVALID_TOUCH_POINTER_ID;
					}
					else
					{
						createElement(elementToUpdate);
					}
					break;
					
				default:
					break;	
			}		
		}
		
		return true;
	}
	
	private LogicElement getLogicElementAtCoordinates (int touchX, int touchY)
	{
	//Поиск логических элементов
		//Если есть элемент в этих координатах, то вернуть его.
		
		int[] viewCoordinates;
		
		for (int idx = 0; idx < mainGrid.getChildCount(); idx++)
		{
			viewCoordinates = new int[2];
			mainGrid.getChildAt(idx).getLocationInWindow(viewCoordinates);
			viewCoordinates[1] -= MainActivity.LOGIC_ELEMENT_HEIGHT;

			if ((touchX >= viewCoordinates[0])
			   && (touchX <= viewCoordinates[0] + MainActivity.LOGIC_ELEMENT_WIDTH)
			   && ((touchY >= viewCoordinates[1])
			   && (touchY <= viewCoordinates[1] + MainActivity.LOGIC_ELEMENT_HEIGHT))) 
			{
				((LogicElement) mainGrid.getChildAt(idx)).setCoordinates(viewCoordinates);
				return (LogicElement) mainGrid.getChildAt(idx);
			}
		}
		
		return null;
	}
	
	private Quadrant getQuadrantOfTouchEvent(int touchX, int touchY, int viewX, int viewY, int width, int height)
	{
		//Решение о том, какой квадрат пользователь нажал (смещение).

		int leftRightDividingLine = viewX + (width/2);
		int topBottomDivingLine = viewY + (height/2);
		boolean isLeft = touchX < leftRightDividingLine;
		boolean isTop = touchY < topBottomDivingLine;
		
		if (isLeft && isTop)
		{
			return Quadrant.LEFT_TOP;	
		}
		else if (isLeft && !isTop)
		{
			return Quadrant.LEFT_BOTTOM;
		}
		else if (!isLeft && isTop)
		{
			return Quadrant.RIGHT_TOP;
		}
		else if (!isLeft && !isTop)
		{
			return Quadrant.RIGHT_BOTTOM;
		}
		
		return null;
	}
	
	private int[] getWireEndCoordinatesFromGateLocation(LogicElement element, GateLocation locationForWire)
	{
		if (element == null || locationForWire == null)
		{
			return null;
		}
		
		int[] coordinates = element.getCoordinates();
		double[] offset = element.getOffsetForGateLocation(locationForWire);
		
		coordinates[0] += offset[0]*LOGIC_ELEMENT_WIDTH;
		coordinates[1] += offset[1]*LOGIC_ELEMENT_HEIGHT;
		return coordinates;
	}
	
}
