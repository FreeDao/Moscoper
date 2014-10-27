package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;


import android.app.Activity;
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

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;

import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseGetUserGoodsList;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.MyListView;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.Trace;

import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.MyListView.OnAddMoreListener;
import com.skycober.mineral.util.MyListView.OnRefreshListener;

/**
 * 搜索页面
 * 
 * @author 新彬
 * 
 */
public class SearchActivity extends FragBaseActivity {
	private static final String TAG = "SearchActivity:";


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_search, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.search_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) v
				.findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.INVISIBLE);
		
		tvEmpty = (TextView) v.findViewById(R.id.empty_view);
		
		lvSearch = (MyListView) v.findViewById(R.id.search_list);
		prodList = new ArrayList<ProductRec>();
		searchAdapter = new ProductAdapter(getActivity(), R.layout.category_product_listview_item, prodList);
		searchAdapter.setOnItemClickListener(onSearchItemClickListener);
		searchAdapter.setOnItemEmptyListener(onSearchResultEmptyListener);
		lvSearch.setAdapter(searchAdapter);
		lvSearch.setonRefreshListener(new OnRefreshListener() {
			@Override
			protected Object clone() throws CloneNotSupportedException {
				
				return super.clone();
			}
			@Override
			public void onRefresh() {
				readyToSearch(true);
			}
		});
		lvSearch.setAddMoreListener( new OnAddMoreListener() {
			
			@Override
			public void onAddMore() {
				readyToSearch(false);
			}
		});

		Button btnSearch = (Button) v.findViewById(R.id.do_search);
		btnSearch.setOnClickListener(btnSearchClickListener);
		Button btnClear = (Button) v.findViewById(R.id.clear_search_result);
		btnClear.setOnClickListener(btnClearClickListener);
		etSearch = (EditText) v.findViewById(R.id.search_keyword);
		
		return v;
	}

	private MyListView lvSearch;
	private ProductAdapter searchAdapter;
	private List<ProductRec> prodList;
	private TextView tvEmpty;
	private EditText etSearch;

	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};
	
	private View.OnClickListener btnSearchClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!v.isEnabled()) return;
			v.setEnabled(false);
			readyToSearch(true);
			v.setEnabled(true);
		}
	};
	
	private View.OnClickListener btnClearClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!v.isEnabled()) return;
			v.setEnabled(false);
			if(null != etSearch){
				etSearch.setText("");
			}
			v.setEnabled(true);
		}
	};
	
	private ProductAdapter.OnItemClickListener onSearchItemClickListener = new ProductAdapter.OnItemClickListener() {

		@Override
		public void onItemClick(ProductRec rec, int pos) {
			Intent mIntent = new Intent(getActivity(),
					ProductDetailActivity.class);
			mIntent.putExtra(ProductDetailActivity.KEY_PRODUCT_REC,
					rec);
			mIntent.putExtra(ProductDetailActivity.KET_REC_NUMBER,
					pos);
			startActivityForResult(mIntent,
					REQUEST_CODE_SHOW_PRODUCT);
		}

	};
	
	private ProductAdapter.OnItemEmptyListener onSearchResultEmptyListener = new ProductAdapter.OnItemEmptyListener() {
		
		@Override
		public void onItemNotEmpty() {
			tvEmpty.setVisibility(View.GONE);
			lvSearch.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onItemEmpty() {
			lvSearch.setVisibility(View.GONE);
			tvEmpty.setVisibility(View.VISIBLE);
		}
	};

	private String searchKeywords = null;
	protected void readyToSearch(final boolean isFirst) {
		String keywords = etSearch.getText().toString();
		if(StringUtil.getInstance().IsEmpty(keywords)){
			Toast.makeText(getActivity(), R.string.input_keyword_not_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if(!NetworkUtil.getInstance().existNetwork(getActivity())){
			Toast.makeText(getActivity(), R.string.network_disable_error, Toast.LENGTH_SHORT).show();
			return;
		}
		if(isFirst){
			searchAdapter.offset = 0;
			searchAdapter.count = 20;
			searchAdapter.setProdList(new ArrayList<ProductRec>());
		}else{
			keywords = searchKeywords;
		}
		if(null == keywords) keywords = "";
		if(isFirst){
			lockScreen(getString(R.string.search_send_ing));
		}
		final String currMethod = "readyToSearch:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Trace.d(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseSearchProduct(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseGetUserGoodsList response = (ResponseGetUserGoodsList) br;
						List<ProductRec> list = response.getProductRecs();
						if (isFirst) {
							prodList.clear();
							prodList.addAll(list);
							searchAdapter.offset += list.size();
							Boolean ishas = (list.size() < 20) ? false : true;
							lvSearch.onScrollComplete(ishas);
							lvSearch.onRefreshComplete();
							searchAdapter.notifyDataSetChanged();
						} else {
							prodList.addAll(list);
							searchAdapter.offset += prodList.size();
							Boolean ishas = (list.size() < 20) ? false : true;
							lvSearch.onScrollComplete(ishas);
							lvSearch.onRefreshComplete();
							searchAdapter.notifyDataSetChanged();
						}
					} else {
						String message = getString(R.string.search_failed_rem);
						String msg = getString(R.string.search_failed_rem);
						if (null == br) {
							msg = "readyToSearch: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();
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
						ExceptionRemHelper.showExceptionReport(getActivity(),
								msg);
					} else {
						Toast.makeText(getActivity(),
								R.string.search_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
			
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->"+strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = currMethod + "onFailure,Msg->"+strMsg+",Exception->"+t.toString();
					ExceptionRemHelper.showExceptionReport(getActivity(),
							msg);
				} else {
					Toast.makeText(getActivity(),
							R.string.search_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}
		};
		String offset = String.valueOf(searchAdapter.offset);
		String count = String.valueOf(searchAdapter.count);
		gs.SearchProduct(getActivity(), keywords, offset, count, callBack);
	}
	
	private static final int REQUEST_CODE_SHOW_PRODUCT = 0x10002;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_SHOW_PRODUCT:
			Trace.e(TAG, "Show Product:result data->"+(null == data ? "null" : data.toString()));
			if (data != null && data.hasExtra(ProductDetailActivity.KEY_PRODUCT_REC)
					&& data.hasExtra(ProductDetailActivity.KEY_POST_NUMBER)
					&& data.hasExtra(ProductDetailActivity.KEY_EDIT_TYPE)) {
				ProductRec productRec = (ProductRec) data
						.getSerializableExtra(ProductDetailActivity.KEY_PRODUCT_REC);
				int pos = (Integer) data
						.getSerializableExtra(ProductDetailActivity.KEY_POST_NUMBER);
				int editType = data.getIntExtra(ProductDetailActivity.KEY_EDIT_TYPE, ProductDetailActivity.TYPE_EDIT_PRODUCT);
				//移除记录满足以下条件即可：
				//（1）藏品做了移除操作；
				//（2）藏品做了下架操作；
				//（3）藏品做了重新设置了分类操作；
				boolean isNeedRemoveProduct = false;
				if(editType == ProductDetailActivity.TYPE_REMOVE_PRODUCT){
					isNeedRemoveProduct = true;
				}else{
					if(null != productRec){
//						isNeedRemoveProduct = !productRec.isOnSale();
					}
				}
				if(isNeedRemoveProduct){
					try {
						prodList.remove(pos);
					} catch (Exception e) {
						Log.e(TAG, "Remove ProductRec failure: pos outOfArrIndex Exception.", e);
					}
				}else{
//					prodList.get(pos).setInFav(productRec.getInFav());
//					prodList.get(pos).setFavNum(productRec.getFavNum());
				}
				searchAdapter.notifyDataSetInvalidated();
			}
			break;
		
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
