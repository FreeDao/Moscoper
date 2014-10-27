package com.skycober.mineral.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.skycober.mineral.R;


public class CommonRemDialog extends Dialog{
	private Button btnTop;
	private Button btnDown;
	private Button btnCancel;
	public CommonRemDialog(Context context) {
		super(context, R.style.dialog);
		Init(context);
	}

	public CommonRemDialog(Context context, int theme) {
		super(context, R.style.dialog);
		Init(context);
	}

	private void Init(Context context) {
		ViewGroup group = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.common_rem_dialog_item, null);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		setContentView(group, new LayoutParams(wm.getDefaultDisplay()
				.getWidth() , LayoutParams.WRAP_CONTENT));
		Window window = this.getWindow();
//		window.setWindowAnimations(R.style.mystyle);
		window.setGravity(Gravity.BOTTOM);
//		WindowManager.LayoutParams wl = window.getAttributes();
//		wl.x = 0;
//		wl.y = wm.getDefaultDisplay().getHeight();
//		this.onWindowAttributesChanged(wl);
		this.setCanceledOnTouchOutside(true);
		btnTop = (Button) group.findViewById(R.id.btnTop);
		btnTop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
				if (null != onButtonTopClickListener) {
					onButtonTopClickListener.onClick(v);
				}
			}
		});
		btnDown = (Button) group.findViewById(R.id.btnDown);
		btnDown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
				if (null != onBottomClickListener) {
					onBottomClickListener.onClick(v);
				}
			}
		});
		btnCancel = (Button) group.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
			}
		});
	}
	
	public void setTopBtnText(String top){
		if(null != btnTop){
			btnTop.setText(top);
		}
	}
	
	public void setDownBtnText(String down){
		if(null != btnDown){
			btnDown.setText(down);
		}
	}
	
	public void setCancelBtnText(String cancel){
		if(null != btnCancel){
			btnCancel.setText(cancel);
		}
	}

	private View.OnClickListener onButtonTopClickListener;

	public View.OnClickListener getButtonTopClickListener() {
		return onButtonTopClickListener;
	}

	public void setOnButtonTopClickListener(View.OnClickListener buttonTopClickListener) {
		this.onButtonTopClickListener = buttonTopClickListener;
	}

	private View.OnClickListener onBottomClickListener;

	public View.OnClickListener getOnButtonDownClickListener() {
		return onBottomClickListener;
	}

	public void setOnButtonDownClickListener(View.OnClickListener butttonDownClickListener) {
		this.onBottomClickListener = butttonDownClickListener;
	}

}
