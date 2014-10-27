package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.CategoryRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseAllGoodsByCatID;
import com.skycober.mineral.network.ResponseFavPost;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.MyListView;
import com.skycober.mineral.util.MyListView.OnAddMoreListener;
import com.skycober.mineral.util.MyListView.OnRefreshListener;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 展示分类浏览查询结果
 * 
 * @author 新彬
 * 
 */
public class CategoryReviewListActivity extends BaseActivity {
	private static final String TAG = "CategoryReviewListActivity";

	/**
	 * 传递值为CategoryRec类型，标题为其属性Name
	 */
	public static final String KEY_CATEGORY_REC = "key_category_rec";
	private CategoryRec categoryRec;
	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;
	@ViewInject(id = R.id.lv_product)
	MyListView lvProduct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_review_list);
		Init();
		InitTopBar();
		InitData();
	}

	private void chanceMyFvaDialog(final ProductRec rec, final int pos) {
		final MyRemDialog chanceMyFvaDialog = new MyRemDialog(
				CategoryReviewListActivity.this, R.style.dialog);

		chanceMyFvaDialog.setHeaderVisility(View.GONE);
		chanceMyFvaDialog.setMessage(R.string.chance_rem);
		chanceMyFvaDialog.setPosBtnText(R.string.chance_btn_pos);
		chanceMyFvaDialog.setNegBtnText(R.string.chance_btn_neg);
		chanceMyFvaDialog.show();
		chanceMyFvaDialog.setPosBtnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				readyToChanceFav(rec.getId(), pos);
				chanceMyFvaDialog.dismiss();
			}
		});
		chanceMyFvaDialog.setCancelClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chanceMyFvaDialog.dismiss();
			}
		});
	}

	private ProductAdapter productAdapter;
	private List<ProductRec> productList = new ArrayList<ProductRec>();

	private void InitData() {

		readyToGetAllGoodsByCatID(categoryRec.getId(), true);
		lvProduct.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				
				readyToGetAllGoodsByCatID(categoryRec.getId(), true);
			}
		});
		lvProduct.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				
				readyToGetAllGoodsByCatID(categoryRec.getId(), false);
			}
		});

		productAdapter = new ProductAdapter(this,
				R.layout.category_product_listview_item, productList);
		lvProduct.setAdapter(productAdapter);
		productAdapter
				.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
					@Override
					public void onItemClick(ProductRec rec, int pos) {

						Intent mIntent = new Intent(
								CategoryReviewListActivity.this,
								ProductDetailActivity.class);
						mIntent.putExtra(ProductDetailActivity.KEY_PRODUCT_REC,
								rec);
						mIntent.putExtra(ProductDetailActivity.KET_REC_NUMBER,
								pos);
						startActivityForResult(mIntent,
								REQUEST_CODE_SHOW_PRODUCT);
					}
				});
		productAdapter
				.setAddFavClickListener(new ProductAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(ProductRec rec, int pos) {
						
						readyToAddFav(rec.getId(), pos);
					}
				});
		productAdapter
				.setSubFavClickListener(new ProductAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(ProductRec rec, int pos) {
						
						chanceMyFvaDialog(rec, pos);
					}
				});
	}

	private static final int REQUEST_CODE_SHOW_PRODUCT = 110;

	private void Init() {
		Intent mIntent = getIntent();
		if (null != mIntent && mIntent.hasExtra(KEY_CATEGORY_REC)) {
			categoryRec = (CategoryRec) mIntent
					.getSerializableExtra(KEY_CATEGORY_REC);
		}
	}

	private void InitTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);
		btnRight.setVisibility(View.INVISIBLE);
		if (null != categoryRec) {
			tvTitle.setText(categoryRec.getName());
		}
	}

	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
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
				}
				if(isNeedRemoveProduct){
					try {
						productList.remove(pos);
					} catch (Exception e) {
						Log.e(TAG, "Remove ProductRec failure: pos outOfArrIndex Exception.", e);
					}
				}
				productAdapter.notifyDataSetInvalidated();
			}
			break;
		case FOR_LOGIN_RESULT:
			readyToGetAllGoodsByCatID(categoryRec.getId(), true);
			break;
		default:
			break;
		}
	};

	private int offset = 0;
	private int count = 20;

	private void readyToGetAllGoodsByCatID(final String Id,
			final Boolean isfirst) {

		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (isfirst) {
			count = 20;
			offset = 0;
		}
		lockScreen(this.getResources().getString(R.string.get_category_ing));
		final String currMethod = "readyToGetAllGoodsByCatID:";
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
							CategoryReviewListActivity.this, msg);
				} else {
					Toast.makeText(CategoryReviewListActivity.this,
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
					BaseResponse br = parser.parseGetAllGoodsByCatID(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseAllGoodsByCatID response = (ResponseAllGoodsByCatID) br;
						List<ProductRec> list = response.getProductRecs();
						if (list != null && list.size() > 0) {
							if (!isfirst) {
								Trace.d(TAG, currMethod +"result response list:"+list.toString());
								productList.addAll(list);
								offset += productList.size();
								Boolean ishas = (list.size() < 20) ? false
										: true;
								lvProduct.onScrollComplete(ishas);
								refreshList();
							} else {
								productList.clear();
								productList.addAll(list);
								offset += list.size();
								Boolean ishas = (list.size() < 20) ? false
										: true;
								lvProduct.onScrollComplete(ishas);
								refreshList();

							}
						}
					} else {

						String message = getString(R.string.get_category_failed_rem);
						String msg = getString(R.string.get_category_failed_rem);
						if (null == br) {
							msg = "parseGetAllGoodsByCatID: Result:BaseResponse is null.";
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
									CategoryReviewListActivity.this, msg);
						} else {
							Toast.makeText(CategoryReviewListActivity.this,
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategoryReviewListActivity.this, msg);
					} else {
						Toast.makeText(CategoryReviewListActivity.this,
								R.string.get_category_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

		};
		String moffset = String.valueOf(offset);
		String mcount = String.valueOf(count);
//		gs.GetAllGoodsByCatID(this, Id, moffset, mcount, callBack);
	}

	private void refreshList() {
		productAdapter.notifyDataSetChanged();
		lvProduct.onRefreshComplete();
	}

	private void refreshListForFav() {
		productAdapter.notifyDataSetChanged();
//		readyToGetAllGoodsByCatID(categoryRec.getId(), true);
	}

	public static final int FOR_LOGIN_RESULT = 1212;

	// 添加收藏
	private void readyToAddFav(final String Id, final int Pos) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources().getString(R.string.add_fav_send_ing));
		final String currMethod = "readyToAddFav:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					Log.e(TAG, "do");
					BaseResponse br = parser.parseAddFav(json);
					Log.e(TAG, "do");
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseFavPost response = (ResponseFavPost) br;
						String id = response.getId();
						if (Id.equals(id)) {
							productList.get(Pos).setInFav(true);
//							productList.get(Pos).setFavNum(productList.get(Pos).getFavNum()+1);
						}
						refreshListForFav();
					} else {
						String message = getString(R.string.add_fav_failed_rem);
						String msg = getString(R.string.add_fav_failed_rem);
						if (null == br) {
							msg = "parseAddFav: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							case 7:
								Intent intent = new Intent(
										CategoryReviewListActivity.this,
										LoginActivity.class);
								startActivityForResult(intent, FOR_LOGIN_RESULT);
								break;

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
									CategoryReviewListActivity.this, msg);
						} else {
							Toast.makeText(CategoryReviewListActivity.this,
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategoryReviewListActivity.this, msg);
					} else {
						Toast.makeText(CategoryReviewListActivity.this,
								R.string.add_fav_failed_rem, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t);
			}

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
							CategoryReviewListActivity.this, msg);
				} else {
					Toast.makeText(CategoryReviewListActivity.this,
							R.string.add_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
				super.onFailure(t, strMsg);
			}

		};
		gs.ToAddFav(this, Id, callBack);
	}

	// 取消收藏
	private void readyToChanceFav(final String Id, final int Pos) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources().getString(R.string.chance_fav_send_ing));
		final String currMethod = "readyToChanceFav:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					Log.e(TAG, "do");
					BaseResponse br = parser.parseAddFav(json);
					Log.e(TAG, "do");
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseFavPost response = (ResponseFavPost) br;
						String id = response.getId();
						if (Id.equals(id)) {
							productList.get(Pos).setInFav(false);
//							productList.get(Pos).setFavNum(productList.get(Pos).getFavNum()-1);
						}
						refreshListForFav();
					} else {
						String message = getString(R.string.chance_fav_failed_rem);
						String msg = getString(R.string.chance_fav_failed_rem);
						if (null == br) {
							msg = "parseChanceFav: Result:BaseResponse is null.";
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
									CategoryReviewListActivity.this, msg);
						} else {
							Toast.makeText(CategoryReviewListActivity.this,
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategoryReviewListActivity.this, msg);
					} else {
						Toast.makeText(CategoryReviewListActivity.this,
								R.string.chance_fav_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

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
							CategoryReviewListActivity.this, msg);
				} else {
					Toast.makeText(CategoryReviewListActivity.this,
							R.string.chance_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
				super.onFailure(t, strMsg);
			}

		};
		gs.ToCancelFav(this, Id, callBack);
	}
}
