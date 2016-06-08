package com.kafferty.circuitdraw;

import android.content.Context;
import android.util.AttributeSet;

public class LogicElementBlank extends LogicElement {

	public LogicElementBlank(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean getLogicValue() {
		return false;
	}

	@Override
	public boolean hasLogicType() {
		return false;
	}

	@Override
	public double[] getOffsetForGateLocation(GateLocation locationForWire) {
		//Смещение не нужно.
		return new double[]{0,0};
	}

	@Override
	public String getElementName() {
		return null;
	}

	public int getTypeId() { return 100000;}

}
