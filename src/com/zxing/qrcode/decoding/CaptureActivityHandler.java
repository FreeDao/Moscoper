/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.qrcode.decoding;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.skycober.mineral.R;
import com.skycober.mineral.company.TagdetialActivity;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.setting.SweepActivity;
import com.zxing.qrcode.CameraManager;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class
			.getSimpleName();

	private final SweepActivity activity;
	private final DecodeThread decodeThread;
	private State state;
	private String eid;
	private String tag_ids;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(SweepActivity activity,
			Vector<BarcodeFormat> decodeFormats, String characterSet,String eid,String tag_ids) {
		this.eid = eid;
		this.tag_ids = tag_ids;
		this.activity = activity;
		decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
				new com.skycober.mineral.widget.ViewfinderResultPointCallback(
						activity.getViewfinderView()));
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.auto_focus:
			// Log.d(TAG, "Got auto-focus message");
			// When one auto focus pass finishes, start another. This is the
			// closest thing to
			// continuous AF. It does seem to hunt a bit, but I'm not sure what
			// else to do.
			if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
			}
			break;
		case R.id.restart_preview:
			Log.d(TAG, "Got restart preview message");
			restartPreviewAndDecode();
			break;
		case R.id.decode_succeeded:
			Log.d(TAG, "Got decode succeeded message");
			state = State.SUCCESS;
			Bundle bundle = message.getData();
			Bitmap barcode = bundle == null ? null : (Bitmap) bundle
					.getParcelable(DecodeThread.BARCODE_BITMAP);
			final String str_result = ((Result) message.obj).getText();
			System.out.println("====str_result===="+str_result);
			String match = "^[0-9]{13}";
			boolean b = str_result.matches(match);
			// Toast.makeText(activity, ""+b, 1).show();
			// activity.handleDecode((Result) message.obj, barcode);
			if (b) {
				String url = RequestUrls.TIAOXING_CODE_URL.replace("[code]",
						str_result);
				FinalHttp fh = new FinalHttp();
				fh.get(url, new AjaxCallBack<Object>() {
					@Override
					public void onSuccess(Object t) {
						// TODO Auto-generated method stub
						super.onSuccess(t);
						try {
							
							System.out.println("===TIAOXING_CODE_URL===="
									+ new JSONObject(t.toString()));
							Intent tagIntent = new Intent(activity,TagdetialActivity.class);
							tagIntent.putExtra("jsonStr", t.toString());
							tagIntent.putExtra("barcodes", str_result);
							tagIntent.putExtra("eid", eid);
							tagIntent.putExtra("tag_ids", tag_ids);
							activity.startActivity(tagIntent);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						// TODO Auto-generated method stub
						super.onFailure(t, strMsg);
					}
				});
			} else {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					// //// intent.setPackage("com.tencent.mm");//直接打开微信
					intent.putExtra(Intent.EXTRA_SUBJECT, "share");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// // 这是你公共帐号的二维码的实际内容。可以用扫描软件扫一下就得到了。这是我的公共帐号地址。这一句的位置挺关键
					// intent.setData(Uri.parse("http://weixin.qq.com/r/o3w_sRvEMSVOhwrSnyCH"));
					intent.setData(Uri.parse(str_result));
					activity.startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(activity, R.string.no_wechat_rem,
							Toast.LENGTH_SHORT).show();
				}
			}

			break;
		case R.id.decode_failed:
			// We're decoding as fast as possible, so when one decode fails,
			// start another.
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			break;
		case R.id.return_scan_result:
			Log.d(TAG, "Got return scan result message");
			activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
			activity.finish();
			break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get().stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			decodeThread.join();
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		// removeMessages(R.id.return_scan_result);
		removeMessages(R.id.decode_failed);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
			activity.drawViewfinder();
		}
	}

}
