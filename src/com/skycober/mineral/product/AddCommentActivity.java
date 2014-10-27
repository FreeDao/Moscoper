package com.skycober.mineral.product;

import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
/**
 * Ìí¼ÓÆÀÂÛ
 * @author Yes366
 *
 */
public class AddCommentActivity extends BaseActivity {
	private static final String TAG = "AddCommentActivity";
	
	@ViewInject(id = R.id.title_button_left) ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right) ImageButton btnRight;
	@ViewInject(id = R.id.title_text) TextView tvTitle;
	
	@ViewInject(id = R.id.content_edit_text) EditText etContent;
	@ViewInject(id = R.id.rateBar_01) RatingBar rankBar;
	
	public static final String KEY_PRODUCT_ID = "key_product_id";
	private String prodId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_comment);
		InitTopBar();
		Intent startIntent = getIntent();
		if(null != startIntent && startIntent.hasExtra(KEY_PRODUCT_ID)){
			prodId = startIntent.getStringExtra(KEY_PRODUCT_ID);
		}
	}

	private void InitTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);
		btnRight.setImageResource(R.drawable.check_btn_selector);
		btnRight.setOnClickListener(btnRightClickListener);
		tvTitle.setText(R.string.add_comment_page_title);
	}
	
	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	private View.OnClickListener btnRightClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!v.isEnabled()) return;
			v.setEnabled(false);
			readyToSendComment();
			v.setEnabled(true);
		}
	};

	protected void readyToSendComment() {
		if(!NetworkUtil.getInstance().existNetwork(this)){
			Toast.makeText(this, R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		String content = etContent.getText().toString();
		if(StringUtil.getInstance().IsEmpty(content)){
			Toast.makeText(this, R.string.comment_content_not_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		int rank = rankBar.getProgress();
		if(rank <= 0){
			rank = 1;
		}else if(rank > 5){
			rank = 5;
		}
		lockScreen(getString(R.string.comment_send_ing));
		final String currMethod = "readyToSendComment:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(
							AddCommentActivity.this, msg);
				} else {
					Toast.makeText(AddCommentActivity.this,
							R.string.send_comment_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}
			
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseAddComment(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						setResult(RESULT_OK);
						finish();
					} else {
						String message = getString(R.string.send_comment_failed_rem);
						String msg = getString(R.string.send_comment_failed_rem);
						if (null == br) {
							msg = "readyMakeOnSale: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(AddCommentActivity.this, msg);
						} else {
							Toast.makeText(AddCommentActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(AddCommentActivity.this, msg);
					} else {
						Toast.makeText(AddCommentActivity.this, R.string.send_comment_failed_rem, Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		gs.AddComment(this, prodId, content, rank, callBack);
	}
	
}
