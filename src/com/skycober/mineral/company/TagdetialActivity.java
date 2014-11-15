package com.skycober.mineral.company;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.bean.TagRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.SettingUtil;

public class TagdetialActivity extends BaseActivity implements OnClickListener {
	private TextView name, address;
	private ImageButton butLeft, butRight;
	private Button attent;
	private String eid;
	private TagRec tag;
	private String barcodes;
	private String tag_ids;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_detial_activity);

		name = (TextView) findViewById(R.id.name);
		address = (TextView) findViewById(R.id.address);
		attent = (Button) findViewById(R.id.attion);
		attent.setOnClickListener(this);

		butLeft = (ImageButton) findViewById(R.id.title_button_left);
		butLeft.setImageResource(R.drawable.back_btn_selector);
		butLeft.setOnClickListener(this);
		butRight = (ImageButton) findViewById(R.id.title_button_right);
		butRight.setVisibility(View.GONE);

		Intent intent = getIntent();
		String jsonStr = intent.getStringExtra("jsonStr");
		eid = intent.getStringExtra("eid");
		barcodes = intent.getStringExtra("barcodes");
		tag_ids = intent.getStringExtra("tag_ids");
		System.out.println("===tag_ids===="+tag_ids);
		tag = getTagDetial(jsonStr);
		name.setText(tag.getId());
		address.setText(tag.getAddress());
	}

	public TagRec getTagDetial(String jsonStr) {
		TagRec tag = new TagRec();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);

			tag.setId(jsonObject.getString("gtin"));
			tag.setName(jsonObject.getString("name"));
			tag.setAddress(jsonObject.getString("manu"));

			// Toast.makeText(getApplicationContext(), tag.toString(),
			// 1).show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tag;
	}

	/**
	 * Ìí¼Ó¹Ø×¢
	 */
	public void addAttentionTag() {
		AjaxParams params = new AjaxParams();
		params.put("eid", eid);
		// String tag_ids = "";
		// for (int i = 0; i < attention_tag.size(); i++) {
		// if ("".equals(tag_ids)) {
		// tag_ids = attention_tag.get(0).getTag_id();
		// } else {
		// tag_ids = tag_ids + "," + attention_tag.get(i).getTag_id();
		// }
		// }
		// System.out.println("==tag_ids===" + tag_ids);
		params.put("barcodes", barcodes);
		params.put("tag_ids", tag_ids);

		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(this).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.COMPANY_ADD_ATTENTION_TAG, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onSuccess(Object t, HttpResponse response) {
						// TODO Auto-generated method stub
						super.onSuccess(t, response);
						try {
							JSONObject jsonObject = new JSONObject(t.toString());
							System.out.println("==addAttentionTag==="
									+ jsonObject.toString());
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_button_left:
			finish();
			break;
		case R.id.attion:
			addAttentionTag();
			break;

		}
	}

}
