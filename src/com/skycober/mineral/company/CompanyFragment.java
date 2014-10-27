package com.skycober.mineral.company;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.CalendarUtil;
import com.skycober.mineral.util.MyListView;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.widget.MyRemDialog;

public class CompanyFragment extends FragBaseActivity {
	private ListView companyListView;
	// private List<CompanyInfo> companyInfoList;
	private CompanyAdapter companyAdapter;

	private String url;
	private int isAttention;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		url = bundle.getString("url");
		isAttention = bundle.getInt("num");
		companyAdapter = new CompanyAdapter();

	}

	// public List<CompanyInfo> getInfo() {
	// List<CompanyInfo> list = new ArrayList<CompanyInfo>();
	// for (int i = 0; i < 20; i++) {
	// CompanyInfo info = new CompanyInfo();
	// info.setEname("company" + i);
	// list.add(info);
	// }
	//
	// return list;
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.attentioncompanyfragment,
				container, false);
		companyListView = (ListView) view.findViewById(R.id.companyList);
		// view.findViewById(R.id.company_search_but).setVisibility(View.GONE);
		// view.findViewById(R.id.comapny_search).setVisibility(View.GONE);
		// companyListView.setAddMoreListener(new OnAddMoreListener() {
		//
		// @Override
		// public void onAddMore() {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		// init();
		// companyAdapter.bindDate(getInfo());
		// companyListView.setAdapter(companyAdapter);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
	}

	private void init() {

		getCommpanyInfos();
		companyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						CompanyTagActivity.class);
				CompanyInfo comInfo = companyAdapter.getItem(position);
				intent.putExtra("eid", comInfo.getEid());
				startActivity(intent);
			}
		});

		companyListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						final String eid = companyAdapter.getItem(position)
								.getEid();
						MyRemDialog exitDialog = new MyRemDialog(getActivity(),
								R.style.Dialog);
						exitDialog.setTitle(R.string.company_delete_attention);
						exitDialog.setMessage("确定取消关注"
								+ companyAdapter.getItem(position).getEname());
						exitDialog.setPosBtnText(R.string.common_dialog_btn_ok);
						exitDialog
								.setNegBtnText(R.string.exit_app_dialog_btn_cancel);
						exitDialog
								.setPosBtnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										cancleAttentionCompany(RequestUrls.SERVER_BASIC_URL
												+ RequestUrls.COMPANY_CANNCEL_ATTENTION
												+ eid);
									}
								});
						exitDialog.show();
						return true;
					}
				});

	}

	/**
	 * 取消关注
	 * 
	 * @param eid
	 */
	public void cancleAttentionCompany(String url) {
		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(getActivity()).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t, HttpResponse response) {
				// TODO Auto-generated method stub
				super.onSuccess(t, response);
				try {
					JSONObject jsonObject = new JSONObject(t.toString());
					if ("1".equals(CompanyJsonUtils.parserResut(jsonObject))) {
						getCommpanyInfos();
						Toast.makeText(getActivity(), "取消成功", 1).show();
					}

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

	/**
	 * 获取企业列表
	 */
	public void getCommpanyInfos() {
		if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen("正在获取信息");
		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(getActivity()).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);

				try {

					if (t != null) {
						JSONObject jsonObject = new JSONObject(t.toString());
						List<CompanyInfo> companyInfos = CompanyJsonUtils
								.parseAttentionCompanys(jsonObject);

						if (companyInfos != null) {
							if (companyInfos.size() == 0) {
								Toast.makeText(getActivity(),
										"您暂时还没关注任何企业，赶紧去关注吧！！！", 1).show();
							}
							companyAdapter.bindDate(companyInfos);
							companyListView.setAdapter(companyAdapter);

						} else {
						}

					} else {
						Toast.makeText(getActivity(), "tNUll", 1).show();
					}
					releaseScreen();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, strMsg);
				releaseScreen();
			}

		});
	}

	public class CompanyAdapter extends BaseAdapter {

		private List<CompanyInfo> infos = new ArrayList<CompanyInfo>();

		public void bindDate(List<CompanyInfo> infos) {
			if (infos != null || infos.size() <= 0) {
				this.infos = infos;
			} else {
				Toast.makeText(getActivity(), "暂无信息", Toast.LENGTH_LONG).show();
			}

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public CompanyInfo getItem(int position) {
			// TODO Auto-generated method stub
			return infos.get(position);
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
						R.layout.company_item, parent, false);
				vh = new ViewHolder();
				vh.ename = (TextView) convertView.findViewById(R.id.ename);
				vh.add_time = (TextView) convertView
						.findViewById(R.id.add_time);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			if (position % 2 != 0) {
				convertView.setBackgroundColor(getActivity().getResources()
						.getColor(R.color.gray_55));
			} else {
				convertView.setBackgroundColor(getActivity().getResources()
						.getColor(R.color.white));
			}

			vh.ename.setText(infos.get(position).getEname());
			vh.add_time.setText(CalendarUtil.GetPostCommentTime(infos.get(
					position).getAdd_time() * 1000l));
			return convertView;
		}

	}

	public static class ViewHolder {
		TextView ename;
		TextView add_time;
	}
}
