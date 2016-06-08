package com.kafferty.circuitdraw;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public abstract class LogicElement extends ImageButton {

	protected boolean hasLogicType;

	protected LogicElement[] inputElements;

	protected int[] coordinates;

	public LogicElement(Context context, AttributeSet attrs, int defStyle)
	{
		super(context,attrs,defStyle);
		this.hasLogicType = false;
		this.inputElements = null;
		this.coordinates = null;
	}

	public abstract int getTypeId() ;

	public abstract boolean getLogicValue();
	public abstract boolean hasLogicType();
	public abstract double[] getOffsetForGateLocation(GateLocation locationForWire);

	public abstract String getElementName();
	public LogicElement[] getInputElements() {
		return inputElements;
	}
	protected String elementToString() {
		return getTypeId() + " " + getElementName() + " " + coordinates[0] + " " + coordinates[1];
	}

	public String elementToStringWithInputs () {//

		//Запишем информацию о самом элементе
		String result = this.elementToString();

		if (inputElements.length > 0 && inputElements[0] != null) { //запишем информацию о первом входном элементе
			result = result + " input1 " + inputElements[0].elementToString();
		}

		if (inputElements.length > 1 && inputElements[1] != null) { //запишем информацию о втором входном элементе
			result = result + " input2 " + inputElements[1].elementToString();
		}

		return result;
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
		if (inputElements == null)
		{
			return GateLocation.NONE;
		}
		
		return determineLocationOfWire(input, quadrantTouched);
	}
	
	protected GateLocation determineLocationOfWire(LogicElement input, Quadrant quadrantTouched)
	{

		//Используем QuadrantToucned для того, чтобы понять, где нужно нарисовать провод.
		//Если пользователь прикоснулся к верхнему левому квадрату, то используем входной порт 0 и рисуем провод в
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
