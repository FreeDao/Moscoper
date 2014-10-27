package com.skycober.mineral.product;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseRandom;
import com.skycober.mineral.product.ShakeListener.OnShakeListener;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.CalendarUtil;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;

/**
 * ҡһҡ
 * 
 * @author �±�
 * 
 */
public class RandReviewActivity extends FragBaseActivity {
	private static final String TAG = "RandReviewActivity";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FragmentChangeActivity.slideMenu.setTag("back");
		final View v = inflater.inflate(R.layout.activity_rand_review, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.random_review_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) v
				.findViewById(R.id.title_button_right);
		btnRight.setImageResource(R.drawable.shake_btn_selector);
		init(v);
		btnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				startToGetRandom(getActivity(), v);
			}
		});

		mShakeListener = new ShakeListener(getActivity());
		mShakeListener.setOnShakeListener(new OnShakeListener() {

			@Override
			public void onShake(Context context) {
				// TODO Auto-generated method stub
				startToGetRandom(context, v);
			}
		});
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mShakeListener.start();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mShakeListener.stop();
	}

	private TextView shakeViewCount;
	private TextView shakeFavCount;

	private void initInfo(View v, final ProductRec mproductRec) {
		ImageView shakeAvatar = (ImageView) v.findViewById(R.id.shake_avatar);
		FinalBitmap bm = FinalBitmap.create(getActivity());
		bm.configCalculateBitmapSizeWhenDecode(false);
		bm.configLoadingImage(R.drawable.default_mineral);
		String avatar = mproductRec.getThumb();
		if (!StringUtil.getInstance().IsEmpty(avatar)) {
			String url = RequestUrls.SERVER_BASIC_URL + "/" + avatar;
			Log.e("avatar=", avatar);
			bm.display(shakeAvatar, url);
			bm.configLoadfailImage(R.drawable.default_mineral);

		} else {
			shakeAvatar.setImageResource(R.drawable.default_mineral);
		}
		TextView shakeName = (TextView) v.findViewById(R.id.shake_name);
		shakeName.setText(mproductRec.getName());
		TextView shakeDescription = (TextView) v
				.findViewById(R.id.shake_description);
		shakeDescription.setText(mproductRec.getDescription());
		TextView shakePhotoCountTextView = (TextView) v
				.findViewById(R.id.shake_photo_count);
		List<PicRec> picList = mproductRec.getPicList();
		int picCount = ((null == picList) ? 0 : picList.size());
		shakePhotoCountTextView.setText(String.valueOf(picCount));
		shakeFavCount = (TextView) v
				.findViewById(R.id.shake_fav_count);
//		String fvaNum=String.valueOf(mproductRec.getFavNum());
//		Log.e("fvaNum", fvaNum);
//		shakeFavCount.setText(fvaNum);
		shakeViewCount = (TextView) v.findViewById(R.id.shake_view_count);
	
//		shakeViewCount.setText(String.valueOf(mproductRec.getClickCount()));

		TextView shakeSendDate = (TextView) v
				.findViewById(R.id.shake_send_date);
		Long sendDate = Long.valueOf(mproductRec.getAddTime());
		shakeSendDate
				.setText(CalendarUtil.GetPostCommentTime(sendDate * 1000l));
		beautyInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(getActivity(),
						ProductDetailActivity.class);
				mIntent.putExtra(ProductDetailActivity.KEY_PRODUCT_REC,
						productRec);
				startActivityForResult(mIntent, REQUEST_CODE_SHOW_PRODUCT);
			}
		});
		beautyInfo.setVisibility(View.VISIBLE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_SHOW_PRODUCT:
			if (data != null) {
				productRec = (ProductRec) data
						.getSerializableExtra(ProductDetailActivity.KEY_PRODUCT_REC);
//				String favNum=String.valueOf(productRec.getFavNum());
//				shakeFavCount.setText(favNum);
			}
			break;
		default:
			break;
		}
	}

	private final int REQUEST_CODE_SHOW_PRODUCT = 1022;
	private LinearLayout beautyInfo;
	private ProductRec productRec;
	private void init(final View v) {
		beautyInfo = (LinearLayout) v.findViewById(R.id.beauty_info);
		beautyInfo.setVisibility(View.GONE);
	}

	ShakeListener mShakeListener = null;
	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};

	private void startToGetRandom(Context context, final View v) {
		if (!NetworkUtil.getInstance().existNetwork(context)) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		final ImageView shake_loading = (ImageView) v
				.findViewById(R.id.shake_loading);
		shake_loading.setVisibility(View.VISIBLE);
		final String currMethod = "startToGetRandom:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				shake_loading.setVisibility(View.GONE);
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseGetRandomGoods(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseRandom response = (ResponseRandom) br;
						productRec = response.getProductRec();
//						Log.e("productRec", productRec.getFavNum()+"");
						initInfo(v, productRec);
					} else {
						String message = getString(R.string.shake_failed_rem);
						String msg = getString(R.string.shake_failed_rem);
						if (null == br) {
							msg = "parseChanceFav: Result:BaseResponse is null.";
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
							ExceptionRemHelper.showExceptionReport(
									getActivity(), msg);
						} else {
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(getActivity(),
								msg);
					} else {
						Toast.makeText(getActivity(),
								R.string.shake_failed_rem, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(), R.string.shake_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
				super.onFailure(t, strMsg);
			}

		};
		gs.GetRandomGoods(getActivity(), callBack);
	}

}
