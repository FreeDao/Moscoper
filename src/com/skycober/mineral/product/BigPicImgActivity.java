package com.skycober.mineral.product;

import net.tsz.afinal.FinalBitmap;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.StringUtil;
/**
 * ͼƬչʾҳ
 * @author Yes366
 *
 */
public class BigPicImgActivity extends BaseActivity {
	public static final int BIGPIC_FOR_RESULT=1333;

	public static final String KET_FOR_BIGPIC = "key_for_bigpic";
	private FinalBitmap finalBitmap;
	private	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_big_pic);
		ImageView bigpic = (ImageView) findViewById(R.id.big_img);
		finalBitmap = FinalBitmap.create(this);
		finalBitmap.configCalculateBitmapSizeWhenDecode(false);
		finalBitmap.configLoadingImage(R.drawable.mineral_logo);
		intent=getIntent();
		if(intent.hasExtra(KET_FOR_BIGPIC)){
			String url = intent.getExtras().getString(KET_FOR_BIGPIC);
			if (!StringUtil.getInstance().IsEmpty(url)) {
				url = RequestUrls.SERVER_BASIC_URL + "/" + url;
				finalBitmap.display(bigpic, url);
			} else {
				bigpic.setImageResource(R.drawable.mineral_logo);
			}
		}else if(intent.hasExtra("Path")){
			String url = intent.getStringExtra("Path");
			finalBitmap.display(bigpic, url);
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(RESULT_OK, intent);
			finish();
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			return false;
		}
		return false;
	}

}
