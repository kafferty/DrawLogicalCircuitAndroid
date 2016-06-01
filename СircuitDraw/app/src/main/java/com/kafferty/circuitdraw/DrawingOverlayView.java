package com.kafferty.circuitdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class DrawingOverlayView extends View {
	
	//Возможность рисовать линии по всему экрану
	//Линии здесь - провода для подключения логических элементов
	
	private int[] PAINT_COLOUR; // Инициализируется в  initializePaint()
	private int STROKE_WIDTH;
	
	private Canvas canvas;
	private Bitmap canvasBitmap;
	private Paint drawPaint;
	private Paint canvasPaint;
	private int currentPaintIndex;
	private Path currentPath;
	
	public DrawingOverlayView(Context context) {
		super(context);
		currentPath = new Path();
		initializePaint();
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		invalidate();
	}
	
	private void initializePaint()
	{
		//Массив цветов для проводов
		PAINT_COLOUR = new int[]{
				Color.RED,
				Color.BLUE,
				Color.GREEN,
				Color.WHITE,
				Color.MAGENTA,
				Color.YELLOW,
				Color.CYAN
		};
		currentPaintIndex = 0;
		STROKE_WIDTH = 5;
		
		drawPaint = new Paint();
		drawPaint.setColor(PAINT_COLOUR[currentPaintIndex]);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(STROKE_WIDTH);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}

	public DrawingOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawingOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	protected void onSizeChanged (int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(canvasBitmap);
	}
	
	protected void onDraw (Canvas canvas)
	{
		int offsetLeft = 0;
		int offsetTop = 0;
		
		canvas.drawBitmap(canvasBitmap, offsetLeft, offsetTop, canvasPaint);
		canvas.drawPath(currentPath, drawPaint);
	}
	
	public void startNewPathAt(float x, float y)
	{
		currentPath = new Path();
		currentPath.moveTo(x, y);
		invalidate();
	}
	
	public void continuePathAlong(float x, float y)
	{
			currentPath.lineTo(x, y);
			invalidate();
	}
	
	public void endPathAndDrawAt(boolean isValid, int[][] firstLineExtension, int[][] secondLineExtension)
	{
		// Стереть путь , если он не является действительным (за пределами логики).
		// Цикл до следующего цвета краски.
		// Соединяем концы нарисованных пользователем линий в соответствующие места лог.элемента.
		
		if (isValid)
		{
			canvas.drawPath(currentPath, drawPaint);

			if (null != firstLineExtension)
			{
				currentPath = new Path();
				currentPath.moveTo(firstLineExtension[0][0], firstLineExtension[0][1]);
				currentPath.lineTo(firstLineExtension[1][0], firstLineExtension[1][1]);
				canvas.drawPath(currentPath, drawPaint);
			}
			
			if (null != secondLineExtension)
			{
				currentPath = new Path();
				currentPath.moveTo(secondLineExtension[0][0], secondLineExtension[0][1]);
				currentPath.lineTo(secondLineExtension[1][0], secondLineExtension[1][1]);
				canvas.drawPath(currentPath, drawPaint);
			}
			
			currentPaintIndex++;
			if (currentPaintIndex == PAINT_COLOUR.length)
			{
				currentPaintIndex = 0;
			}

			drawPaint.setColor(PAINT_COLOUR[currentPaintIndex]);
		}
		
		invalidate();
		currentPath.reset();
	}

}

