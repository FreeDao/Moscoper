package com.skycober.mineral.company;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.widget.MyRemDialog;

public class CompanyTagListFragment extends FragBaseActivity {

	private ExpandableListView tags_catger_list;
	private String eid;
	private boolean isAttention;
	private String jsonString;
	private TagResult tagResult;
	private List<CatgerTag> tag_list;

	private List<Tag> attention_tag;
	private Button attention_but;
	private ExpandableAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		eid = bundle.getString("eid");
		isAttention = bundle.getBoolean("isAttention");
		jsonString = bundle.getString("jsonString");
		attention_tag = new ArrayList<Tag>();
		try {
			tagResult = CompanyJsonUtils.parserCateTage(new JSONObject(
					jsonString));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isAttention) {
			tag_list = tagResult.getDone_tag_list();
			if (tag_list != null) {
				for (CatgerTag catgerTag : tag_list) {
					for (Tag tag : catgerTag.getTag_list()) {
						attention_tag.add(tag);
					}
				}
			}

		} else {
			tag_list = tagResult.getDonot_tag_list();
			List<CatgerTag> tags = tagResult.getDone_tag_list();
			if (tags != null && tags.size() != 0) {
				for (CatgerTag catgerTag : tags) {
					for (Tag tag : catgerTag.getTag_list()) {
						attention_tag.add(tag);
					}
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.company_tag_list_fragment,
				container, false);
		tags_catger_list = (ExpandableListView) view
				.findViewById(R.id.tag_catger);

		attention_but = (Button) view.findViewById(R.id.attention_but);
		attention_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (attention_tag.size() == 0) {
					MyRemDialog exitDialog = new MyRemDialog(getActivity(),
							R.style.Dialog);
					exitDialog.setTitle(R.string.company_waring);
					exitDialog.setMessage(R.string.company_waring_message);
					exitDialog.setPosBtnText(R.string.common_dialog_btn_ok);
					exitDialog
							.setNegBtnText(R.string.exit_app_dialog_btn_cancel);
					exitDialog.setNegBtnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getActivity().finish();
						}
					});
					exitDialog
							.setPosBtnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
								}
							});
					exitDialog.show();
				} else {
					addAttentionTag();
					Intent intent = new Intent(getActivity(),
							CompanyTagActivity.class);
					intent.putExtra("eid", eid);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}

			}
		});

		tags_catger_list.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
		adapter = new ExpandableAdapter(getActivity());
		adapter.setdate(tag_list);
		tags_catger_list.setAdapter(adapter);
		if (isAttention) {
			for (int i = 0; i < adapter.getGroupCount(); i++) {
				tags_catger_list.expandGroup(i);
			}
		}
		tags_catger_list.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				CheckBox check = (CheckBox) v.findViewById(R.id.tag_check);
				Tag tag = tag_list.get(groupPosition).getTag_list()
						.get(childPosition);
				if (tag.isChecked()) {
					tag.setChecked(false);
					attention_tag.remove(tag);
					check.setChecked(false);
					v.setBackgroundColor(getResources().getColor(
							R.color.gray_55));
				} else {
					tag.setChecked(true);
					attention_tag.add(tag);
					check.setChecked(true);
					v.setBackgroundColor(getResources().getColor(R.color.white));
				}
				return false;
			}
		});
		return view;
	}

	/**
	 * Ìí¼Ó¹Ø×¢
	 */
	public void addAttentionTag() {
		AjaxParams params = new AjaxParams();
		params.put("eid", eid);
		String tag_ids = "";
		for (int i = 0; i < attention_tag.size(); i++) {
			if ("".equals(tag_ids)) {
				tag_ids = attention_tag.get(0).getTag_id();
			} else {
				tag_ids = tag_ids + "," + attention_tag.get(i).getTag_id();
			}
		}
		System.out.println("==tag_ids===" + tag_ids);
		params.put("tag_ids", tag_ids);

		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(getActivity()).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.COMPANY_ADD_ATTENTION_TAG, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onSuccess(Object t, HttpResponse response) {
						// TODO Auto-generated method stub
						super.onSuccess(t, response);
						try {
							JSONObject jsonObject = new JSONObject(t.toString());
							System.out.println("==addAttentionTag==="
									+ jsonObject.toString());
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

	public class ExpandableAdapter extends BaseExpandableListAdapter {
		private Context context;
		private List<CatgerTag> tags = new ArrayList<CatgerTag>();

		public void setdate(List<CatgerTag> list) {
			if (list != null) {
				tags = list;
			}

		}

		public ExpandableAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return tags.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return tags.get(groupPosition).getTag_list().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return tags.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return tags.get(groupPosition).getTag_list().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.expandable_group_item, null, false);

			}

			TextView tv_group = (TextView) convertView.findViewById(R.id.group);
			tv_group.setText(tags.get(groupPosition).getE_cat_name());

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.tag_list_item, parent, false);
				vh = new ViewHolder();
				vh.tag_check = (CheckBox) convertView
						.findViewById(R.id.tag_check);
				vh.tagName = (TextView) convertView.findViewById(R.id.tag_name);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			vh.tagName.setText(tags.get(groupPosition).getTag_list()
					.get(childPosition).getTag_name());
			boolean isChecked = tags.get(groupPosition).getTag_list()
					.get(childPosition).isChecked();
			vh.tag_check.setChecked(isChecked);
			if (isChecked) {
				convertView.setBackgroundColor(getResources().getColor(
						R.color.white));
			} else {
				convertView.setBackgroundColor(getResources().getColor(
						R.color.gray_55));
			}

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	public static class ViewHolder {
		public TextView tagName;
		public CheckBox tag_check;
	}
}
