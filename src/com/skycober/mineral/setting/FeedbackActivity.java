package com.skycober.mineral.setting;

import net.tsz.afinal.annotation.view.ViewInject;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * 反馈信息
 * @author Yes366
 *
 */
public class FeedbackActivity extends BaseActivity {

	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initTopBar();
	}

	private void initTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnRight.setImageResource(R.drawable.check_btn_selector);
		btnRight.setOnClickListener(btnRightClickListener);
		tvTitle.setText(R.string.feedback_page_title);
	}

	private View.OnClickListener btnRightClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO 提交反馈
		}
	};
}
