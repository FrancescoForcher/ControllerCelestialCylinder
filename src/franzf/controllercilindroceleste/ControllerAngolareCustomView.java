package franzf.controllercilindroceleste;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ControllerAngolareCustomView extends View {
	
	Paint penna = new Paint();
	int AngoloCerchioStep=0;
	double AngoloCerchioRadianti=0;
	Point centro = new Point();
	float xTouch;
	float yTouch;

	public ControllerAngolareCustomView(Context context) {
		super(context);
		init(null, 0,context);
	}

	public ControllerAngolareCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0,context);
	}

	public ControllerAngolareCustomView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle,context);
	}

	private void init(AttributeSet attrs, int defStyle , Context context) {
		penna.setAntiAlias(true);
		penna.setDither(true);
		penna.setColor(Color.GREEN);
		penna.setStyle(Style.STROKE);
	}



	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//Disegna il cerchio esterno
		int larghezza = canvas.getWidth();
		int altezza = canvas.getHeight();
		centro.set(larghezza/2,altezza/2);
		int rMax = Math.min(larghezza/2, altezza/2);
		int raggio = rMax;
		AngoloCerchioRadianti = AngoloCerchioStep*0.098175d;
		
		canvas.drawCircle(centro.x, centro.y, raggio-2, penna);
		canvas.drawLine((float)centro.x, 
						(float)centro.y,
						(float)(centro.x + raggio*Math.cos(AngoloCerchioRadianti)-2), 
						(float)(centro.y+raggio*Math.sin(AngoloCerchioRadianti)-2),
						penna);

	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 * 
	 * Sposta l'angolo sul tocco
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {
	    float x = e.getX();
	    float y = e.getY();
	    
	    if (e.getActionMasked()==MotionEvent.ACTION_DOWN){
	    	xTouch=x;
	    	yTouch=y;
	    }
	    double angoloRad=Math.atan2(yTouch,yTouch);
	    //int angoloCerchioStep
	    
	    return true;
	}

}
