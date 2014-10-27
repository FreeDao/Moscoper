package com.skycober.mineral.account;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;

import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * ’“ªÿ√‹¬Î
 * @author Yes366
 *
 */
public class FindPassActivity extends BaseActivity {

	@ViewInject(id = R.id.title_button_left) ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right) ImageButton btnRight;
	@ViewInject(id = R.id.title_text) TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_pass);
		InitTopBar();
	}

	private void InitTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);
		btnRight.setVisibility(View.INVISIBLE);
		tvTitle.setText(R.string.find_pass_page_title);
	}
	
	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
}
