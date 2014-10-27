package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseAllGoodsByCatID;
import com.skycober.mineral.network.ResponseFavPost;
import com.skycober.mineral.product.ProductAdapter.OnItemClickListener;
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
 * 分类浏览
 * 
 * @author 新彬
 * 
 */
public class CategoryReviewActivity extends FragBaseActivity {
	private static final String TAG = "CategoryReviewActivity";
	private List<ProductRec> productList = new ArrayList<ProductRec>();
	private ProductAdapter productAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FragmentChangeActivity.slideMenu.setTag("back");
		View v = inflater.inflate(R.layout.activity_category_review_list, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.category_review_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) v
				.findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.INVISIBLE);

		lvProduct = (MyListView) v.findViewById(R.id.lv_product);
		init();
		return v;
	}
	private static final int REQUEST_CODE_SHOW_PRODUCT = 110;

	private void init() {

		readyToGetAllGoodsByCatID(true);
		lvProduct.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				
				readyToGetAllGoodsByCatID(true);
			}
		});
		lvProduct.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				
				readyToGetAllGoodsByCatID(false);
			}
		});

		productAdapter = new ProductAdapter(this.getActivity(),
				R.layout.category_product_listview_item, productList);
		lvProduct.setAdapter(productAdapter);
		productAdapter.setReportClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(ProductRec rec, int pos) {
				// TODO Auto-generated method stub
				readyToreport(rec.getId());
			}
		});
		productAdapter
				.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
					@Override
					public void onItemClick(ProductRec rec, int pos) {

						Intent mIntent = new Intent(
								CategoryReviewActivity.this.getActivity(),
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
	private void chanceMyFvaDialog(final ProductRec rec, final int pos) {
		final MyRemDialog chanceMyFvaDialog = new MyRemDialog(
				CategoryReviewActivity.this.getActivity(), R.style.dialog);

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
	private MyListView lvProduct;

	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};

	private int offset = 0;
	private int count = 20;

	private void readyToGetAllGoodsByCatID(final Boolean isfirst) {

		if (!NetworkUtil.getInstance().existNetwork(this.getActivity())) {
			Toast.makeText(this.getActivity(), R.string.network_disable_error,
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
							CategoryReviewActivity.this.getActivity(), msg);
				} else {
					Toast.makeText(CategoryReviewActivity.this.getActivity(),
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
								Trace.d(TAG,
										currMethod + "result response list:"
												+ list.toString());
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
									CategoryReviewActivity.this.getActivity(),
									msg);
						} else {
							Toast.makeText(
									CategoryReviewActivity.this.getActivity(),
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategoryReviewActivity.this.getActivity(), msg);
					} else {
						Toast.makeText(
								CategoryReviewActivity.this.getActivity(),
								R.string.get_category_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

		};
		String moffset = String.valueOf(offset);
		String mcount = String.valueOf(count);
		gs.GetAllGoodsByCatID(this.getActivity(), moffset, mcount, callBack);
	}

	private void refreshList() {
		productAdapter.notifyDataSetChanged();
		lvProduct.onRefreshComplete();
	}

	private void refreshListForFav() {
		productAdapter.notifyDataSetChanged();
	}

	public static final int FOR_LOGIN_RESULT = 1212;

	// 添加收藏
	private void readyToAddFav(final String Id, final int Pos) {
		if (!NetworkUtil.getInstance().existNetwork(this.getActivity())) {
			Toast.makeText(this.getActivity(), R.string.network_disable_error,
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
							 productList.get(Pos).setCollectUserNum((productList.get(Pos).getCollectUserNum()+1));
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
										CategoryReviewActivity.this
												.getActivity(),
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
									CategoryReviewActivity.this.getActivity(),
									msg);
						} else {
							Toast.makeText(
									CategoryReviewActivity.this.getActivity(),
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategoryReviewActivity.this.getActivity(), msg);
					} else {
						Toast.makeText(
								CategoryReviewActivity.this.getActivity(),
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
							CategoryReviewActivity.this.getActivity(), msg);
				} else {
					Toast.makeText(CategoryReviewActivity.this.getActivity(),
							R.string.add_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
				super.onFailure(t, strMsg);
			}

		};
		gs.ToAddFav(this.getActivity(), Id, callBack);
	}

	// 取消收藏
	private void readyToChanceFav(final String Id, final int Pos) {
		if (!NetworkUtil.getInstance().existNetwork(this.getActivity())) {
			Toast.makeText(this.getActivity(), R.string.network_disable_error,
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
							 productList.get(Pos).setCollectUserNum((productList.get(Pos).getCollectUserNum()-1));
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
									CategoryReviewActivity.this.getActivity(),
									msg);
						} else {
							Toast.makeText(
									CategoryReviewActivity.this.getActivity(),
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategoryReviewActivity.this.getActivity(), msg);
					} else {
						Toast.makeText(
								CategoryReviewActivity.this.getActivity(),
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
							CategoryReviewActivity.this.getActivity(), msg);
				} else {
					Toast.makeText(CategoryReviewActivity.this.getActivity(),
							R.string.chance_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
				super.onFailure(t, strMsg);
			}

		};
		gs.ToCancelFav(this.getActivity(), Id, callBack);
	}
	private void readyToreport(String Id) {
		if (!NetworkUtil.getInstance().existNetwork(this.getActivity())) {
			Toast.makeText(this.getActivity(), R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen("正在举报信息");
		final String currMethod = "readyToreport:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {

				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseReport(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						Toast.makeText(getActivity(), "举报成功，我们将会在24小时内进行处理，谢谢您的支持", Toast.LENGTH_LONG).show();
						
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
									CategoryReviewActivity.this.getActivity(),
									msg);
						} else {
							Toast.makeText(
									CategoryReviewActivity.this.getActivity(),
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								CategoryReviewActivity.this.getActivity(), msg);
					} else {
						Toast.makeText(
								CategoryReviewActivity.this.getActivity(),
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
							CategoryReviewActivity.this.getActivity(), msg);
				} else {
					Toast.makeText(CategoryReviewActivity.this.getActivity(),
							R.string.chance_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

		};
		gs.ToReport(this.getActivity(), Id, callBack);
	}
}
