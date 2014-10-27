package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbConstant;
import com.ab.net.AbHttpCallback;
import com.ab.net.AbHttpItem;
import com.ab.view.AbPullToRefreshListView;
import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.EnterpriseItemRec;
import com.skycober.mineral.bean.EnterpriseRec;
import com.skycober.mineral.bean.KeyWordsRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseEnterpriseInfo;
import com.skycober.mineral.network.ResponseKeyWords;
import com.skycober.mineral.product.SelectKeyWordsActivity.OnSelectClickListener;
import com.skycober.mineral.util.AndroidIdUtil;
import com.skycober.mineral.util.AndroidIdUtil.onAndroidIdGetFailure;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.widget.MyRemDialog;
import com.skycober.socialfarm.customview.smartImageView.SmartImageView;

/**
 * 2微码界面
 * 
 * @author Cool
 * 
 */
public class TwoDimensionActivity extends BaseActivity {
	private EnterpriseRec enterpriseRec = new EnterpriseRec();
	private static final String TAG = "TwoDimensionActivity";
	private AbPullToRefreshListView gridView;
	private int count = 20, offset = 0;
	private int have = AbConstant.HAVE;
	private KeyWordsItemAdapter adapter;
	private List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
	private String tagCatId;
	private List<KeyWordsRec> myRecs = new ArrayList<KeyWordsRec>();
	private EnterpriseItemRec enterpriseItemRec=new EnterpriseItemRec();
	private SmartImageView avatar;
	private TextView com_name,com_key;
	private AndroidIdUtil androidIdUtil=new AndroidIdUtil();

	private onAndroidIdGetFailure androidIdGetFailure=new onAndroidIdGetFailure() {
		
		@Override
		public void onFailure() {
			// TODO Auto-generated method stub
			showLogOutRem(androidIdUtil);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_dimension);
		initTitle();
		initdate();
		init();
		getKeyWordsList(true, tagCatId, null);

	}
	private MyRemDialog logOutRemDialog;

