package com.kafferty.circuitdraw;
import android.content.Context;
import android.util.AttributeSet;

public class LogicElementInput extends LogicElement {
	
	private boolean logicValue;
	private boolean isInUse;

	public LogicElementInput(Context context, AttributeSet attrs, int defStyle, int imageId) {
		super(context, attrs, defStyle);
		inputElements = null;
		this.setImageResource(imageId);
		logicValue = false;
	}

	@Override
	public boolean getLogicValue() {
		return logicValue;
	}
	
	public void setLogicValue(boolean logicValue)
	{
		this.logicValue = logicValue;
	}
	
	public boolean isInUse() {
		return isInUse;
	}

	public void setInUse(boolean isInUse) {
		this.isInUse = isInUse;
	}

	@Override
	public boolean hasLogicType() {
		return true;
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
