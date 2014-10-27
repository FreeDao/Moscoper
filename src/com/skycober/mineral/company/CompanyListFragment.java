package com.skycober.mineral.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.R.string;
import com.skycober.mineral.company.SideBar.OnTouchingLetterChangedListener;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.MyListView;
import com.skycober.mineral.util.MyListView.OnAddMoreListener;
import com.skycober.mineral.util.MyListView.OnRefreshListener;
import com.skycober.mineral.util.SettingUtil;

public class CompanyListFragment extends FragBaseActivity {
	private MyListView companyList;
	private String url;
	private CompanyListAdapter adapter;
	private TextView searchBut;
	private EditText searchEdit;
	private int offset = 0;
	private int count = 20;
	private List<CompanyInfo> prodList;
	private SideBar sideBar;
	private TextView dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		adapter = new CompanyListAdapter(getActivity());
		url = getArguments().getString("url");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.companyfragment, container, false);
		searchBut = (TextView) view.findViewById(R.id.company_search_but);
		searchEdit = (EditText) view.findViewById(R.id.comapny_search);
		searchEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

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
					getSearchCompanyList(s.toString());
				}
			}
		});
		searchBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchStr = searchEdit.getText().toString();
				if (searchStr == null || "".equals(searchStr)) {
					Toast.makeText(getActivity(), "请输入您要搜索的内容", 1).show();
				} else {
					getSearchCompanyList(searchStr);
				}

			}
		});
		companyList = (MyListView) view.findViewById(R.id.companyList);
		prodList = new ArrayList<CompanyInfo>();
		adapter.bindDate(prodList);
		companyList.setAdapter(adapter);

		dialog = (TextView) view.findViewById(R.id.dialog);
		sideBar = (SideBar) view.findViewById(R.id.sidrbar);
		sideBar.setTextView(dialog);
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// TODO Auto-generated method stub
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					companyList.setSelection(position + 1);
				}
			}
		});
		// init();
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
	}

	/**
	 * 搜索企业
	 * 
	 * @param searchStr
	 */
	public void getSearchCompanyList(String searchStr) {
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.COMPANY_SEARCH_LIST + searchStr;
		lockScreen("正在获取信息...");
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
					List<CompanyInfo> list = CompanyJsonUtils.parserCompany(
							getActivity(), new JSONObject(t.toString()));
					if (list != null && list.size() == 0) {
						Toast.makeText(getActivity(), "没有您想要的！！！", 1).show();
					}
					// 汉字转换成拼音
					getSelling(list);
					// 根据a-z进行排序源数据
					Collections.sort(list, pinyinComparator);
					adapter.bindDate(list);
					companyList.setAdapter(adapter);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				refreshList();
				releaseScreen();
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, strMsg);
			}

		});
	}

	// 获取入驻企业列表
	private void readyToGetCompanyList() {
		companyList.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				String searchStr = searchEdit.getText().toString();
				if (searchStr == null || "".equals(searchStr)) {
					getCompanyList(true);
				} else {
					getSearchCompanyList(searchStr);
				}

			}
		});
		companyList.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				String searchStr = searchEdit.getText().toString();
				if (searchStr == null || "".equals(searchStr)) {
					getCompanyList(false);
				} else {
					companyList.onScrollComplete(true);
				}

			}
		});
		String searchStr = searchEdit.getText().toString();
		if (searchStr == null || "".equals(searchStr)) {
			getCompanyList(true);
		} else {
			getSearchCompanyList(searchStr);
		}
	}

	/**
	 * 初始化
	 */
	public void init() {
		// getCompanyList(true);// 获取入住企业
		readyToGetCompanyList();
		companyList.setAdapter(adapter);
		companyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						CompanyTagActivity.class);
				CompanyInfo comInfo = adapter.getItem(position - 1);
				intent.putExtra("eid", comInfo.getEid());
				startActivity(intent);
			}
		});
	}

	protected void refreshList() {

		adapter.notifyDataSetChanged();
		companyList.onRefreshComplete();
		// productAdapter.setDelect(false);
		// btnRight.setText("全部删除");
		// delectIds.clear();
		// for (ProductRec rec : prodList) {
		// rec.setCheck(false);
		// }

	}

	// 汉字转换成拼音

	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator = new PinyinComparator();

	public void getSelling(List<CompanyInfo> list) {
		CompanyInfo companyInfo = null;
		characterParser = CharacterParser.getInstance();
		for (int i = 0; i < list.size(); i++) {
			companyInfo = list.get(i);
			String pinyin = characterParser.getSelling(companyInfo.getEname());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				companyInfo.setSortLetters(sortString.toUpperCase());
			} else {
				companyInfo.setSortLetters("#");
			}
		}

	}

	// 获取入住企业
	public void getCompanyList(final boolean isFirst) {
		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(getActivity()).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		if (isFirst) {
			offset = 0;
			count = 20;
			prodList.clear();
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
					List<CompanyInfo> list = CompanyJsonUtils.parserCompany(
							getActivity(), new JSONObject(t.toString()));
					// adapter.bindDate(list);
					// companyList.setAdapter(adapter);
					if (list != null && list.size() > 0) {

						if (!isFirst) {
							prodList.addAll(list);
							// 汉字转换成拼音
							getSelling(prodList);
							// 根据a-z进行排序源数据
							Collections.sort(prodList, pinyinComparator);
							offset += prodList.size();
							Boolean ishas = (list.size() < 20) ? false : true;
							adapter.bindDate(prodList);
							companyList.onScrollComplete(ishas);
							refreshList();
						} else {
							prodList.clear();
							prodList.addAll(list);
							// 汉字转换成拼音
							getSelling(prodList);
							// 根据a-z进行排序源数据
							Collections.sort(prodList, pinyinComparator);
							offset += prodList.size();
							Boolean ishas = (list.size() < 20) ? false : true;
							adapter.bindDate(prodList);
							companyList.onScrollComplete(ishas);
							refreshList();
						}
					} else {
						companyList.onScrollComplete(false);
						refreshList();
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

	public class CompanyListAdapter extends BaseAdapter {

		private List<CompanyInfo> list = new ArrayList<CompanyInfo>();
		private Context context;

		private final int GRAY = 0;
		private final int WHITE = 1;

		public CompanyListAdapter(Context context) {
			this.context = context;
		}

		public void bindDate(List<CompanyInfo> list) {
			if (list == null) {
				list = new ArrayList<CompanyInfo>();
			}
			this.list = null;

			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public CompanyInfo getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (position % 2 == 0) {
				return WHITE;
			} else {
				return GRAY;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.company_list_item, parent, false);
				vh.attention_num = (TextView) convertView
						.findViewById(R.id.attention_num);
				vh.is_attention = (TextView) convertView
						.findViewById(R.id.is_attention);
				vh.ename = (TextView) convertView.findViewById(R.id.ename);
				vh.tvCatlog = (TextView) convertView.findViewById(R.id.catlog);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			if (getItemViewType(position) == GRAY) {
				convertView.setBackgroundColor(getActivity().getResources()
						.getColor(R.color.gray_55));
			} else {
				convertView.setBackgroundColor(getActivity().getResources()
						.getColor(R.color.white));
			}

			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);

			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				vh.tvCatlog.setVisibility(View.VISIBLE);
				vh.tvCatlog.setText(list.get(position).getSortLetters());
			} else {
				vh.tvCatlog.setVisibility(View.GONE);
			}
			vh.ename.setText(list.get(position).getEname());
			if (list.get(position).isIs_attention()) {
				vh.is_attention.setText(string.attention);
			} else {
				vh.is_attention.setText(string.no_attention);
			}
			vh.attention_num.setText(list.get(position).getAttention_num()
					+ "人关注");

			return convertView;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		public int getSectionForPosition(int position) {

			return list.get(position).getSortLetters().charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = list.get(i).getSortLetters();
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

	public static class ViewHolder {
		public TextView ename;
		public TextView is_attention;
		public TextView attention_num;
		public TextView tvCatlog;
	}
}