	private void showLogOutRem(final AndroidIdUtil androidIdUtil) {
		if (null == logOutRemDialog) {
			logOutRemDialog = new MyRemDialog(this, R.style.dialog);
			logOutRemDialog.setHeaderVisility(View.GONE);
			logOutRemDialog.setMessage("连接服务器失败了，是否重试？");
			logOutRemDialog.setPosBtnText("确定");
			logOutRemDialog.setNegBtnText("取消");
			logOutRemDialog.setPosBtnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					androidIdUtil.sendAndroidId(TwoDimensionActivity.this);
				}
			});
			logOutRemDialog.setNegBtnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			logOutRemDialog.setCanceledOnTouchOutside(false);
		}
		if (null != logOutRemDialog && !this.isFinishing()
				&& !logOutRemDialog.isShowing()) {
			logOutRemDialog.show();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			startActivity(new Intent(TwoDimensionActivity.this, FragmentChangeActivity.class));
			TwoDimensionActivity.this.finish();
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initdate() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		String data = intent.getDataString();
		String jsons[] = data.split("/");
		for (int i = 0; i < jsons.length; i++) {
			if (jsons[i].equalsIgnoreCase(EnterpriseRec.EID)) {
				enterpriseRec.setEid(jsons[i + 1]);
				i = i + 1;
			}else if (jsons[i].equalsIgnoreCase(EnterpriseRec.TAG_CAT_ID)) {
				enterpriseRec.setTagCatId(jsons[i + 1]);
				i = i + 1;
			}
		}
		tagCatId = enterpriseRec.getTagCatId();
		androidIdUtil.sendAndroidId(this);
		androidIdUtil.setAndroidIdGetFailure(androidIdGetFailure);
	}

	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			startActivity(new Intent(TwoDimensionActivity.this, FragmentChangeActivity.class));
		TwoDimensionActivity.this.finish();
		}
	};

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
		btnRight.setVisibility(View.GONE);
	}

	private void init() {
		// TODO Auto-generated method stub
		
		gridView = (AbPullToRefreshListView) findViewById(R.id.lv_key_words);
		Button attention_btn = (Button) findViewById(R.id.attention_btn);
		attention_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (myRecs.size() <= 0) {
					Toast.makeText(TwoDimensionActivity.this, "您还没有选择任何标签",
							Toast.LENGTH_LONG).show();
				} else {
					String tag_ids = null;
					for (int i = 0; i < myRecs.size(); i++) {
						if (i == 0) {
							tag_ids = myRecs.get(i).getTagID();
						} else {
							tag_ids = tag_ids + "," + myRecs.get(i).getTagID();
						}

					}
					toAttention(tag_ids);
				}

			}
		});
		adapter = new KeyWordsItemAdapter(this, R.layout.adapter_key_words_item,recs);
		View view = LayoutInflater.from(this).inflate(R.layout.view_text_view,null);
		avatar=(SmartImageView)view.findViewById(R.id.user_avatar);
		com_name=(TextView)view.findViewById(R.id.com_name);
		com_key=(TextView)view.findViewById(R.id.com_key);
		gridView.addHeaderView(view);
		// 当热词被选择或取消选择
		adapter.setOnSelectListener(new OnSelectClickListener() {

			@Override
			public void onSelect(int number) {
				recs.get(number).setIschacked(!recs.get(number).isIschacked());
				if (recs.get(number).isIschacked()) {
					myRecs.add(recs.get(number));
				} else {
					for (int i = 0; i < myRecs.size(); i++) {
						if (recs.get(number).getTagID()
								.equals(myRecs.get(i).getTagID())) {
							myRecs.remove(i);
						}
					}
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
				recs.clear();
				count = 20;
				offset = 0;
				getKeyWordsList(true, null, null);
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
					getKeyWordsList(false, null, null);
			}
		};
		gridView.setScrollItem(item1);
		getEnterpriseInfo(enterpriseRec.getEid());
	}

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

	private void appIs() {

		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = "com.agero.bluelink";
		String bingMapClassName = "com.agero.bluelink.BingMapActivity";

		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			ComponentName topConponent = tasksInfo.get(0).topActivity;

			if (packageName.equals(topConponent.getPackageName())) {

				// 当前的APP在前台运行

				if (topConponent.getClassName().equals(bingMapClassName)) {

					// 当前正在运行的是不是期望的Activity

				} else {

				}
			} else {
				startActivity(new Intent(TwoDimensionActivity.this, FragmentChangeActivity.class));
				
				// 当前的APP在后台运行

			}
		}
	}

	// 获取标签
	Handler handler = new Handler();

	public void getKeyWordsList(final boolean isFirst, String tagId,
			final String search) {
		if (!NetworkUtil.getInstance().existNetwork(TwoDimensionActivity.this)) {
			Toast.makeText(TwoDimensionActivity.this,
					R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				lockScreen(TwoDimensionActivity.this.getResources().getString(
						R.string.get_key_words_ing));
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
							TwoDimensionActivity.this, msg);
				} else {
					Toast.makeText(TwoDimensionActivity.this,
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
						ResponseKeyWords response = (ResponseKeyWords) br;
						List<KeyWordsRec> list = response.getList();
						have = list.size() < 20 ? AbConstant.NOTHAVE
								: AbConstant.HAVE;
						if (have == AbConstant.HAVE) {
							offset += list.size();
						}
						recs.addAll(list);
						adapter.setKeyWordsList(recs);
						adapter.notifyDataSetChanged();
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
									TwoDimensionActivity.this, msg);
						} else {
							Toast.makeText(TwoDimensionActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								TwoDimensionActivity.this, msg);
					} else {
						Toast.makeText(TwoDimensionActivity.this,
								R.string.get_key_words_ing_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

		};
		try {
			gs.GetKeyWords(TwoDimensionActivity.this, tagId, null, search,null,
					offset, count, callBack);
		} catch (Exception e) {
			Log.e("wangxu", "" + e);
		}
	}

	Handler attentionHandler = new Handler();

	public void toAttention(String tag_ids) {
		if (!NetworkUtil.getInstance().existNetwork(TwoDimensionActivity.this)) {
			Toast.makeText(TwoDimensionActivity.this,
					R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				lockScreen("正在关注");
			}
		});

		final String currMethod = "toAttention:";
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
							TwoDimensionActivity.this, msg);
				} else {
					Toast.makeText(TwoDimensionActivity.this, "网速不给力哦",
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
					BaseResponse br = parser.parseAttention(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						
						Toast.makeText(TwoDimensionActivity.this,
								"关注" + enterpriseItemRec.getEname() + "成功",
								Toast.LENGTH_SHORT).show();
						TwoDimensionActivity.this.finish();
						startActivity(new Intent(TwoDimensionActivity.this, FragmentChangeActivity.class));
					} else {
						String message = "关注失败";
						String msg = "关注失败";
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
									TwoDimensionActivity.this, msg);
						} else {
							Toast.makeText(TwoDimensionActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								TwoDimensionActivity.this, msg);
					} else {

						Toast.makeText(TwoDimensionActivity.this, "关注失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		gs.AttentionCom(TwoDimensionActivity.this, tagCatId,
				enterpriseRec.getEid(), tag_ids, callBack);
	}
	public void getEnterpriseInfo(String eid) {
		if (!NetworkUtil.getInstance().existNetwork(TwoDimensionActivity.this)) {
			Toast.makeText(TwoDimensionActivity.this,
					R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				lockScreen("正在获取企业信息");
			}
		});
		final String currMethod = "getEnterpriseInfo:";
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
							TwoDimensionActivity.this, msg);
				} else {
					Toast.makeText(TwoDimensionActivity.this, "网速不给力哦",
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
					BaseResponse br = parser.parseGetEnterpriseInfo(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseEnterpriseInfo enterpriseInfo=(ResponseEnterpriseInfo)br;
						enterpriseItemRec=enterpriseInfo.getEnterpriseInfo();
						Log.e("wangxu", enterpriseItemRec.getElogo());
						avatar.setImageUrl(RequestUrls.SERVER_BASIC_URL+"/"+enterpriseItemRec.getElogo());
						com_name.setText(enterpriseItemRec.getEname());
						com_key.setText("所属:"+enterpriseItemRec.getIndustry());
					} else {
						String message = "获取企业信息失败";
						String msg = "获取企业信息失败";
						if (null == br) {
							msg = "getEnterpriseInfo: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							default:
								msg = br.getMessage();
								message = br.getMessage();
								//暂时显示错误信息给用户，正式版后只提示失败
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									TwoDimensionActivity.this, msg);
						} else {
							Toast.makeText(TwoDimensionActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								TwoDimensionActivity.this, msg);
					} else {
						Toast.makeText(TwoDimensionActivity.this, "获取企业信息失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		gs.enterprise(TwoDimensionActivity.this, eid, callBack);
	}
}
