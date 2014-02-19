package com.planb.soda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class RateLayout extends RelativeLayout {
	public TextView txtRate=null;
	public RatingBar rateBar=null;
	public RateLayout(Context context,int screenW) {
		super(context);
		float[] outerR = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
		ShapeDrawable rect = new ShapeDrawable(new RoundRectShape(outerR, null, null));
		rect.setBounds(0, 0, (int) (screenW * 0.21875), (int) (screenW * 0.09375));
		Paint paint = rect.getPaint();
		paint.setColor(0x00FFFFFF);
		this.setBackground(rect);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    inflater.inflate(com.planb.soda.R.layout.rate_layout, this, true);

		RelativeLayout.LayoutParams rlpForThis = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForThis.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlpForThis.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlpForThis.height = (int) (screenW * 0.09375);
		rlpForThis.width = (int) (screenW * 0.21875);
		rlpForThis.bottomMargin=(int) (ShareVariable.screenW * 0.09375+22);
		rlpForThis.rightMargin=(int) (22);
		this.setLayoutParams(rlpForThis);

		rateBar =(RatingBar) ((RelativeLayout) this.getChildAt(0)).getChildAt(1);
		rateBar.setRating((float) 0.00);

		
		RelativeLayout.LayoutParams rlpForRateBar= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForRateBar.addRule(RelativeLayout.CENTER_VERTICAL);
		rlpForRateBar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rlpForRateBar.leftMargin=(int) (ShareVariable.screenW * 0.015416666);
		if(ShareVariable.screenW==1080){
			rlpForRateBar.height=80;
			rlpForRateBar.width=80;
		}else{
			//no config adapter container
		}
		rateBar.setLayoutParams(rlpForRateBar);	
		
		txtRate =(TextView) ((RelativeLayout) this.getChildAt(0)).getChildAt(0);
		txtRate.setTextColor(0x00FFFFFF);
		txtRate.setText(String.valueOf(rateBar.getRating()));
		RelativeLayout.LayoutParams rlpForTxtRate= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForTxtRate.addRule(RelativeLayout.CENTER_VERTICAL);
		rlpForTxtRate.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		if(ShareVariable.screenW==1080){
			txtRate.setTextSize(26);
			rlpForTxtRate.rightMargin=(int) (screenW*0.01041666);
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
			txtRate.setTextSize(26);
			rlpForTxtRate.rightMargin=(int) (screenW*0.01041666);
		}else{
			txtRate.setTextSize((int) (screenW*0.036041666));
			rlpForTxtRate.rightMargin=(int) (screenW*0.01041666);
		}
		txtRate.setLayoutParams(rlpForTxtRate);
		// TODO Auto-generated constructor stub
		txtRate.bringToFront();
		rateBar.bringToFront();
	}
	
	public void setRating(float rate){
		rateBar.setRating(rate);
		//Log.d("test","test width:" + String.valueOf(rateBar.getVerticalScrollbarWidth()));
	}
}
