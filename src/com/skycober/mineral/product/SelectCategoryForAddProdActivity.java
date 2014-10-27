package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.skycober.mineral.account.CategoryService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.TagCategoryRec;
import com.skycober.mineral.company.CharacterParser;
import com.skycober.mineral.company.SideBar;
import com.skycober.mineral.company.SideBar.OnTouchingLetterChangedListener;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseTagCategory;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;

/**
 * 分类信息浏览
 * 
 * @author 新彬
 * 
 */
public class SelectCategoryForAddProdActivity extends BaseActivity {
	private static final String TAG = "SelectCategoryForAddProdActivity";
	private int lastSelectnumber = -1;
	private EditText fb_search_edt;
	private Button fb_search_btn;
	private ImageButton btnRight;

	private String search = null;
	private String type = "publish";
	private List<TagCategoryRec> categoryRecList = new ArrayList<TagCategoryRec>();
	private TagCategoryRec categoryRec = new TagCategoryRec();
	ArrayList<String> listName = new ArrayList<String>();
	ArrayList<String> listId = new ArrayList<String>();
	private CharacterParser characterParser;
	private PinyinComparator1 pinyinComparator = new PinyinComparator1();
	private SideBar sideBar;
	private TextView dialog;
	private int offSet = 0;
	private int have = AbConstant.HAVE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_review);
		Intent intent = getIntent();
		add_layOut = (RelativeLayout) findViewById(R.id.add);
		if (intent.hasExtra("type")) {
			type = intent.getExtras().getString("type");
		}
		TextView tvTitle = (TextView) findViewById(R.id.title_text);
		tvTitle.setText(R.string.add_prod_select_category_page_title);
		ImageButton btnLeft = (ImageButton) findViewById(R.id.title_button_left);
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnListClickListener);
		btnRight = (ImageButton) findViewById(R.id.title_button_right);
		btnRight.setImageResource(R.drawable.check_btn_selector);
		btnRight.setVisibility(View.VISIBLE);
		btnRight.setOnClickListener(btnrightClickListener);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		fb_search_edt = (EditText) findViewById(R.id.fb_search_edt);// 搜索
		fb_search_btn = (Button) findViewById(R.id.fb_search_btn);
		fb_search_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				search = fb_search_edt.getText().toString();
				String s = search.trim();
				if (search == null || "".equals(s)) {
					Toast.makeText(getApplicationContext(), "请输入合法关键字！！", 1)
							.show();
				} else {
					categoryRecList.clear();
					offSet = 0;
					startToGetCategoryReview(false, fb_search_edt.getText()
							.toString());
				}

			}
		});
		fb_search_edt.addTextChangedListener(textWatcher);
		lvCategory = (AbPullToRefreshListView) findViewById(R.id.lv_category);
		categoryAdapter = new CategoryAdapter(this,
				R.layout.select_listview_item, categoryRecList);
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
				categoryRecList.clear();
				// count = 20;
				offSet = 0;
				startToGetCategoryReview(true, null);
			}
		};
		lvCategory.setRefreshItem(item);
		AbHttpItem item1 = new AbHttpItem();
		item1.callback = new AbHttpCallback() {

			@Override
			public void update() {
				// TODO Auto-generated method stub
				lvCategory.onScrollComplete(have);
			}

			@Override
			public void get() {
				// TODO Auto-generated method stub
				if (have == AbConstant.HAVE) {
					startToGetCategoryReview(false, null);
				}
			}
		};
		lvCategory.setScrollItem(item1);
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// TODO Auto-generated method stub
				// 该字母首次出现的位置
				int position = categoryAdapter.getPositionForSection(s
						.charAt(0));
				if (position != -1) {
					lvCategory.setSelection(position);
				}
			}
		});
		// categoryAdapter.setOnSelectClickListener(new OnSelectClickListener()
		// {
		// @Override
		// public void onSelect(int number) {
		// if (lastSelectnumber != -1
		// && lastSelectnumber < categoryRecList.size()) {
		// categoryRecList.get(lastSelectnumber).setChecked(false);
		// }
		// categoryRecList.get(number).setChecked(true);
		// lastSelectnumber = number;
		// categoryAdapter.setCategoryList(categoryRecList);
		// categoryAdapter.notifyDataSetChanged();
		// categoryRec = categoryRecList.get(number);
		// btnRight.performClick();
		// }
		// });

		lvCategory.setAdapter(categoryAdapter);
		lvCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (lastSelectnumber != -1
						&& lastSelectnumber < categoryRecList.size()) {
					categoryRecList.get(lastSelectnumber).setChecked(false);
				}
				int pos = arg2-1;
				categoryRecList.get(pos).setChecked(true);
				lastSelectnumber = pos;
				categoryAdapter.setCategoryList(categoryRecList);
				categoryAdapter.notifyDataSetChanged();
				categoryRec = categoryRecList.get(pos);
				btnRight.performClick();
			}
		});
		startToGetCategoryReview(false, null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent mIntent = new Intent();
			if (categoryRec != null
					&& categoryRec.getTagCatName() != null
					&& !StringUtil.getInstance().IsEmpty(
							categoryRec.getTagCatName().trim())) {

				mIntent.putExtra(KEY_CATEGORY_REC, categoryRec);

			} 
//			else {
//				Toast.makeText(getApplicationContext(), "请选择信息分类", 1).show();
//			}
			setResult(RESULT_OK, mIntent);
			SelectCategoryForAddProdActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
				offSet = 0;
				categoryRecList.clear();
				startToGetCategoryReview(false, null);
				add_layOut.setVisibility(View.GONE);
			}
		}
	};

	private AbPullToRefreshListView lvCategory;
	private CategoryAdapter categoryAdapter;

	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent();
			if (categoryRec != null
					&& categoryRec.getTagCatName() != null
					&& !StringUtil.getInstance().IsEmpty(
							categoryRec.getTagCatName().trim())) {

				mIntent.putExtra(KEY_CATEGORY_REC, categoryRec);

			} 
