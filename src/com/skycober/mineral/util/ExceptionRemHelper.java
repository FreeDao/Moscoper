package com.skycober.mineral.util;

import com.skycober.mineral.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
/**
 * 异常工具类
 * @author Yes366
 *
 */
public class ExceptionRemHelper {
	
	public static void showExceptionReport(Context context, String message){
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(R.string.common_dialog_title_error)
				.setMessage(message).setPositiveButton(R.string.common_dialog_btn_ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();
		dialog.show();
	}
}
