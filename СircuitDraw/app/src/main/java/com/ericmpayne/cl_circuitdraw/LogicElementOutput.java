package com.ericmpayne.cl_circuitdraw;

import android.content.Context;
import android.util.AttributeSet;

public class LogicElementOutput extends LogicElement {

	public LogicElementOutput(Context context, AttributeSet attrs, int defStyle, int imageId) {
		super(context, attrs, defStyle);
		inputElements = new LogicElement[1];
		this.setImageResource(imageId);
	}

	@Override
	public boolean getLogicValue() {
		boolean v0 = (null == inputElements[0] ? false : inputElements[0].getLogicValue());
		return v0;
	}

	@Override
	public boolean hasLogicType() {
		return true;
	}
	
	public boolean hasInput()
	{
		return (null == inputElements[0] ? false : true);
	}

	@Override
	public double[] getOffsetForGateLocation(GateLocation locationForWire) {
		return new double[]{0,0};
	}

	@Override
	public String getElementName() {
		return null;
	}

}
