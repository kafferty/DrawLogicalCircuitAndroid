package com.ericmpayne.cl_circuitdraw;

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
		boolean v0 = (null == inputElements[0] ? false : inputElements[0].getLogicValue());
		boolean v1 = (null == inputElements[1] ? false : inputElements[1].getLogicValue());
		return v0 && v1;
	}

	@Override
	public boolean hasLogicType() {
		return true;
	}

	@Override
	public double[] getOffsetForGateLocation(GateLocation locationForWire) {
		//Для данного местоположения входа, определяем смещение от верхнего левого угла
		//Логичесеого элемента, чтобы понять, где мы должны нарисовать окончание провода.
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
				offset = new double[] {0.5, 0.2};
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

}
