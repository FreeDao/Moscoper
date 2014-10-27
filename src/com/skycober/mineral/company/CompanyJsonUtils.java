package com.skycober.mineral.company;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.bean.BlackInfo;
import com.skycober.mineral.bean.Company;
import com.skycober.mineral.util.SettingUtil;

import android.content.Context;
import android.widget.Toast;

public class CompanyJsonUtils {
	public static final String ERROR = "Errors";
	public static final String CODE = "Code";
	public static final String MESSAGE = "Message";
	public static final String RESULT = "Result";

	public static String parserResut(JSONObject jsonObject) {
		String code = null;
		try {
			JSONObject object = jsonObject.getJSONObject(ERROR);
			code = object.getString(CODE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}

	public static List<Company> parserHomePageCompany(Context context,
			JSONObject jsonObject) {
		List<Company> list = null;
		try {
			JSONObject object = jsonObject.getJSONObject(ERROR);
			if (1 == object.getInt(CODE)) {
				JSONArray jsonArray = jsonObject.getJSONArray(RESULT);
				list = new ArrayList<Company>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject company_object = jsonArray.getJSONObject(i);
					Company company = new Company();
					company.setEid(company_object.getString("eid"));
					company.setElogo(company_object.getString("elogo"));
					company.setEname(company_object.getString("ename"));
					list.add(company);
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public static List<BlackInfo> parserBlackList(Context context,
			JSONObject jsonObject) {
		List<BlackInfo> list = null;

		JSONObject object;
		try {
			object = jsonObject.getJSONObject(ERROR);
			if (1 == object.getInt(CODE)) {
				JSONArray jsonArray = jsonObject.getJSONArray(RESULT);
				list = new ArrayList<BlackInfo>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject blackInfo_object = jsonArray.getJSONObject(i);
					BlackInfo info = new BlackInfo();
					info.setEid(blackInfo_object.getString("eid"));
					info.setId(blackInfo_object.getString("id"));
					info.setIseid(blackInfo_object.getString("iseid"));
					info.setMid(blackInfo_object.getString("mid"));
					info.setName(blackInfo_object.getString("name"));
					list.add(info);
				}
			} else {
				Toast.makeText(context, object.getString(MESSAGE), 1).show();
				SettingUtil.getInstance(context).clearLoginInfo();
				FragmentChangeActivity.leftFragment.refreshLoginState(true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	/**
	 * 解析关注的企业列表的json
	 */

	public static final String COMPANY_ATTENTION_ID = "id";
	public static final String COMPANY_ID = "eid";
	public static final String COMPANY_NAME = "ename";
	public static final String COMPANY_ADD_TIME = "add_time";

	public static List<CompanyInfo> parseAttentionCompanys(JSONObject jsonObject) {
		List<CompanyInfo> list = null;
		try {
			JSONObject object = jsonObject.getJSONObject(ERROR);
			if (1 == object.getInt(CODE)) {
				list = new ArrayList<CompanyInfo>();
				JSONArray jsonArray = jsonObject.getJSONArray(RESULT);
				for (int i = 0; i < jsonArray.length(); i++) {

					CompanyInfo company = new CompanyInfo();
					JSONObject companyObject = jsonArray.getJSONObject(i);
					company.setId(companyObject.getString(COMPANY_ATTENTION_ID));
					company.setEid(companyObject.getString(COMPANY_ID));
					company.setEname(companyObject.getString(COMPANY_NAME));
					company.setAdd_time(Long.parseLong(companyObject
							.getString(COMPANY_ADD_TIME)));
					list.add(company);
				}
			} else {

				return null;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private static final String IS_ATTENTION = "is_attention";
	private static final String ATTENTION_NUM = "attention_num";

	/**
	 * 解析入住企业列表
	 */
	public static List<CompanyInfo> parserCompany(Context context,
			JSONObject jsonObject) {
		List<CompanyInfo> list = null;
		try {
			JSONObject object = jsonObject.getJSONObject(ERROR);
			if (1 == object.getInt(CODE)) {
				list = new ArrayList<CompanyInfo>();
				JSONArray companyArray = jsonObject.getJSONArray(RESULT);
				for (int i = 0; i < companyArray.length(); i++) {
					JSONObject companyObject = companyArray.getJSONObject(i);
					CompanyInfo company = new CompanyInfo();
					company.setEid(companyObject.getString(COMPANY_ID));
					company.setEname(companyObject.getString(COMPANY_NAME));
					String isAttention = companyObject.getString(IS_ATTENTION);
					if ("1".equals(isAttention)) {
						company.setIs_attention(true);
					} else {
						company.setIs_attention(false);
					}
					company.setAttention_num(companyObject
							.getString(ATTENTION_NUM));
					list.add(company);
				}
			} else {
				SettingUtil.getInstance(context).clearLoginInfo();
				FragmentChangeActivity.leftFragment.refreshLoginState(true);
				Toast.makeText(context, object.getString(MESSAGE),
						Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 解析企业标签
	 */

	private static final String TAG_DONE = "done";
	private static final String TAG_ONDONE = "donot";
	private static final String E_CATGER_ID = "e_cat_id";
	private static final String E_CATGER_NAME = "e_cat_name";
	private static final String TAG_LIST = "tag_list";
	private static final String TAG_ID = "tag_id";
	private static final String TAG_NAME = "tag_name";

	public static TagResult parserCateTage(JSONObject jsonObject) {
		TagResult tagResult = new TagResult();
		List<CatgerTag> attention_tag_list = new ArrayList<CatgerTag>();
		List<CatgerTag> no_attention_tag_list = new ArrayList<CatgerTag>();
		try {
			if (1 == jsonObject.getJSONObject(ERROR).getInt(CODE)) {
				JSONArray attention_tagArray = jsonObject.getJSONObject(RESULT)
						.getJSONArray(TAG_DONE);

				for (int i = 0; i < attention_tagArray.length(); i++) {
					JSONObject catgerObject = attention_tagArray
							.getJSONObject(i);
					CatgerTag catgerTag = new CatgerTag();
					catgerTag.setE_cat_id(catgerObject.getString(E_CATGER_ID));
					catgerTag.setE_cat_name(catgerObject
							.getString(E_CATGER_NAME));
					JSONArray attention_tag_array = catgerObject
							.getJSONArray(TAG_LIST);
					List<Tag> tag_list_attention = new ArrayList<Tag>();
					for (int j = 0; j < attention_tag_array.length(); j++) {
						JSONObject tag_object = attention_tag_array
								.getJSONObject(j);
						Tag attention_tag = new Tag();
						attention_tag.setTag_id(tag_object.getString(TAG_ID));
						attention_tag.setTag_name(tag_object
								.getString(TAG_NAME));
						attention_tag.setChecked(true);
						tag_list_attention.add(attention_tag);
					}
					catgerTag.setTag_list(tag_list_attention);

					attention_tag_list.add(catgerTag);

				}
				tagResult.setDone_tag_list(attention_tag_list);

				JSONArray no_attentionTagArray = jsonObject.getJSONObject(
						RESULT).getJSONArray(TAG_ONDONE);
				for (int i = 0; i < no_attentionTagArray.length(); i++) {
					JSONObject catgerObject = no_attentionTagArray
							.getJSONObject(i);
					CatgerTag catgerTag = new CatgerTag();
					catgerTag.setE_cat_id(catgerObject.getString(E_CATGER_ID));
					catgerTag.setE_cat_name(catgerObject
							.getString(E_CATGER_NAME));
					JSONArray no_attention_tag_array = catgerObject
							.getJSONArray(TAG_LIST);
					List<Tag> tag_list_no_attention = new ArrayList<Tag>();
					for (int j = 0; j < no_attention_tag_array.length(); j++) {
						JSONObject tag_object = no_attention_tag_array
								.getJSONObject(j);
						Tag attention_tag = new Tag();
						attention_tag.setTag_id(tag_object.getString(TAG_ID));
						attention_tag.setTag_name(tag_object
								.getString(TAG_NAME));
						tag_list_no_attention.add(attention_tag);
					}
					catgerTag.setTag_list(tag_list_no_attention);

					no_attention_tag_list.add(catgerTag);
				}

				tagResult.setDonot_tag_list(no_attention_tag_list);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tagResult;
	}

	/**
	 * 解析企业信息
	 */
	private static final String COMPANY_INDUSTRY = "industry";
	private static final String COMPANY_LOGO = "elogo";
	private static final String PRAISE_NUM = "praise_num";
	private static final String BLACK_NUM = "black_num";

	public static CompanyInfo parserCompanyInfo(JSONObject jsonObject) {
		CompanyInfo company = null;
		try {
			if (1 == jsonObject.getJSONObject(ERROR).getInt(CODE)) {
				company = new CompanyInfo();
				JSONObject companyObject = jsonObject.getJSONObject(RESULT);
				company.setEid(companyObject.getString(COMPANY_ID));
				company.setEname(companyObject.getString(COMPANY_NAME));
				company.setIndustry(companyObject.getString(COMPANY_INDUSTRY));
				company.setElogo(companyObject.getString(COMPANY_LOGO));
				company.setPraise_num(companyObject.getString(PRAISE_NUM));
				company.setBlack_num(companyObject.getString(BLACK_NUM));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return company;
	}
}
