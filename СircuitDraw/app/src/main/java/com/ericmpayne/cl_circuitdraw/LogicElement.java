package com.ericmpayne.cl_circuitdraw;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public abstract class LogicElement extends ImageButton {
	
	protected boolean hasLogicType;
	protected LogicElement[] inputElements;
	protected int[] coordinates;
	
	public abstract boolean getLogicValue();
	public abstract boolean hasLogicType();
	public abstract double[] getOffsetForGateLocation(GateLocation locationForWire);

	public LogicElement(Context context, AttributeSet attrs, int defStyle)
	{
		super(context,attrs,defStyle);
		hasLogicType = false;
		inputElements = null;
		coordinates = null;
	}
	
	public int[] getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(int[] coordinates) {
		this.coordinates = coordinates;
	}
	
	public GateLocation addInput(LogicElement input, Quadrant quadrantTouched)
	{
		//Добавление входа и возвращение того, где он расположен
		if (null == inputElements)
		{
			return GateLocation.NONE;
		}
		
		return determineLocationOfWire(input, quadrantTouched);
	}
	
	private GateLocation determineLocationOfWire(LogicElement input, Quadrant quadrantTouched)
	{

		//Используем QuadrantToucned для того, чтобы понять, где нужно нарисовать провод.
		// Если пользователь прикоснулся к верхнему левому квадрату, то используем входной порт 0 и рисуем провод в
		//Левом верхнем углу. Если уже используется, то используем другой.
		GateLocation locationToReturn = GateLocation.NONE;
		
		switch (inputElements.length)
		{
			case 1:
				locationToReturn = (null == inputElements[0] ? GateLocation.LEFT_CENTRE : GateLocation.NONE);
				inputElements[0] = input;
				break;
			case 2:
				if (Quadrant.LEFT_TOP == quadrantTouched || Quadrant.RIGHT_TOP == quadrantTouched)
				{
					if (null == inputElements[0])
					{
						inputElements[0] = input;
						locationToReturn = GateLocation.LEFT_UPPER;
					}
					else if (null == inputElements[1])
					{
						inputElements[1] = input;
						locationToReturn = GateLocation.LEFT_LOWER;
					}
				}
				else if (Quadrant.LEFT_BOTTOM == quadrantTouched || Quadrant.RIGHT_BOTTOM == quadrantTouched)
				{
					if (null == inputElements[1])
					{
						inputElements[0] = input;
						locationToReturn = GateLocation.LEFT_UPPER;
					}
					else if (null == inputElements[1])
					{
						inputElements[1] = input;
						locationToReturn = GateLocation.LEFT_LOWER;
					}
				}		
			default:
				break;
		}
		
		return locationToReturn;
	}
	
}
