package com.kafferty.circuitdraw;

import android.content.Context;
import android.util.AttributeSet;

public class LogicElementAnd extends LogicElement {

	public LogicElementAnd(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inputElements = new LogicElement[2];
        this.setImageResource(R.drawable.icon_and);
	}

	@Override
	public boolean getLogicValue() {
		boolean v0 = (inputElements[0] == null ? false : inputElements[0].getLogicValue());
		boolean v1 = (inputElements[1] == null ? false : inputElements[1].getLogicValue());
		return v0 && v1;
	}

	@Override
	public boolean hasLogicType() {
		return true;
	}

	@Override
	public double[] getOffsetForGateLocation(GateLocation locationForWire) {
		//Для данного местоположения входа, определяем смещение от верхнего левого угла
		//Логического элемента, чтобы понять, где мы должны нарисовать окончание провода.
		//Константы тут выведены эксперименатально (зависят от соответствующего .png)
		
		double[] offset;
		switch (locationForWire)
		{
			case LEFT_CENTRE:
				offset = new double[] {0.2, 0.2};
				break;
			case LEFT_UPPER:
				offset = new double[] {0, 0.1};
				break;
			case LEFT_LOWER:
				offset = new double[] {0, 0.3};
				break;
			case RIGHT_CENTRE:
				offset = new double[] {0.5, 0.1};
				break;
			case NONE:
				offset = new double[] {0, 0};
				break;
			default:
				offset = new double[] {0, 0};
				break;	
		}
		
		return offset;
	}

	@Override
	public String getElementName() {
		return "And";
	}

	@Override
	public int getTypeId() { return R.id.menu_button_and;}
}
