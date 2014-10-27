package com.skycober.mineral.company.head_page_company;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.skycober.mineral.R;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.bean.Company;
import com.skycober.mineral.company.CompanyJsonUtils;
import com.skycober.mineral.company.CompanyTagActivity;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.SettingUtil;

public class HomePageFragment extends Fragment {
	private GridView gridView;
	private List<Company> companies;
	private int[] companyIds = { R.drawable.suning_tiantongyuan,
			R.drawable.guomei, R.drawable.suning_beiqijia, R.drawable.bhg,
			R.drawable.suning_yayuncun, R.drawable.jialefu,
			R.drawable.suning_lishuiqiao, R.drawable.woerma,
			R.drawable.suning_huairou };

	private int[] companyIds_lager = { R.drawable.suning_tiantongyuan_lager,
			R.drawable.guomei_lager, R.drawable.suning_beiqijia_lager,
			R.drawable.bhg_lager, R.drawable.suning_yayuncun_lager,
			R.drawable.jialefu_lager, R.drawable.suning_lishuiqiao_lager,
			R.drawable.woerma_lager, R.drawable.suning_huairou_lager };

	private String[] eids = { "58", "53", "48", "43", "49", "39", "51", "41",
			"32" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	static final int REQUEST_CODE_LOGIN = 100;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.home_page_fragment, null, false);
		gridView = (GridView) view.findViewById(R.id.companys);
		companies = getData();
		getCompanyList();
		MyAdapter myAdapter = new MyAdapter(getActivity());
		gridView.setAdapter(myAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				boolean isLogin = !SettingUtil
						.getInstance(getActivity())
						.getValue(SettingUtil.KEY_LOGIN_USER_ID,
								SettingUtil.DEFAULT_LOGIN_USER_ID)
						.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
				if (companies.get(position).getEid() != null) {
					if (isLogin) {
						Intent intent = new Intent(getActivity(),
								CompanyTagActivity.class);
						intent.putExtra("eid", companies.get(position).getEid());
						startActivity(intent);
					} else {
						Intent inte = new Intent(getActivity(),
								LoginActivity.class);
						startActivityForResult(inte, REQUEST_CODE_LOGIN);
					}

				}

			}
		});
		return view;
	}

	public void getCompanyList() {
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.HOMEPAGE_COMPANY_LIST_URL;
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
					System.out.println("=====getCompanyList======"+new JSONObject(t.toString()));
					
					
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

	public List<Company> getData() {
		List<Company> list = new ArrayList<Company>();
		int height = getActivity().getWindowManager().getDefaultDisplay()
				.getHeight();
		for (int i = 0; i < 9; i++) {
			Company company = new Company();
			// if (i < 7) {
			if (height > 1200) {
				company.setPictureId(companyIds_lager[i]);
			} else {
				company.setPictureId(companyIds[i]);
			}

			company.setEid(eids[i]);
			// }

			list.add(company);
		}

		return list;
	}

	public class MyAdapter extends BaseAdapter {
		private Context context;

		public MyAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return companies.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return companies.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.home_page_gridview, null, false);

			}
			imageView = (ImageView) convertView
					.findViewById(R.id.company_image);
			// if(position ==7){
			imageView.setImageResource(companies.get(position).getPictureId());
			// }else{
			// imageView.setBackgroundResource(companies.get(position).getPictureId());
			// AnimationDrawable animationDrawable = (AnimationDrawable)
			// imageView.getBackground();
			// animationDrawable.start();
			// }

			return convertView;
		}

	}

}
