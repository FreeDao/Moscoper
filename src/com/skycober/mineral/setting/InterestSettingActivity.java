package com.skycober.mineral.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.tsz.afinal.http.AjaxCallBack;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.js.cloudtags.KeywordsFlow;
import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.CategoryRec;
import com.skycober.mineral.bean.TagRec;
import com.skycober.mineral.bean.UserRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseAddTag;
import com.skycober.mineral.network.ResponseGetAttentionTag;
import com.skycober.mineral.product.CategoryReviewListActivity;
import com.skycober.mineral.product.TagReviewListActivity;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 个人中心
 * 
 * @author 新彬
 * 
 */
public class InterestSettingActivity extends FragBaseActivity implements View.OnClickListener, View.OnLongClickListener{
	
	private static final String TAG = "InterestSettingActivity:";
	
	private KeywordsFlow keywordsFlow;
	private ViewGroup emptyView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_my_interest_setting, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.interest_setting_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) v
				.findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.INVISIBLE);
		
		Button btnAddCategory = (Button) v.findViewById(R.id.btnAddCategory);
		btnAddCategory.setOnClickListener(btnAddCategoryClickListener);
		Button btnAddTag = (Button) v.findViewById(R.id.btnAddTag);
		btnAddTag.setOnClickListener(btnAddTagClickListener);
		
		emptyView = (ViewGroup) v.findViewById(R.id.empty_rem);
		
		keywordsFlow = (KeywordsFlow) v.findViewById(R.id.keywordsflow);
		keywordsFlow.setDuration(800l);  
        keywordsFlow.setOnItemClickListener(this); 
        keywordsFlow.setOnItemLongClickListener(this);
        keywordsFlow.setOnItemEmptyListener(new KeywordsFlow.OnItemEmptyListener() {
			
			@Override
			public void onNotEmpty() {
				emptyView.setVisibility(View.GONE);
			}
			
			@Override
			public void onEmpty() {
				emptyView.setVisibility(View.VISIBLE);
			}
		});
        feedKeywordsFlow(keywordsFlow, tagList);
        keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
        keywordsFlow.checkEmpty();
        
        
		return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		readyToLoadTag();
		super.onCreate(savedInstanceState);
	}
	
	private List<String> tagList = new ArrayList<String>();
	private void feedKeywordsFlow(KeywordsFlow keywordsFlow, List<String> tagList) {
		if(null == tagList || tagList.size() == 0) return;
        Random random = new Random();  
        for (int i = 0; i < KeywordsFlow.MAX; i++) {  
            int ran = random.nextInt(tagList.size());  
            String tmp = tagList.get(ran);  
            keywordsFlow.feedKeyword(tmp);  
        }  
    }
	
	private void addKeywordsToFlow(KeywordsFlow keywordsFlow, Object tag){
		if(null != tag){
			keywordsFlow.feedKeyword(tag);
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			keywordsFlow.checkEmpty();
		}
	}
	
	private void addKeywordsToFlow(KeywordsFlow keywordsFlow, List<Object> tagList, boolean isNeedClear){
		if(null != tagList && tagList.size() > 0){
			if(isNeedClear) keywordsFlow.removeAllTags();
			for (Object tag : tagList) {
				keywordsFlow.feedKeyword(tag);
			}
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			keywordsFlow.checkEmpty();
		}
	}
	
	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};
	private static final int REQUEST_CODE_ADD_CATEGORY = 2021;
	private View.OnClickListener btnAddCategoryClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(getActivity(), CategorySelectorActivity.class);
			startActivityForResult(mIntent, REQUEST_CODE_ADD_CATEGORY);
		}
	};
	
	private View.OnClickListener btnAddTagClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!v.isEnabled()) return;
			v.setEnabled(false);
			showAddTagRem();
			v.setEnabled(true);
		}
	};
	
	private void showAddTagRem(){
		final Dialog dialog = new Dialog(getActivity(), R.style.dialog);
		ViewGroup root = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
				R.layout.interest_add_tag_dialog_content_view, null);
		TextView tvTitle = (TextView) root.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.add_tag_dialog_title);
		final EditText etContent = (EditText) root.findViewById(R.id.etContent);
		etContent.setHint(R.string.add_tag_input_hint);
		Button btnOk = (Button) root.findViewById(R.id.positiveButton);
		Button btnCancel = (Button) root.findViewById(R.id.negativeButton);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				String tag = etContent.getText().toString();
				if(StringUtil.getInstance().IsEmpty(tag)){
					Toast.makeText(getActivity(), R.string.add_tag_error_empty, Toast.LENGTH_SHORT).show();
				}else{
					if(!keywordsFlow.hasTag(tag)){
						readyToAddMyTag(tag);
					}else{
						Toast.makeText(getActivity(), R.string.add_tag_error_repeat, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(root);
		dialog.show();
	}
	
	@Override
	public boolean onLongClick(View v) {
		if(v instanceof TextView){
			 String keyword = ((TextView) v).getText().toString(); 
			 final Object obj = v.getTag(R.id.cloud_tag_second);
			 MyRemDialog dialog = new MyRemDialog(getActivity(), R.style.dialog);
			 dialog.setTitle(R.string.remove_tag_dialog_title);
			 String message = String.format(getString(R.string.remove_tag_message), keyword);
			 dialog.setMessage(message);
			 dialog.setPosBtnText(R.string.remove_tag_dialog_pos_btn);
			 dialog.setNegBtnText(R.string.remove_tag_dialog_neg_btn);
			 dialog.setPosBtnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 需要请求服务器删除关注标签后，再做本地UI移除标签
					AttentionType type = AttentionType.Other;
					UserRec userRec = null;CategoryRec catRec = null;TagRec tagRec = null;
					if(obj instanceof UserRec){
						type = AttentionType.People;
						userRec = (UserRec)obj;
					}else if(obj instanceof CategoryRec){
						type = AttentionType.Category;
						catRec = (CategoryRec) obj;
					}else if(obj instanceof TagRec){
						type = AttentionType.Tag;
						tagRec = (TagRec) obj;
					}
					readyToRemoveAttention(type, userRec, catRec, tagRec);
				}
			});
			 dialog.show();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v instanceof TextView){
			 Object obj = v.getTag(R.id.cloud_tag_second);
			 if(obj instanceof CategoryRec){
				 CategoryRec rec = (CategoryRec) obj;
//				 Toast.makeText(getActivity(), "CatRec:"+rec.toString(), Toast.LENGTH_SHORT).show();
				 Intent mIntent = new Intent(getActivity(), CategoryReviewListActivity.class);
				 mIntent.putExtra(CategoryReviewListActivity.KEY_CATEGORY_REC, rec);
				 getActivity().startActivity(mIntent);
			 }else if(obj instanceof TagRec){
				 TagRec rec = (TagRec) obj;
//				 Toast.makeText(getActivity(), "TagRec:"+rec.toString(), Toast.LENGTH_SHORT).show();
				 Intent mIntent = new Intent(getActivity(), TagReviewListActivity.class);
				 mIntent.putExtra(TagReviewListActivity.KEY_TAG_REC, rec);
				 getActivity().startActivity(mIntent);
			 }
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_ADD_CATEGORY:
			// 添加分类成功，重新请求关注信息，更新界面
			readyToLoadTag();
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void readyToLoadTag(){
		if(!NetworkUtil.getInstance().existNetwork(getActivity())){
			Toast.makeText(getActivity(), R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToLoadTag:";
		lockScreen(getString(R.string.attention_tag_list_get_ing));
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
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(),
							R.string.attention_tag_list_get_failed,
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
					BaseResponse br = parser.parseGetMyAttentionTags(json);
					if (null != br && ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseGetAttentionTag response = (ResponseGetAttentionTag) br;
						Trace.d(TAG, "ResponseGetAttentionTag:" + response.toString());
						List<CategoryRec> categoryRecList = response.getCategoryRecList();
						List<TagRec> tagRecList = response.getTagRecList();
						List<Object> allRecordList = new ArrayList<Object>();
						if(null != categoryRecList && categoryRecList.size() > 0){
							allRecordList.addAll(categoryRecList);
						}
						if(null != tagRecList && tagRecList.size() > 0){
							allRecordList.addAll(tagRecList);
						}
						Trace.d(TAG, "RecordList:" + allRecordList.toString());
						addKeywordsToFlow(keywordsFlow, allRecordList, true);
					} else {
						String message = getString(R.string.attention_tag_list_get_failed);
						String msg = getString(R.string.attention_tag_list_get_failed);
						if (null == br) {
							msg = "GetMyAttentionTags: Result:BaseResponse is null.";
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
									getActivity(), msg);
						} else {
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(getActivity(),msg);
					} else {
						Toast.makeText(getActivity(), R.string.attention_tag_list_get_failed, Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
 		String type = "2,3";
		String offset = String.valueOf(0);
		String count = String.valueOf(10000);
		gs.GetMyAttentionTags(getActivity(), type, offset, count, callBack);
		
	}

	private void readyToAddMyTag(final String tagName){
		if(!NetworkUtil.getInstance().existNetwork(getActivity())){
			Toast.makeText(getActivity(), R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToAddMyTag:";
		lockScreen(getString(R.string.add_my_tag_send_ing));
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
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(),
							R.string.add_my_tag_failed_rem,
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
					BaseResponse br = parser.parseAddTag(json);
					if (null != br && ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseAddTag response = (ResponseAddTag) br;
						TagRec tagRec = response.getTagRec();
						if(null != tagRec){
							addKeywordsToFlow(keywordsFlow, tagRec);
							Toast.makeText(getActivity(), R.string.add_my_tag_succeed_rem, Toast.LENGTH_SHORT).show();
						}else{
							String exception = "result TagRec is null.";
							Log.e(TAG, exception);
							if(BuildConfig.isDebug){
								ExceptionRemHelper.showExceptionReport(getActivity(), exception);
							}else{
								Toast.makeText(getActivity(), R.string.add_my_tag_failed_rem, Toast.LENGTH_SHORT).show();
							}
						}
					} else {
						String message = getString(R.string.add_my_tag_failed_rem);
						String msg = getString(R.string.add_my_tag_failed_rem);
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
									getActivity(), msg);
						} else {
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(getActivity(),msg);
					} else {
						Toast.makeText(getActivity(), R.string.add_my_tag_failed_rem, Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
 		gs.AddTag(getActivity(), tagName, callBack);
	}
	
	private void readyToRemoveAttention(final AttentionType type, final UserRec userRec, final CategoryRec catRec, final TagRec tagRec ){
		if(!NetworkUtil.getInstance().existNetwork(getActivity())){
			Toast.makeText(getActivity(), R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToRemoveAttention:";
		lockScreen(getString(R.string.remove_tag_sending));
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
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(),
							R.string.remove_tag_failed_rem,
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
					BaseResponse br = parser.parseRemoveTag(json);
					if (null != br && ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						boolean isSucRemove = false;
						if(type == AttentionType.People){
							isSucRemove = keywordsFlow.removeTag(userRec);
						}else if(type == AttentionType.Category){
							isSucRemove = keywordsFlow.removeTag(catRec);
						}else if(type == AttentionType.Tag){
							isSucRemove = keywordsFlow.removeTag(tagRec);
						}
						if(isSucRemove){
							Toast.makeText(getActivity(), R.string.remove_tag_success, Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(getActivity(), R.string.remove_tag_fail, Toast.LENGTH_SHORT).show();
						}
					} else {
						String message = getString(R.string.remove_tag_failed_rem);
						String msg = getString(R.string.remove_tag_failed_rem);
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
									getActivity(), msg);
						} else {
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(getActivity(),msg);
					} else {
						Toast.makeText(getActivity(), R.string.remove_tag_failed_rem, Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String typeId = "";
		String followId = "";
		if(type == AttentionType.People){
			typeId = String.valueOf(1);
			followId = userRec.getUserId();
		}else if(type == AttentionType.Category){
			typeId = String.valueOf(2);
			followId = catRec.getId();
		}else if(type == AttentionType.Tag){
			typeId = String.valueOf(3);
			followId = tagRec.getId();
		}
 		gs.RemoveTag(getActivity(), typeId, followId, callBack);
	}
	
	public enum AttentionType{
		People,
		Category,
		Tag,
		Other
	}
}
