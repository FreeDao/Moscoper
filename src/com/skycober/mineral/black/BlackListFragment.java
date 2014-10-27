package com.skycober.mineral.black;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.BlackInfo;
import com.skycober.mineral.company.CompanyJsonUtils;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponsePraiseAndBlack;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.MyListView;
import com.skycober.mineral.util.MyListView.OnAddMoreListener;
import com.skycober.mineral.util.MyListView.OnRefreshListener;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.widget.MyRemDialog;

public class BlackListFragment extends FragBaseActivity {

	private Button btnLeft, btnRight;
	private MyListView blackList;
	private int offset;
	private int count;
	private List<BlackInfo> blackListInfo;
	private BlackAdapter adapter;
	private final String url = RequestUrls.SERVER_BASIC_URL
			+ RequestUrls.BLACK_LIST_URL;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.black_list_layou, null, false);
		TextView tvTitle = (TextView) view.findViewById(R.id.title_text);
		tvTitle.setText(R.string.my_black_product_page_title);
		ImageButton btnLeft = (ImageButton) view
				.findViewById(R.id.title_button_left);// 返回
		 btnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentChangeActivity.slideMenu.toggle();
			}
		});
		btnRight = (Button) view.findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.GONE);
		blackListInfo = new ArrayList<BlackInfo>();
		blackList = (MyListView) view.findViewById(R.id.black_list);
		adapter = new BlackAdapter();
		blackList.setAdapter(adapter);
		init();
		return view;
	}

	private void init() {
		boolean hasLogin = !SettingUtil
				.getInstance(getActivity())
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		// 如果没登陆跳转登录界面，已登录获取中意信息列表
		if (hasLogin) {
			readgetBlackInfos();
		} else {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}
	}

	public void readgetBlackInfos() {
		blackList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final int p = position-1;
				MyRemDialog exitDialog = new MyRemDialog(getActivity(),
						R.style.Dialog);
				exitDialog.setTitle(R.string.company_delete_black);
				exitDialog.setMessage("确定取消屏蔽"
						+ blackListInfo.get(p).getName());
				exitDialog.setCancelable(false);
				exitDialog.setPosBtnText(R.string.common_dialog_btn_ok);
				exitDialog.setNegBtnText(R.string.exit_app_dialog_btn_cancel);
				exitDialog.setPosBtnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						cancleBlack(blackListInfo.get(p).getMid());
					}
				});
				exitDialog.show();
			}
		});
		blackList.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				getBlackInfo(true);
			}
		});

		blackList.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				// TODO Auto-generated method stub
				getBlackInfo(false);
			}
		});

		getBlackInfo(true);

	}

	public void cancleBlack(final String id){
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parsePraiseAndBlack(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponsePraiseAndBlack response = (ResponsePraiseAndBlack) br;
						getBlackInfo(true);
//							Toast.makeText(getActivity(),
//									"已取消屏蔽！！", Toast.LENGTH_SHORT).show();

					} else {
						String message = "网速有点不给力啊";
						String msg = "网速有点不给力啊";
						if (null == br) {
							msg = "readyToPriaseOrBlack: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							case 7:
								Intent intent = new Intent(
										getActivity(),
										LoginActivity.class);
								startActivity(intent);
								break;
							default:
								msg = br.getMessage();
								message = br.getMessage();
								break;
							}
						}
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
						ExceptionRemHelper.showExceptionReport(
								getActivity(), msg);
					} else {
						Toast.makeText(getActivity(), "网速有点不给力啊",
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(
							getActivity(), msg);
				} else {
					Toast.makeText(getActivity(), "网速有点不给力啊",
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

		};
		gs.NotBlack(getActivity(), id, callBack);
	}
	public void getBlackInfo(final boolean isFirst) {
		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(getActivity()).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		if (isFirst) {
			offset = 0;
			count = 20;
			blackListInfo.clear();
			adapter.notifyDataSetChanged();
		}
		String offsetStr = String.valueOf(offset);
		String countStr = String.valueOf(count);
		String url1 = url;
		url1 = url1.replace("[offset]", offsetStr);
		url1 = url1.replace("[count]", countStr);

		fh.get(url1, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t, HttpResponse response) {
				// TODO Auto-generated method stub
				super.onSuccess(t, response);
				try {
					List<BlackInfo> list = CompanyJsonUtils.parserBlackList(
							getActivity(), new JSONObject(t.toString()));
					if (list != null && list.size() > 0) {
						if (!isFirst) {
							blackListInfo.addAll(list);
							offset += blackListInfo.size();
							Boolean ishas = (list.size() < 20) ? false : true;
							blackList.onScrollComplete(ishas);
							refreshList();
						} else {
							blackListInfo.clear();
							blackListInfo.addAll(list);
							offset += blackListInfo.size();
							Boolean ishas = (list.size() < 20) ? false : true;
							blackList.onScrollComplete(ishas);
							refreshList();
						}
					} else {
						blackList.onScrollComplete(false);
						refreshList();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private void refreshList() {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();
				blackList.onRefreshComplete();
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, strMsg);
			}

		});
	}

	public class BlackAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return blackListInfo.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return blackListInfo.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.black_item, null);
				vh = new ViewHolder();
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			if (position % 2 == 0) {
				convertView.setBackgroundResource(R.color.gray_55);

			} else {
				convertView.setBackgroundResource(R.color.white);
			}
			vh.tvName = (TextView) convertView.findViewById(R.id.name);
			vh.tvName.setText(blackListInfo.get(position).getName());
			return convertView;
		}

	}

	public static final class ViewHolder {
		TextView tvName;
	}

}
