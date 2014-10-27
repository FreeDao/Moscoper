package com.skycober.mineral.widget;

import com.skycober.mineral.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyRemDialog extends Dialog {
	private ImageView ivIcon;
	private TextView tvTitle;
	private TextView tvMessage;
	private Button btnOk;
	private Button btnCancel;
	private ViewGroup headerLayout;
	private View.OnClickListener posBtnClickListener;
	private View.OnClickListener negBtnClickListener;
	private View.OnClickListener cancelClickListener;
private boolean isBackClose=true;
	public MyRemDialog(Context context) {
		super(context);
		Init(context);
	}

	public MyRemDialog(Context context, int theme) {
		super(context, theme);
		Init(context);
	}
	public MyRemDialog(Context context, int theme,boolean isbackClose) {
		super(context, theme);
		Init(context);
		this.isBackClose=isbackClose;
	}
	private void Init(Context context) {
		ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.custom_dialog, null);
		headerLayout = (ViewGroup) root.findViewById(R.id.title_layout);
		ivIcon = (ImageView) root.findViewById(R.id.icon);
		tvTitle = (TextView) root.findViewById(R.id.title);
		tvMessage = (TextView) root.findViewById(R.id.message);
		btnOk = (Button) root.findViewById(R.id.positiveButton);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
				if (null != posBtnClickListener) {
					posBtnClickListener.onClick(v);
				}
			}
		});
		btnCancel = (Button) root.findViewById(R.id.negativeButton);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
				if (null != negBtnClickListener) {
					negBtnClickListener.onClick(v);
				}
			}
		});
		this.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (null != cancelClickListener) {
					cancelClickListener.onClick(null);
				}
			}
		});
		this.setContentView(root);
		Window dialogWindow = this.getWindow();
		WindowManager m = ((Activity)context).getWindowManager();
		Display d = m.getDefaultDisplay(); 
		WindowManager.LayoutParams p = getWindow().getAttributes(); 
		p.width = (int) (d.getWidth() * 0.9); 
		dialogWindow.setAttributes(p);
		
	}

	public View.OnClickListener getPosBtnClickListener() {
		return posBtnClickListener;
	}

	public void setPosBtnClickListener(View.OnClickListener posBtnClickListener) {
		this.posBtnClickListener = posBtnClickListener;
	}

	public View.OnClickListener getNegBtnClickListener() {
		return negBtnClickListener;
	}

	public void setNegBtnClickListener(View.OnClickListener negBtnClickListener) {
		this.negBtnClickListener = negBtnClickListener;
	}

	public View.OnClickListener getCancelClickListener() {
		return cancelClickListener;
	}

	public void setCancelClickListener(View.OnClickListener cancelClickListener) {
		this.cancelClickListener = cancelClickListener;
	}

	public void setIcon(int resId) {
		try {
			if (null != ivIcon)
				ivIcon.setBackgroundResource(resId);
		} catch (Exception e) {

		}
	}

	public void setTitle(String title) {
		if (null != tvTitle)
			tvTitle.setText(title);
	}

	public void setTitle(int titleResId) {
		try {
			if (null != tvTitle) {
				this.tvTitle.setText(titleResId);
			}
		} catch (Exception e) {
			
		}
	}
	
	public void setHeaderVisility(int visibility){
		try {
			if(null != headerLayout){
				headerLayout.setVisibility(visibility);
			}
		} catch (Exception e) {
			
		}
	}

	public void setMessage(String message) {
		if (null != tvMessage)
			tvMessage.setText(message);
	}

	public void setMessage(int msgResId) {
		try {
			if (null != tvMessage) {
				this.tvMessage.setText(msgResId);
			}
		} catch (Exception e) {

		}
	}

	public void setPosBtnText(String posButtonText) {
		if (null != btnOk)
			btnOk.setText(posButtonText);
	}

	public void setPosBtnText(int posButtonTextId) {
		try {
			if (null != btnOk)
				btnOk.setText(posButtonTextId);
		} catch (Exception e) {
		}
	}

	public void setNegBtnText(String negButtonText) {
		if (null != btnCancel)
			btnCancel.setText(negButtonText);
	}

	public void setNegBtnText(int negButtonTextId) {
		try {
			if (null != btnCancel)
				btnCancel.setText(negButtonTextId);
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (!isBackClose) {
				break;
			}else{
				dismiss();
			}
			break;

		default:
			break;
		}
		
		return isBackClose;
	}
	
}
