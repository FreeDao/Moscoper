package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbConstant;
import com.ab.net.AbHttpCallback;
import com.ab.net.AbHttpItem;
import com.ab.view.AbPullToRefreshListView;
import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.KeyWordsListRec;
import com.skycober.mineral.bean.KeyWordsRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseKeyWords;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.widget.AbPullToRefreshGridView;

/**
 * 关键字浏览
 * 
 * @author Yes366
 * 
 */
public class SelectKeyWordsActivity extends BaseActivity {
	private static final String TAG = "SelectKeyWordsActivity";

	private List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
	private List<KeyWordsRec> myRecs = new ArrayList<KeyWordsRec>();
	private List<KeyWordsListRec> list = new ArrayList<KeyWordsListRec>();
	private AbPullToRefreshListView gridView;
	private KeyWordsItemAdapter adapter, myAdapter;
	private int count = 20, offset = 0;
	private int have = AbConstant.HAVE;
	private String tagCatId;
	private String sTagId;
	private String search = null;
	private ListView myWords;
	private EditText fb_search_edt;
	private Button fb_search_btn;
	private RelativeLayout add_layout;
	private String mySelect = null;
	private KeyWordsListRec keyWordsListRec;
	private boolean isSelected;
	private List<KeyWordsRec> tempMyRecs = new ArrayList<KeyWordsRec>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_words);
		initTitle();
		initdate();

		init();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// myRecs.clear();
	}

	private void initdate() {
		// TODO Auto-generated method stub
		ArrayList<String> listName = new ArrayList<String>();
		ArrayList<String> listId = new ArrayList<String>();
		Intent intent = getIntent();
		tagCatId = intent.getExtras().getString("TAG_CAT_ID");
		listName = intent.getStringArrayListExtra("listName");
		listId = intent.getStringArrayListExtra("listId");

		// myRecs.clear();
		boolean isHave = false;
		if (listId != null) {
			if(listId.size()> 0){
				isSelected = true;
			}
			for (int i = 0; i < listId.size(); i++) {

				for (int j = 0; j < myRecs.size(); j++) {
					if (listId.get(i).equals(myRecs.get(j).getTagID())) {
						isHave = true;

					}
				}
				if (!isHave) {
					isHave = false;
					KeyWordsRec keyWordsRec = new KeyWordsRec();
					keyWordsRec.setIschacked(true);
					keyWordsRec.setTagID(listId.get(i));
					keyWordsRec.setTagName(listName.get(i));
					myRecs.add(keyWordsRec);
					tempMyRecs.add(keyWordsRec);
				}

			}
		}

		Log.e("anshuai", listName + "");

	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			if (count == 0) {
				search = null;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if ("".equals(s.toString())) {
				recs.clear();
				getKeyWordsList(true, sTagId, null, mySelect);
			}
		}
	};

	// 初始化、搜索
	private void init() {
		// TODO Auto-generated method stub
		fb_search_edt = (EditText) findViewById(R.id.fb_search_edt);
		fb_search_btn = (Button) findViewById(R.id.fb_search_btn);
		add_layout = (RelativeLayout) findViewById(R.id.add);

		fb_search_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				search = fb_search_edt.getText().toString();
				String s = search.trim();
				offset = 0;
				if (search == null || "".equals(s)) {
					Toast.makeText(getApplicationContext(), "请输入合法关键字！！", 1)
							.show();
				} else {
					recs.clear();
					getKeyWordsList(true, sTagId, fb_search_edt.getText()
							.toString(), mySelect);
				}

			}
		});
		fb_search_edt.addTextChangedListener(textWatcher);
		gridView = (AbPullToRefreshListView) findViewById(R.id.lv_key_words);
		myWords = (ListView) findViewById(R.id.my_select_key_words);
		myAdapter = new KeyWordsItemAdapter(this,
				R.layout.adapter_selectwords_item, myRecs);
		myWords.setAdapter(myAdapter);
		adapter = new KeyWordsItemAdapter(this,
				R.layout.adapter_selectwords_item, recs);
		// 当热词被选择或取消选择
		adapter.setOnSelectListener(new OnSelectClickListener() {

			@Override
			public void onSelect(int number) {
				if (number < recs.size()) {
					recs.get(number).setIschacked(
							!recs.get(number).isIschacked());
					sTagId = recs.get(number).getTagID();
					if (recs.get(number).isIschacked()) {
						boolean Ishas = false;
						for (int i = 0; i < myRecs.size(); i++) {
							if (recs.get(number).getTagID()
									.equals(myRecs.get(i).getTagID()))
								Ishas = true;
						}
						if (!Ishas)
							myRecs.add(recs.get(number));
						Ishas = false;
					}
					myAdapter.setKeyWordsList(myRecs);
					myAdapter.notifyDataSetChanged();
					recs.clear();
					count = 20;
					offset = 0;
					if (mySelect == null) {
						mySelect = sTagId;
					} else {
						mySelect = mySelect + "," + sTagId;
					}
					getKeyWordsList(true, sTagId, null, mySelect);
					sTagId = null;
				}

			}
		});
		// 当我选的取消选择
		myAdapter.setOnSelectListener(new OnSelectClickListener() {

			@Override
			public void onSelect(int number) {
				// TODO Auto-generated method stub
				if (number < myRecs.size()) {
					myRecs.remove(number);
					myAdapter.setKeyWordsList(myRecs);
					myAdapter.notifyDataSetChanged();
				}

			}
		});

		gridView.setAdapter(adapter);
		AbHttpItem item = new AbHttpItem();
		item.callback = new AbHttpCallback() {

			@Override
			public void update() {
				// TODO Auto-generated method stub

			}

			@Override
			public void get() {
				// TODO Auto-generated method stub
				search = null;
				recs.clear();
				count = 20;
				offset = 0;
				getKeyWordsList(true, sTagId, search, null);
			}
		};
		gridView.setRefreshItem(item);

		AbHttpItem item1 = new AbHttpItem();
		item1.callback = new AbHttpCallback() {

			@Override
			public void update() {
				// TODO Auto-generated method stub
				gridView.onScrollComplete(have);
			}

			@Override
			public void get() {
				// TODO Auto-generated method stub
				if (have == AbConstant.HAVE)
					getKeyWordsList(false, sTagId, search, mySelect);
			}
		};
		gridView.setScrollItem(item1);
		getKeyWordsList(true, sTagId, null, null);
	}

	private String keywordsStr = "";

	private void initTitle() {
		// TODO Auto-generated method stub
		TextView tvTitle = (TextView) findViewById(R.id.title_text);
		tvTitle.setText(R.string.select_key_words_title);
		ImageButton btnLeft = (ImageButton) findViewById(R.id.title_button_left);
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.VISIBLE);
		btnRight.setImageResource(R.drawable.check_btn_selector);
		btnRight.setOnClickListener(btnrightClickListener);
	}

	// 确认
	private View.OnClickListener btnrightClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			Intent mIntent = new Intent();
			keyWordsListRec = new KeyWordsListRec();
			List<KeyWordsRec> intentList = new ArrayList<KeyWordsRec>();
			for (int i = 0; i < myRecs.size(); i++) {
				if (myRecs.get(i).isIschacked()) {
					intentList.add(myRecs.get(i));
				}
			}
			keyWordsListRec.setRecs(intentList);
			mIntent.putExtra("KYEWORDS_KEY", keyWordsListRec);
			setResult(RESULT_OK, mIntent);
			SelectKeyWordsActivity.this.finish();
			v.setEnabled(true);

		}
	};
	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent();
