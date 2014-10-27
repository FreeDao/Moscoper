package com.skycober.mineral.company;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.bean.TagRec;

public class TagdetialActivity extends BaseActivity implements OnClickListener{
	private TextView name,address;
	private ImageButton butLeft ,butRight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_detial_activity);
		name = (TextView) findViewById(R.id.name);
		address = (TextView) findViewById(R.id.address);
		
		butLeft = (ImageButton) findViewById(R.id.title_button_left);
		butLeft.setImageResource(R.drawable.back_btn_selector);
		butLeft.setOnClickListener(this);
		butRight = (ImageButton) findViewById(R.id.title_button_right);
		butRight.setVisibility(View.GONE);
		
		Intent intent = getIntent();
		String jsonStr = intent.getStringExtra("jsonStr");
		TagRec tag = getTagDetial(jsonStr);
		name.setText(tag.getName());
		address.setText(tag.getAddress());
	}
	
	public TagRec getTagDetial(String jsonStr){
		TagRec tag = new TagRec();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			
			tag.setId(jsonObject.getString("gtin"));
			tag.setName(jsonObject.getString("name"));
			tag.setAddress(jsonObject.getString("manu"));
			
//			Toast.makeText(getApplicationContext(), tag.toString(), 1).show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tag;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_button_left:
			finish();
			break;

		}
	}
	
	
}
