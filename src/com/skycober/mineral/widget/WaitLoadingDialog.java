package com.skycober.mineral.widget;


import com.skycober.mineral.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class WaitLoadingDialog extends Dialog {
	private LayoutInflater inflater;
	private View contentView;
	private String message;

	public WaitLoadingDialog(Context context, int theme) {
		super(context, R.style.dialog);
	}

	protected WaitLoadingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public WaitLoadingDialog(Context context) {
		super(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		contentView = inflater.inflate(R.layout.wait_dialog_content_view, null);
		setContentView(contentView);
		setCanceledOnTouchOutside(false);
	}

	public void setMessage(String message) {
		this.message = message;
		TextView tvMessage = (TextView) contentView
				.findViewById(R.id.tvDialogMessage);
		tvMessage.setText(this.message);
	}
}
