package com.skycober.mineral.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.CategoryService;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.CategoryRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseCategory;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 个人标签（废弃）
 * @author Yes366
 *
 */
public class CategorySelectorActivity extends BaseActivity {
	private static final String TAG = "CategorySelectorActivity";

	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;
	@ViewInject(id = R.id.lv_category_select)
	ListView lvCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_selector);
		InitTopBar();
		InitListView();
		readyToLoadCategoryInfo();
	}

	private void InitListView() {
		List<Map<CategoryRec, List<CategoryRec>>> categoryRecList = new ArrayList<Map<CategoryRec, List<CategoryRec>>>();
		categoryAdapter = new CategoryAdapter(this,
				R.layout.attention_category_setting_listview_item,
				categoryRecList);
		lvCategory.setAdapter(categoryAdapter);
	}

	private void InitTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);

		btnRight.setImageResource(R.drawable.check_btn_selector);
		btnRight.setOnClickListener(btnRightClickListener);
		tvTitle.setText(R.string.select_category_page_title);
	}

	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	private View.OnClickListener btnRightClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			readyToSaveAttentionCategory();
			v.setEnabled(true);
		}
	};

	public class CategoryAdapter extends BaseAdapter {

		private List<Map<CategoryRec, List<CategoryRec>>> categoryList;
		private int layoutId;
		private LayoutInflater inflater;

		public CategoryAdapter(Context context, int layoutId,
				List<Map<CategoryRec, List<CategoryRec>>> categoryList) {
			inflater = LayoutInflater.from(context);
			this.layoutId = layoutId;
			this.categoryList = categoryList;
		}

		public List<Map<CategoryRec, List<CategoryRec>>> getCategoryList() {
			return categoryList;
		}

		public void setCategoryList(
				List<Map<CategoryRec, List<CategoryRec>>> categoryList) {
			if (null == this.categoryList) {
				this.categoryList = categoryList;
			} else {
				this.categoryList.clear();
				if (null != categoryList && categoryList.size() > 0) {
					this.categoryList.addAll(categoryList);
				}
			}
			notifyDataSetChanged();
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
		public View getView(int pos, View view, ViewGroup parent) {
			if (null == view) {
				view = inflater.inflate(layoutId, null);
			}
			Map<CategoryRec, List<CategoryRec>> categoryRecMap = categoryList
					.get(pos);
			final CategoryRec rec = getKeyFromMap(categoryRecMap);
			TextView tvGroupName = (TextView) view
					.findViewById(R.id.category_name);
			if (null != rec) {
				tvGroupName.setText(rec.getName());
			}
			Button btnSelectStaus = (Button) view.findViewById(R.id.select_btn);
			boolean isFollowedForGroup = rec.isFollowed();
			if (isFollowedForGroup) {
				btnSelectStaus
						.setBackgroundResource(R.drawable.card_faceshow_cancel);
				btnSelectStaus.setText(R.string.group_btn_all_un_select);
			} else {
				btnSelectStaus
						.setBackgroundResource(R.drawable.card_faceshow_select);
				btnSelectStaus.setText(R.string.group_btn_all_select);
			}

			GridView gvCategory = (GridView) view
					.findViewById(R.id.gv_sub_category);
			final List<CategoryRec> subCategoryList = categoryRecMap.get(rec);
			final SubCategoryAdapter subAdapter = new SubCategoryAdapter(
					CategorySelectorActivity.this,
					R.layout.category_select_child_item_view, subCategoryList);
			gvCategory.setAdapter(subAdapter);
			gvCategory
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int pos, long id) {
							CategoryRec subRec = subCategoryList.get(pos);
							boolean isFollowed = subRec.isFollowed();
							subRec.setFollowed(!isFollowed);
							boolean isAllSelect = true;
							for (CategoryRec categoryRec : subCategoryList) {
								isAllSelect = isAllSelect
										&& categoryRec.isFollowed();
								if (!isAllSelect)
									break;
							}
							rec.setFollowed(isAllSelect);
							notifyDataSetChanged();
						}
					});

			btnSelectStaus.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean isTmpFollowed = !rec.isFollowed();
					rec.setFollowed(isTmpFollowed);
					if (null != subCategoryList) {
						for (CategoryRec categoryRec : subCategoryList) {
							categoryRec.setFollowed(isTmpFollowed);
						}
					}
					notifyDataSetChanged();
				}
			});

			return view;
		}

	}

	private CategoryRec getKeyFromMap(
			Map<CategoryRec, List<CategoryRec>> categoryRecMap) {
		if (null != categoryRecMap) {
			Iterator<CategoryRec> recIterator = categoryRecMap.keySet()
					.iterator();
			while (recIterator.hasNext()) {
				CategoryRec categoryRec = (CategoryRec) recIterator.next();
				return categoryRec;
			}
		}
		return null;
	}

	public class SubCategoryAdapter extends BaseAdapter {
		private List<CategoryRec> categoryList;
		private int layoutId;
		private LayoutInflater inflater;

		public SubCategoryAdapter(Context context, int layoutId,
				List<CategoryRec> categoryList) {
			inflater = LayoutInflater.from(context);
			this.layoutId = layoutId;
			this.categoryList = categoryList;
		}

		public List<CategoryRec> getCategoryList() {
			return categoryList;
		}

		public void setCategoryList(List<CategoryRec> categoryList) {
			if (null == this.categoryList) {
				this.categoryList = categoryList;
			} else {
				this.categoryList.clear();
				if (null != categoryList && categoryList.size() > 0) {
					this.categoryList.addAll(categoryList);
				}
			}
			notifyDataSetChanged();
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
		public View getView(int pos, View view, ViewGroup parent) {
			if (null == view) {
				view = inflater.inflate(layoutId, null);
			}
			final CategoryRec category = categoryList.get(pos);
			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			ImageView ivSelect = (ImageView) view.findViewById(R.id.ivSelect);
			tvName.setText(category.getName());
			boolean isFollowedForChild = category.isFollowed();
			if (isFollowedForChild) {
				ivSelect.setImageResource(R.drawable.card_faceshow_check);
			} else {
				ivSelect.setImageResource(R.drawable.card_faceshow_nocheck);
			}
			return view;
		}
	}

	private void readyToLoadCategoryInfo() {
		if (!NetworkUtil.getInstance().existNetwork(
				CategorySelectorActivity.this)) {
			Toast.makeText(CategorySelectorActivity.this,
					R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources().getString(R.string.get_category_ing));
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
							CategorySelectorActivity.this, msg);
				} else {
					Toast.makeText(CategorySelectorActivity.this,
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
					BaseResponse br = parser.parseGetCategory(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseCategory response = (ResponseCategory) br;
						Trace.d(TAG,
								currMethod
										+ ",response->"
										+ (null == response ? "null" : response
												.toString()));
						List<CategoryRec> catList = response.getCategoryRec();
						refreshCategoryList(catList);
					} else {
						String message = getString(R.string.get_category_failed_rem);
						String msg = getString(R.string.get_category_failed_rem);
						if (null == br) {
							msg = "readyToLoadCategoryInfo: Result:BaseResponse is null.";
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
									CategorySelectorActivity.this, msg);
						} else {
							Toast.makeText(CategorySelectorActivity.this,
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategorySelectorActivity.this, msg);
					} else {
						Toast.makeText(CategorySelectorActivity.this,
								R.string.get_category_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

		};
		cs.GetCategoryForHasLogin(CategorySelectorActivity.this, callBack);
	}

	protected void readyToSaveAttentionCategory() {
		if (null != categoryRecList && categoryRecList.size() > 0) {
			addAttentionCategoryRecList.clear();
			cancelAttentionCategoryRecList.clear();
			for (int i = 0; i < categoryRecList.size(); i++) {
				int pos = i;
				Map<CategoryRec, List<CategoryRec>> categoryRecMap = categoryRecList
						.get(pos);
				CategoryRec rec = getKeyFromMap(categoryRecMap);
				List<CategoryRec> subCategoryList = categoryRecMap.get(rec);
				boolean isFollowedForGroup = rec.isFollowed();
				if (isFollowedForGroup) {
					addAttentionCategoryRecList.add(rec);
					cancelAttentionCategoryRecList.addAll(subCategoryList);
				} else {
					cancelAttentionCategoryRecList.add(rec);
					for (CategoryRec categoryRec : subCategoryList) {
						if (categoryRec.isFollowed()) {
							addAttentionCategoryRecList.add(categoryRec);
						} else {
							cancelAttentionCategoryRecList.add(categoryRec);
						}
					}
				}
			}
			if (null != cancelAttentionCategoryRecList
					&& cancelAttentionCategoryRecList.size() > 0) {
				readyToCancelAttentionCategory();
			} else if (null != addAttentionCategoryRecList
					&& addAttentionCategoryRecList.size() > 0) {
				readyToAddAttionCategory();
			}
		}
	}

	private List<CategoryRec> addAttentionCategoryRecList = new ArrayList<CategoryRec>();

	protected void readyToAddAttionCategory() {
		if (!NetworkUtil.getInstance().existNetwork(
				CategorySelectorActivity.this)) {
			releaseScreen();
			Toast.makeText(CategorySelectorActivity.this,
					R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToAddAttionCategory:";
		lockScreen(getString(R.string.setting_my_attention_cat_tag_send_ing));
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
							CategorySelectorActivity.this, msg);
				} else {
					Toast.makeText(CategorySelectorActivity.this,
							R.string.setting_my_attention_cat_tag_failed_rem, Toast.LENGTH_SHORT)
							.show();
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
					BaseResponse br = parser.parseAddAttentionTags(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						Toast.makeText(CategorySelectorActivity.this,
									R.string.setting_my_attention_cat_tag_succeed_rem,
									Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
					} else {
						String message = getString(R.string.setting_my_attention_cat_tag_failed_rem);
						String msg = getString(R.string.setting_my_attention_cat_tag_failed_rem);
						if (null == br) {
							msg = currMethod+": Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();// TODO
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									CategorySelectorActivity.this, msg);
						} else {
							Toast.makeText(CategorySelectorActivity.this,
									message, Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategorySelectorActivity.this, msg);
					} else {
						Toast.makeText(CategorySelectorActivity.this,
								R.string.setting_my_attention_cat_tag_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String type = String.valueOf(2);
		StringBuffer buff = new StringBuffer();
		for (CategoryRec catRec : addAttentionCategoryRecList) {
			buff.append(catRec.getId()).append(",");
		}
		if(buff.indexOf(",") != -1) buff.delete(buff.length() - 1, buff.length());
		String followId = buff.toString();
		gs.AddMyAttentionTags(CategorySelectorActivity.this, type, followId, callBack);
	}
	
	private List<CategoryRec> cancelAttentionCategoryRecList = new ArrayList<CategoryRec>();

	protected void readyToCancelAttentionCategory() {
		if(!NetworkUtil.getInstance().existNetwork(CategorySelectorActivity.this)){
			Toast.makeText(CategorySelectorActivity.this, R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToRemoveAttention:";
		lockScreen(getString(R.string.setting_my_attention_cat_tag_send_ing));
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
					ExceptionRemHelper.showExceptionReport(CategorySelectorActivity.this, msg);
				} else {
					Toast.makeText(CategorySelectorActivity.this,
							R.string.setting_my_attention_cat_tag_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}
			
			@Override
			public void onSuccess(Object t) {
				if (null != t) {
					String json = t.toString();
					Trace.d(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseRemoveTag(json);
					if (null != br && ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						if(null != addAttentionCategoryRecList && addAttentionCategoryRecList.size() > 0){
							readyToAddAttionCategory();
						}else{
							releaseScreen();
							Toast.makeText(CategorySelectorActivity.this,
									R.string.setting_my_attention_cat_tag_succeed_rem,
									Toast.LENGTH_SHORT).show();
							setResult(RESULT_OK);
							finish();
						}
					} else {
						releaseScreen();
						String message = getString(R.string.setting_my_attention_cat_tag_failed_rem);
						String msg = getString(R.string.setting_my_attention_cat_tag_failed_rem);
						if (null == br) {
							msg = "AddTag: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();// TODO
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									CategorySelectorActivity.this, msg);
						} else {
							Toast.makeText(CategorySelectorActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					releaseScreen();
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(CategorySelectorActivity.this,msg);
					} else {
						Toast.makeText(CategorySelectorActivity.this, R.string.setting_my_attention_cat_tag_failed_rem, Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String typeId = String.valueOf(2);
		StringBuffer buff = new StringBuffer();
		for (CategoryRec catRec : cancelAttentionCategoryRecList) {
			buff.append(catRec.getId()).append(",");
		}
		if(buff.indexOf(",") != -1) buff.delete(buff.length() - 1, buff.length());
		String followId = buff.toString();
 		gs.RemoveTag(CategorySelectorActivity.this, typeId, followId, callBack);
	}

	private List<Map<CategoryRec, List<CategoryRec>>> categoryRecList = new ArrayList<Map<CategoryRec, List<CategoryRec>>>();
	private CategoryAdapter categoryAdapter;

	protected void refreshCategoryList(List<CategoryRec> catList) {
		for (int i = 0; i <= catList.size() - 1; i++) {
			if (catList.get(i).getParentId().equals("0")) {
				CategoryRec categoryRec = catList.get(i);
				boolean isFollowedForGroup = categoryRec.isFollowed();
				List<CategoryRec> categoryRecslist = new ArrayList<CategoryRec>();
				for (int j = 0; j <= catList.size() - 1; j++) {
					if (catList.get(j).getParentId()
							.equals(categoryRec.getId())) {
						CategoryRec subCatRec = catList.get(j);
						if(isFollowedForGroup){
							subCatRec.setFollowed(true);
						}
						categoryRecslist.add(subCatRec);
					}
				}
				Map<CategoryRec, List<CategoryRec>> categoryMap = new HashMap<CategoryRec, List<CategoryRec>>(1);
				categoryMap.put(categoryRec, categoryRecslist);
				categoryRecList.add(categoryMap);
			}
		}
		categoryAdapter.setCategoryList(categoryRecList);
	}

	// private CategoryAdapter groupCategoryAdapter;
	// private List<GroupCategoryRec> groupCategoryList;
	// private Map<GroupCategoryRec, List<ChildCategoryRec>> groupCategoryMap;
	// private void InitListView(){
	// initListViewData();
	// groupCategoryAdapter = new CategoryAdapter(this,
	// R.layout.category_select_group_item_view,
	// R.layout.category_select_child_view, groupCategoryList,
	// groupCategoryMap);
	// lvCategory.setAdapter(groupCategoryAdapter);
	// Utility.setListViewHeightBasedOnChildren(lvCategory,
	// groupCategoryAdapter);
	// lvCategory.setOnGroupCollapseListener(new
	// ExpandableListView.OnGroupCollapseListener() {
	//
	// @Override
	// public void onGroupCollapse(int groupPosition) {
	// Utility.setListViewHeightBasedOnChildren(lvCategory,
	// groupCategoryAdapter);
	// }
	// });
	// lvCategory.setOnGroupExpandListener(new
	// ExpandableListView.OnGroupExpandListener() {
	//
	// @Override
	// public void onGroupExpand(int groupPosition) {
	// Utility.setListViewHeightBasedOnChildren(lvCategory,
	// groupCategoryAdapter);
	// }
	// });
	// }
	//
	// private void initListViewData() {
	// groupCategoryList = new ArrayList<GroupCategoryRec>();
	// groupCategoryMap = new HashMap<GroupCategoryRec,
	// List<ChildCategoryRec>>();
	// }
	//
	//
	//

	//
	// protected void refreshCategoryList(List<CategoryRec> catList) {
	// final String currMethod = "refreshCategoryList:";
	// if(null == catList || catList.size() == 0) {
	// Log.e(TAG, currMethod +"null == catList");
	// Toast.makeText(this, R.string.add_category_rem_no_data,
	// Toast.LENGTH_SHORT).show();
	// return;
	// }
	// //构建本地数据层
	//
	// }
	//
	// public class CategoryAdapter extends BaseExpandableListAdapter{
	//
	// private LayoutInflater inflater;
	// private int groupLayoutId;
	// private int childLayoutId;
	// private List<GroupCategoryRec> groupCategoryList;//组分类List
	// private Map<GroupCategoryRec, List<ChildCategoryRec>>
	// groupCategoryMap;//组分类Map: k - 组分类 v - 子分类List
	//
	// private Map<Integer, UnScrollGridView> gvCategoryMap = new
	// HashMap<Integer, UnScrollGridView>();
	// public CategoryAdapter(Context context, int groupLayoutId, int
	// childLayoutId, List<GroupCategoryRec> groupList,
	// Map<GroupCategoryRec, List<ChildCategoryRec>> groupCategoryMap){
	// inflater = LayoutInflater.from(context);
	// this.groupLayoutId = groupLayoutId;
	// this.childLayoutId = childLayoutId;
	// this.groupCategoryList = groupList;
	// this.groupCategoryMap = groupCategoryMap;
	// }
	//
	// @Override
	// public Object getChild(int groupPosition, int childPosition) {
	// // if(null != groupCategoryList && groupPosition <
	// groupCategoryList.size()){
	// GroupCategoryRec group = groupCategoryList.get(groupPosition);
	// // if(null != groupCategoryMap && groupCategoryMap.containsKey(group)){
	// List<ChildCategoryRec> childList = groupCategoryMap.get(group);
	// return childList.get(childPosition);
	// // }
	// // }
	// // return null;
	// }
	//
	// @Override
	// public long getChildId(int groupPosition, int childPosition) {
	// return childPosition;
	// }
	//
	// @Override
	// public View getChildView(int groupPosition, int childPosition,
	// boolean isLastChild, View convertView, ViewGroup parent) {
	// convertView = inflater.inflate(childLayoutId, null);
	// UnScrollGridView gvCategory = (UnScrollGridView)
	// convertView.findViewById(R.id.gvCategory);
	// gvCategory.setGravity(Gravity.CENTER);
	// gvCategory.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
	// if(null == gvCategory.getAdapter()){
	// GroupCategoryRec key = groupCategoryList.get(groupPosition);
	// List<ChildCategoryRec> recList = groupCategoryMap.get(key);
	// ChildCategoryAdapter adapter = new
	// ChildCategoryAdapter(CategorySelectorActivity.this,
	// R.layout.category_select_child_item_view, recList);
	// gvCategory.setAdapter(adapter);
	// final ChildCategoryAdapter tmpAdapter = adapter;
	// final int tmpPos = groupPosition;
	// gvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int pos, long id) {
	// ChildCategoryRec rec = tmpAdapter.getCategoryList().get(pos);
	// rec.setSelect(!rec.isSelect());
	// if(!rec.isSelect()){
	// if(groupCategoryList.get(tmpPos).getSelectType() ==
	// SelectType.AllSelect){
	// groupCategoryList.get(tmpPos).setSelectType(SelectType.PartSelect);
	// tmpAdapter.setStateSelect(SelectType.PartSelect);
	// notifyDataSetChanged();
	// }else{
	// tmpAdapter.notifyDataSetChanged();
	// }
	// }else{
	// boolean isAllSelect = true;
	// for (ChildCategoryRec child : tmpAdapter.getCategoryList()) {
	// isAllSelect = child.isSelect() & isAllSelect;
	// if(!isAllSelect) break;
	// }
	// groupCategoryList.get(tmpPos).setSelectType(isAllSelect ?
	// SelectType.AllSelect : SelectType.PartSelect);
	// if(isAllSelect){
	// tmpAdapter.setStateSelect(SelectType.AnySelect);
	// notifyDataSetChanged();
	// }else{
	// tmpAdapter.setStateSelect(SelectType.PartSelect);
	// tmpAdapter.notifyDataSetChanged();
	// }
	// }
	// }
	// });
	// }else{
	// ChildCategoryAdapter adapter = (ChildCategoryAdapter)
	// gvCategory.getAdapter();
	// GroupCategoryRec key = groupCategoryList.get(groupPosition);
	// List<ChildCategoryRec> recList = groupCategoryMap.get(key);
	// adapter.setCategoryList(recList);
	// adapter.notifyDataSetChanged();
	// }
	// gvCategoryMap.put(groupPosition, gvCategory);
	// return convertView;
	// }
	//
	// @Override
	// public int getChildrenCount(int groupPosition) {
	// GroupCategoryRec groupKey = groupCategoryList.get(groupPosition);
	// int count = 0;
	// if(groupCategoryMap.containsKey(groupKey)){
	// count = 1;
	// }
	// return count;
	// }
	//
	// @Override
	// public Object getGroup(int groupPosition) {
	// return groupCategoryList.get(groupPosition);
	// }
	//
	// @Override
	// public int getGroupCount() {
	// return groupCategoryList.size();
	// }
	//
	// @Override
	// public long getGroupId(int groupPosition) {
	// return groupPosition;
	// }
	//
	// @Override
	// public View getGroupView(int groupPosition, boolean isExpanded,
	// View convertView, ViewGroup parent) {
	// LinearLayout group = null;
	// if(null == convertView){
	// group = getTextView(CategorySelectorActivity.this);
	// }else{
	// group = (LinearLayout) convertView;
	// }
	// TextView textView = (TextView) group.getChildAt(1);
	// textView.setText(groupCategoryList.get(groupPosition).getName());
	// ImageView iv = (ImageView) group.getChildAt(0);
	// if (isExpanded) {
	// iv.setBackgroundResource(R.drawable.card_faceshow_list_on);
	// } else {
	// iv.setBackgroundResource(R.drawable.card_faceshow_list_off);
	// }
	// Button btnSelect = (Button) group.getChildAt(2);
	// btnSelect.setTag(groupPosition);
	// final int groupIndex = groupPosition;
	// btnSelect.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// //isResultOK = true;
	// SelectType selectType =
	// groupCategoryList.get(groupIndex).getSelectType();
	// Button tempBtnSelect = (Button) v;
	// if (selectType == SelectType.AllSelect) {
	// tempBtnSelect
	// .setBackgroundResource(R.drawable.card_faceshow_select);
	// tempBtnSelect
	// .setText(R.string.group_btn_all_select);
	// groupCategoryList.get(groupIndex).setSelectType(SelectType.NoSelect);
	// setAllSelect(false, groupIndex);
	// } else if (selectType == SelectType.NoSelect
	// || selectType == SelectType.PartSelect) {
	// tempBtnSelect
	// .setBackgroundResource(R.drawable.card_faceshow_cancel);
	// tempBtnSelect
	// .setText(R.string.group_btn_all_un_select);
	// groupCategoryList.get(groupIndex).setSelectType(SelectType.AllSelect);
	// setAllSelect(true, groupIndex);
	// }
	// }
	// });
	//
	// SelectType selectType =
	// groupCategoryList.get(groupPosition).getSelectType();
	// if (selectType == SelectType.AllSelect) {
	// btnSelect
	// .setBackgroundResource(R.drawable.card_faceshow_cancel);
	// btnSelect.setText(R.string.group_btn_all_un_select);
	// setAllSelect(true, groupIndex);
	// } else {
	// btnSelect
	// .setBackgroundResource(R.drawable.card_faceshow_select);
	// btnSelect.setText(R.string.group_btn_all_select);
	// if (selectType == SelectType.NoSelect) {
	// setAllSelect(false, groupIndex);
	// }
	// }
	// final TextView tempTV = textView;
	// group.setOnTouchListener(new View.OnTouchListener() {
	//
	// @Override
	// public boolean onTouch(View arg0, MotionEvent arg1) {
	// tempTV.setTextColor(Color.LTGRAY);
	// return false;
	// }
	// });
	// return group;
	// }
	//
	// public void setAllSelect(boolean isSelect) {
	// if (null == groupCategoryList)
	// return;
	// for (GroupCategoryRec rec : groupCategoryList) {
	// rec.setSelectType(SelectType.AllSelect);
	// }
	// notifyDataSetChanged();
	// }
	//
	// public void setAllSelect(boolean isAllSelect, int groupPosition) {
	// GroupCategoryRec rec = groupCategoryList.get(groupPosition);
	// List<ChildCategoryRec> childRecList = groupCategoryMap.get(rec);
	// for (ChildCategoryRec child: childRecList) {
	// child.setSelect(isAllSelect);
	// }
	// if (gvCategoryMap.containsKey(groupPosition)) {
	// UnScrollGridView gvCategory = gvCategoryMap.get(groupPosition);
	// ChildCategoryAdapter adapter = (ChildCategoryAdapter)
	// gvCategory.getAdapter();
	// if (null == adapter) {
	// notifyDataSetChanged();
	// } else {
	// adapter.notifyDataSetChanged();
	// }
	// } else {
	// notifyDataSetChanged();
	// }
	// }
	//
	// @Override
	// public boolean hasStableIds() {
	// return true;
	// }
	//
	// @Override
	// public boolean isChildSelectable(int groupPosition, int childPosition) {
	// return true;
	// }
	//
	// public LinearLayout getTextView(Context context) {
	// LinearLayout group = (LinearLayout) inflater.inflate(groupLayoutId,
	// null);
	// group.setLayoutParams(new
	// AbsListView.LayoutParams(LayoutParams.FILL_PARENT, 48));
	// return group;
	// }
	// }
	//
	// public class ChildCategoryAdapter extends BaseAdapter{
	//
	// private List<ChildCategoryRec> categoryList;
	// private int layoutId;
	// private LayoutInflater inflater;
	// public ChildCategoryAdapter(Context context, int layoutId,
	// List<ChildCategoryRec> recList){
	// inflater = LayoutInflater.from(context);
	// this.layoutId = layoutId;
	// this.categoryList = recList;
	// }
	//
	// public List<ChildCategoryRec> getCategoryList() {
	// return categoryList;
	// }
	//
	// public void setCategoryList(List<ChildCategoryRec> categoryList) {
	// if(null == this.categoryList){
	// this.categoryList = categoryList;
	// }else{
	// this.categoryList.clear();
	// if(null != categoryList && categoryList.size() > 0){
	// this.categoryList.addAll(categoryList);
	// }
	// }
	// notifyDataSetChanged();
	// }
	//
	// private SelectType stateSelect = SelectType.PartSelect;
	//
	// public SelectType getStateSelect() {
	// return stateSelect;
	// }
	//
	// public void setStateSelect(SelectType stateSelect) {
	// this.stateSelect = stateSelect;
	// }
	//
	// @Override
	// public int getCount() {
	// return categoryList.size();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return categoryList.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// if(null == convertView){
	// convertView = inflater.inflate(layoutId, null);
	// }
	// TextView tvName = (TextView) convertView
	// .findViewById(R.id.tvName);
	// ImageView ivSelect = (ImageView) convertView
	// .findViewById(R.id.ivSelect);
	// ChildCategoryRec rec = categoryList.get(position);
	// tvName.setText(rec.getName());
	// if (stateSelect == SelectType.AnySelect) {
	// ivSelect.setBackgroundResource(R.drawable.card_faceshow_check);
	// categoryList.get(position).setSelect(true);
	// } else if (stateSelect == SelectType.AnyNoSelect) {
	// ivSelect.setBackgroundResource(R.drawable.card_faceshow_nocheck);
	// categoryList.get(position).setSelect(false);
	// } else if (stateSelect == SelectType.PartSelect) {
	// boolean isSelect = rec.isSelect();
	// if (isSelect) {
	// ivSelect.setBackgroundResource(R.drawable.card_faceshow_check);
	// ivSelect.setVisibility(View.VISIBLE);
	// } else {
	// ivSelect.setBackgroundResource(R.drawable.card_faceshow_nocheck);
	// }
	// }
	// return convertView;
	// }
	//
	// }

	public class GroupCategoryRec extends CategoryRec {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private SelectType selectType;

		public SelectType getSelectType() {
			return selectType;
		}

		public void setSelectType(SelectType selectType) {
			this.selectType = selectType;
		}

		@Override
		public String toString() {
			return "GroupCategoryRec [selectType=" + selectType
					+ ", toString()=" + super.toString() + "]";
		}

	}

	public enum SelectType {
		AllSelect, PartSelect, NoSelect, AnySelect, AnyNoSelect
	}

	public class ChildCategoryRec extends CategoryRec {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean isSelect;

		public boolean isSelect() {
			return isSelect;
		}

		public void setSelect(boolean isSelect) {
			this.isSelect = isSelect;
		}

		@Override
		public String toString() {
			return "ChildCategoryRec [isSelect=" + isSelect + ", toString()="
					+ super.toString() + "]";
		}

	}
}