//			KeyWordsListRec keyWordsListRec = new KeyWordsListRec();
//			List<KeyWordsRec> intentList = new ArrayList<KeyWordsRec>();
//			for (int i = 0; i < myRecs.size(); i++) {
//				if (myRecs.get(i).isIschacked()) {
//					intentList.add(myRecs.get(i));
//				}
//			}
//			keyWordsListRec.setRecs(intentList);
			keyWordsListRec = new KeyWordsListRec();
			if(isSelected){
				List<KeyWordsRec> intentList = new ArrayList<KeyWordsRec>();
				for (int i = 0; i < tempMyRecs.size(); i++) {
					if (tempMyRecs.get(i).isIschacked()) {
						intentList.add(tempMyRecs.get(i));
					}
				}
				keyWordsListRec.setRecs(intentList);
			}
			mIntent.putExtra("KYEWORDS_KEY", keyWordsListRec);
			setResult(RESULT_OK, mIntent);
			SelectKeyWordsActivity.this.finish();
//			finish();
		}
	};

	public class KeyWordsItemAdapter extends BaseAdapter {
		private List<KeyWordsRec> keyWordsList;
		private int layoutId;
		private LayoutInflater inflater;
		private OnSelectClickListener onSelectListener;

		public OnSelectClickListener getOnSelectListener() {
			return onSelectListener;
		}

		public void setOnSelectListener(OnSelectClickListener onSelectListener) {
			this.onSelectListener = onSelectListener;
		}

		public KeyWordsItemAdapter(Context context, int layoutId,
				List<KeyWordsRec> recs) {
			inflater = LayoutInflater.from(context);
			this.layoutId = layoutId;
			this.keyWordsList = recs;
		}

		public List<KeyWordsRec> getKeyWordsList() {
			return keyWordsList;
		}

		public void setKeyWordsList(List<KeyWordsRec> res) {
			this.keyWordsList = res;
		}

		@Override
		public int getCount() {
			return keyWordsList.size();
		}

		@Override
		public Object getItem(int position) {
			return keyWordsList.get(position);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(final int pos, View view, ViewGroup parent) {
			if (null == view) {
				view = inflater.inflate(layoutId, null);
			}
			RelativeLayout layout_keywords = (RelativeLayout) view
					.findViewById(R.id.layout_keywords);
			KeyWordsRec keyWords = keyWordsList.get(pos);
			TextView tvName = (TextView) view.findViewById(R.id.key_words_name);
			tvName.setText(keyWords.getTagName());
			CheckBox keywords_select = (CheckBox) view
					.findViewById(R.id.keywords_select);
			keywords_select.setChecked(keyWordsList.get(pos).isIschacked());
			layout_keywords.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onSelectListener.onSelect(pos);
				}
			});
			keywords_select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onSelectListener.onSelect(pos);
				}
			});
			return view;
		}
	}

	// 获取标签
	Handler handler = new Handler();

	public void getKeyWordsList(final boolean isFirst, String tagId,
			final String search, String selected) {
		if (!NetworkUtil.getInstance()
				.existNetwork(SelectKeyWordsActivity.this)) {
			Toast.makeText(SelectKeyWordsActivity.this,
					R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				lockScreen(SelectKeyWordsActivity.this.getResources()
						.getString(R.string.get_key_words_ing));
			}
		});

		final String currMethod = "getKeyWordsList:";
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
							SelectKeyWordsActivity.this, msg);
				} else {
					Toast.makeText(SelectKeyWordsActivity.this,
							R.string.get_key_words_ing_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Trace.d(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseKeyWords(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						gridView.onRefreshComplete();
						ResponseKeyWords response = (ResponseKeyWords) br;
						List<KeyWordsRec> list = response.getList();
						have = list.size() < 20 ? AbConstant.NOTHAVE
								: AbConstant.HAVE;
						if (have == AbConstant.HAVE) {
							offset += list.size();
						}
						if (list.size() > 0) {
							recs.addAll(list);
							adapter.setKeyWordsList(recs);
							adapter.notifyDataSetChanged();
						}

						if ("1".equals(response.getIshavekey())) {
							add_layout.setVisibility(View.VISIBLE);
							TextView tag_Text = (TextView) add_layout
									.findViewById(R.id.tag);
							tag_Text.setText(search);
							Button addBut = (Button) add_layout
									.findViewById(R.id.add_but);
							addBut.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									// 显示添加新标签对话框
									showChangeKeywordsRem();
								}
							});
						} else {
							if (add_layout.getVisibility() == View.VISIBLE) {
								add_layout.setVisibility(View.GONE);
							}

						}

						// if (list.size() <= 0
						// && !StringUtil.getInstance().IsEmpty(search)) {
						// // 显示添加新标签对话框
						// showChangeKeywordsRem();
						// } else {
						// recs.addAll(list);
						// adapter.setKeyWordsList(recs);
						// adapter.notifyDataSetChanged();
						// }

						if (isFirst) {
							gridView.onRefreshComplete();
						}
					} else {

						String message = getString(R.string.get_key_words_ing_failed_rem);
						String msg = getString(R.string.get_key_words_ing_failed_rem);
						if (null == br) {
							msg = "parseKeyWords: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();// TODO
															// 暂时显示错误信息给用户，正式版后只提示失败
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									SelectKeyWordsActivity.this, msg);
						} else {
							Toast.makeText(SelectKeyWordsActivity.this,
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								SelectKeyWordsActivity.this, msg);
					} else {
						Toast.makeText(SelectKeyWordsActivity.this,
								R.string.get_key_words_ing_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

		};
		try {
			gs.GetKeyWords(SelectKeyWordsActivity.this, tagCatId, tagId,
					search, selected, offset, count, callBack);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "搜索内容含有非法字符，如空格，\"[] \"等,为您直接搜索了最热的标签",
					Toast.LENGTH_LONG).show();
			this.search = null;
			gs.GetKeyWords(SelectKeyWordsActivity.this, tagCatId, tagId, null,
					selected, offset, count, callBack);

		}
	}

	private Dialog changeKeywordsDialog;

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent mIntent = new Intent();
//			KeyWordsListRec keyWordsListRec = new KeyWordsListRec();
//			List<KeyWordsRec> intentList = new ArrayList<KeyWordsRec>();
//			for (int i = 0; i < myRecs.size(); i++) {
//				if (myRecs.get(i).isIschacked()) {
//					intentList.add(myRecs.get(i));
//				}
//			}
//			keyWordsListRec.setRecs(intentList);
			keyWordsListRec = new KeyWordsListRec();
			if(isSelected){
				List<KeyWordsRec> intentList = new ArrayList<KeyWordsRec>();
				for (int i = 0; i < tempMyRecs.size(); i++) {
					if (tempMyRecs.get(i).isIschacked()) {
						intentList.add(tempMyRecs.get(i));
					}
				}
				keyWordsListRec.setRecs(intentList);
			}
			mIntent.putExtra("KYEWORDS_KEY", keyWordsListRec);
			setResult(RESULT_OK, mIntent);
			SelectKeyWordsActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	protected void showChangeKeywordsRem() {
		// if (null == changeKeywordsDialog) {
		changeKeywordsDialog = new Dialog(this, R.style.dialog);
		ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
				R.layout.product_change_name_dialog_content_view, null);
		TextView tvTitle = (TextView) mRoot.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.search_keywords_dialog_title);
		final EditText etKeywords = (EditText) mRoot
				.findViewById(R.id.etContent);
		Button btnOk = (Button) mRoot.findViewById(R.id.positiveButton);
		Button btnCancel = (Button) mRoot.findViewById(R.id.negativeButton);
		etKeywords.setText(fb_search_edt.getText().toString());
		etKeywords.setHint(R.string.keywords_input_hint);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeKeywordsDialog.cancel();
				add_layout.setVisibility(View.GONE);
				String prodKeywords = etKeywords.getText().toString();
				if (StringUtil.getInstance().IsEmpty(prodKeywords)) {
					Toast.makeText(SelectKeyWordsActivity.this,
							R.string.prod_keywords_not_empty,
							Toast.LENGTH_SHORT).show();
				} else {
					KeyWordsRec keyWordsRec = new KeyWordsRec();
					keyWordsRec.setTagName(etKeywords.getText().toString());
					keyWordsRec.setIschacked(true);
					myRecs.add(keyWordsRec);
					myAdapter.setKeyWordsList(myRecs);
					myAdapter.notifyDataSetChanged();
					recs.clear();
					count = 20;
					offset = 0;
					getKeyWordsList(true, null, null, null);
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				add_layout.setVisibility(View.GONE);
				changeKeywordsDialog.cancel();
			}
		});
		changeKeywordsDialog.setContentView(mRoot);
		changeKeywordsDialog.setCanceledOnTouchOutside(false);
		changeKeywordsDialog.show();
		// }
		//
		// if (null != changeKeywordsDialog && !isFinishing()
		// && !changeKeywordsDialog.isShowing()) {
		// changeKeywordsDialog.show();
		// }
	}

	public interface OnSelectClickListener {
		void onSelect(int number);
	}
}
