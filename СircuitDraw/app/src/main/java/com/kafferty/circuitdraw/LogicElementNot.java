package com.kafferty.circuitdraw;

import android.content.Context;
import android.util.AttributeSet;

public class LogicElementNot extends LogicElement {

	public LogicElementNot(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inputElements = new LogicElement[1];
        this.setImageResource(R.drawable.icon_not);
	}

	@Override
	public boolean getLogicValue() {
		boolean v0 = (null == inputElements[0] ? false : inputElements[0].getLogicValue());
		return !v0;
	}

	@Override
	public boolean hasLogicType() {
		return true;
	}

	@Override
	public double[] getOffsetForGateLocation(GateLocation locationForWire) {
		double[] offset;
		switch (locationForWire)
		{
			case LEFT_CENTRE:
				offset = new double[] {0.1, 0.2};
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
		return "Not";
	}

}