//			else {
//				Toast.makeText(getApplicationContext(), "请选择信息分类", 1).show();
//			}
			setResult(RESULT_OK, mIntent);
			SelectCategoryForAddProdActivity.this.finish();
			// finish();
		}
	};
	private View.OnClickListener btnrightClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			if (categoryRec != null
					&& categoryRec.getTagCatName() != null
					&& !StringUtil.getInstance().IsEmpty(
							categoryRec.getTagCatName().trim())) {
				Intent mIntent = new Intent();
				mIntent.putExtra(KEY_CATEGORY_REC, categoryRec);
				setResult(RESULT_OK, mIntent);
				SelectCategoryForAddProdActivity.this.finish();
			} else {
				Toast.makeText(getApplicationContext(), "请选择信息分类", 1).show();
			}

			v.setEnabled(true);

		}
	};

	public static final String KEY_CATEGORY_REC = "key_category_rec";

	public class CategoryAdapter extends BaseAdapter {

		private List<TagCategoryRec> categoryList;
		private int layoutId;
		private int selectnumber = -1;
		private LayoutInflater inflater;
		private OnSelectClickListener onSelectClickListener;

//		public OnSelectClickListener getOnSelectClickListener() {
//			return onSelectClickListener;
//		}

		// public void setOnSelectClickListener(
		// OnSelectClickListener onSelectClickListener) {
		// this.onSelectClickListener = onSelectClickListener;
		// }

		public CategoryAdapter(Context context, int layoutId,
				List<TagCategoryRec> categoryList) {
			inflater = LayoutInflater.from(context);
			this.layoutId = layoutId;
			this.categoryList = categoryList;
		}

		public List<TagCategoryRec> getCategoryList() {
			return categoryList;
		}

		public void setCategoryList(List<TagCategoryRec> categoryList) {
			this.categoryList = categoryList;
		}

		@Override
		public int getCount() {
			return categoryList.size();
		}

		@Override
		public Object getItem(int position) {
			return categoryList.get(position);
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
			TextView tvGroupName = (TextView) view.findViewById(R.id.tag_name);
			TextView tvCatlog = (TextView) view.findViewById(R.id.catlog);
			tvGroupName.setText(categoryList.get(pos).getTagCatName());
			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(pos);

			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (pos == getPositionForSection(section)) {
				tvCatlog.setVisibility(View.VISIBLE);
				tvCatlog.setText(categoryList.get(pos).getSortLetters());
			} else {
				tvCatlog.setVisibility(View.GONE);
			}
			// CheckBox select_box = (CheckBox)
			// view.findViewById(R.id.select_box);
			// select_box.setChecked(categoryList.get(pos).isChecked());
			// select_box.setOnClickListener(new OnClickListener() {

			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// onSelectClickListener.onSelect(pos);
			//
			// }
			// });
			return view;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		public int getSectionForPosition(int position) {

			return categoryList.get(position).getSortLetters().charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = categoryList.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}

			return -1;
		}

		/**
		 * 提取英文的首字母，非英文字母用#代替。
		 * 
		 * @param str
		 * @return
		 */
		private String getAlpha(String str) {
			String sortStr = str.trim().substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortStr.matches("[A-Z]")) {
				return sortStr;
			} else {
				return "#";
			}
		}

	}

	public void getSelling(List<TagCategoryRec> list) {
		TagCategoryRec tcr = null;
		characterParser = CharacterParser.getInstance();
		for (int i = 0; i < list.size(); i++) {
			tcr = list.get(i);
			String pinyin = characterParser.getSelling(tcr.getTagCatName());
			if (pinyin.length() > 0) {
				String sortString = pinyin.substring(0, 1).toUpperCase();

				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					tcr.setSortLetters(sortString.toUpperCase());
				} else {
					tcr.setSortLetters("#");
				}
			} else {
				tcr.setSortLetters("#");
			}

		}

	}

	/**
	 * 获取分类信息
	 */

	private RelativeLayout add_layOut;
	Handler handler = new Handler();

	public void startToGetCategoryReview(final boolean isFirst,
			final String serach) {
		if (!NetworkUtil.getInstance().existNetwork(
				SelectCategoryForAddProdActivity.this)) {
			Toast.makeText(SelectCategoryForAddProdActivity.this,
					R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				lockScreen(SelectCategoryForAddProdActivity.this.getResources()
						.getString(R.string.get_category_ing));
			}
		});

		final String currMethod = "startToGetCategoryReview:";
		CategoryService cs = new CategoryService();
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
							SelectCategoryForAddProdActivity.this, msg);
				} else {
					Toast.makeText(SelectCategoryForAddProdActivity.this,
							R.string.get_category_failed_rem,
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
					BaseResponse br = parser.parseTagCategory(json);

					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseTagCategory response = (ResponseTagCategory) br;
						List<TagCategoryRec> list = response.getList();
						if (null != search && !"".equals(serach)) {
							if ("0".equals(response.getIshavekey())) {
								add_layOut.setVisibility(View.VISIBLE);
								TextView tag_Text = (TextView) add_layOut
										.findViewById(R.id.tag);
								tag_Text.setText(serach);
								Button addBut = (Button) add_layOut
										.findViewById(R.id.add_but);
								addBut.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										showChangePIDRem();// 添加标签的对话框
									}
								});

							} else {
								if (add_layOut.getVisibility() == View.VISIBLE) {
									add_layOut.setVisibility(View.GONE);
								}

							}
						} else {
							if (add_layOut.getVisibility() == View.VISIBLE) {
								add_layOut.setVisibility(View.GONE);
							}
						}
						have = list.size() < 20 ? AbConstant.NOTHAVE
								: AbConstant.HAVE;
						if (have == AbConstant.HAVE) {
							offSet += list.size();
						}
						if (list.size() > 0) {
							// categoryRecList.clear();

							categoryRecList.addAll(list);
							getSelling(categoryRecList);
							// 根据a-z进行排序源数据
							Collections.sort(categoryRecList, pinyinComparator);
							categoryAdapter.setCategoryList(categoryRecList);
							categoryAdapter.notifyDataSetChanged();
						}

						// if (list.size() <= 0
						// && !StringUtil.getInstance().IsEmpty(serach)) {
						// showChangePIDRem();// 添加标签的对话框
						// } else {
						// categoryRecList.addAll(list);
						// categoryAdapter.setCategoryList(categoryRecList);
						// categoryAdapter.notifyDataSetChanged();
						// }
					} else {

						String message = getString(R.string.get_category_failed_rem);
						String msg = getString(R.string.get_category_failed_rem);
						if (null == br) {
							msg = "parseGetAllDynamic: Result:BaseResponse is null.";
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
									SelectCategoryForAddProdActivity.this, msg);
						} else {
							Toast.makeText(
									SelectCategoryForAddProdActivity.this,
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								SelectCategoryForAddProdActivity.this, msg);
					} else {
						Toast.makeText(SelectCategoryForAddProdActivity.this,
								R.string.get_category_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				if (isFirst) {
					lvCategory.onRefreshComplete();
				}
				super.onSuccess(t);
			}

		};

		cs.GetTagCategory(SelectCategoryForAddProdActivity.this, serach, type,
				offSet, 20, callBack);

	}

	private Dialog selectParentIdDialog;

	protected void showChangePIDRem() {
		// if (null == selectParentIdDialog) {
		selectParentIdDialog = new Dialog(this, R.style.dialog);
		ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
				R.layout.product_change_name_dialog_content_view, null);
		TextView tvTitle = (TextView) mRoot.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.change_category_dialog_title);
		final EditText etKeywords = (EditText) mRoot
				.findViewById(R.id.etContent);
		Button btnOk = (Button) mRoot.findViewById(R.id.positiveButton);
		Button btnCancel = (Button) mRoot.findViewById(R.id.negativeButton);
		etKeywords.setText(fb_search_edt.getText().toString());
		etKeywords.setHint(R.string.prod_prientid_input_hint);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectParentIdDialog.cancel();
				add_layOut.setVisibility(View.GONE);
				String prodKeywords = etKeywords.getText().toString();
				if (StringUtil.getInstance().IsEmpty(prodKeywords)) {
					Toast.makeText(SelectCategoryForAddProdActivity.this,
							R.string.prod_keywords_not_empty,
							Toast.LENGTH_SHORT).show();
				} else {
					Intent mIntent = new Intent();
					TagCategoryRec tagCategoryRec = new TagCategoryRec();
					tagCategoryRec.setTagCatName(etKeywords.getText()
							.toString());
					mIntent.putExtra(KEY_CATEGORY_REC, tagCategoryRec);
					setResult(RESULT_OK, mIntent);
					SelectCategoryForAddProdActivity.this.finish();
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				add_layOut.setVisibility(View.GONE);
				selectParentIdDialog.cancel();
			}
		});
		selectParentIdDialog.setContentView(mRoot);
		selectParentIdDialog.setCanceledOnTouchOutside(false);
		selectParentIdDialog.show();
		// }
		// if (null != selectParentIdDialog && !isFinishing()
		// && !selectParentIdDialog.isShowing()) {
		// selectParentIdDialog.show();
		// }
	}

	public interface OnSelectClickListener {
		void onSelect(int number);
	}
}
