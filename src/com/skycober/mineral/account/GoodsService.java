package com.skycober.mineral.account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.util.Util;

/**
 * 所有信息的http请求
 * 
 * @author Yes366
 * 
 */
public class GoodsService {
	private static final String TAG = "GoodsService";

	/**
	 * 获取子分类物品的列表
	 */
	private static final String GetAllGoodsByCatIDParamOffset = "[offset]";
	private static final String GetAllGoodsByCatIDParamCount = "[count]";

	public void GetAllGoodsByCatID(Context context, String offset,
			String count, AjaxCallBack<Object> callBack) {
		final String currMethod = "GetAllGoodsByCatID:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_ALL_GOODS_BY_ID;
		url = url.replace(GetAllGoodsByCatIDParamOffset, offset);
		url = url.replace(GetAllGoodsByCatIDParamCount, count);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	/**
	 * 添加收藏
	 * 
	 */
	private static final String ToAddFavCatIDParamID = "[goods_id]";

	/**
	 * 增加点击数
	 * 
	 * @param context
	 * @param Id
	 * @param callBack
	 */
	public void ToAddView(Context context, String Id,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "ToAddFav:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ADD_VIEW_URL;
		url = url.replace(ToAddFavCatIDParamID, Id);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	public void ToAddFav(Context context, String Id,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "ToAddFav:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ADD_FAV_URL;
		url = url.replace(ToAddFavCatIDParamID, Id);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	/**
	 * 取消收藏
	 */
	private static final String ToAddCancelCatIDParamID = "[goods_id]";

	public void ToCancelFav(Context context, String Id,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "ToCancelFav:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.CANCEL_FAV_URL;
		url = url.replace(ToAddCancelCatIDParamID, Id);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String GetMyFavsListParamOffset = "[offset]";
	private static final String GetMyFavsListParamCount = "[count]";

	/**
	 * 获取我收藏的物品列表
	 * 
	 * @param context
	 * @param offset
	 * @param count
	 * @param callBack
	 */
	public void GetMyFavsList(Context context, String offset, String count,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "GetMyFavsList:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_MY_FAV_LIST_URL;
		url = url.replace(GetMyFavsListParamOffset, offset);
		url = url.replace(GetMyFavsListParamCount, count);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String GetUsersGoodsListParamOffset = "[offset]";
	private static final String GetUsersGoodsListParamCount = "[count]";

	public void GetUsersGoodsList(Context context, String Id, String offset,
			String count, String isOnsale, AjaxCallBack<Object> callBack) {
		final String currMthod = "GetUsersGoodsList";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_USERS_GOODS_LIST_URL;
		url = url.replace(GetUsersGoodsListParamCount, count);
		url = url.replace(GetUsersGoodsListParamOffset, offset);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e(TAG, currMthod + "url->" + url);
		fh.get(url, callBack);
	}

	/**
	 * 摇一摇
	 * 
	 * @param context
	 * @param callBack
	 */
	public void GetRandomGoods(Context context, AjaxCallBack<Object> callBack) {
		final String currMthod = "GetUsersGoodsList";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_RANDOM_GOODS_URL;
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMthod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String MakeOnSaleParamOffsetGoodId = "[goods_id]";

	/**
	 * 产品上架
	 * 
	 * @param context
	 * @param goodsId
	 * @param callBack
	 */
	public void MakeOnSale(Context context, String goodsId,
			AjaxCallBack<Object> callBack) {
		final String currMthod = "GetUsersGoodsList";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.MAKE_ON_SALE;
		url = url.replace(MakeOnSaleParamOffsetGoodId, goodsId);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMthod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String MakeOffSaleParamOffsetGoodId = "[goods_id]";

	/**
	 * 产品下架
	 * 
	 * @param context
	 * @param goodsId
	 * @param callBack
	 */
	public void MakeOffSale(Context context, String goodsId,
			AjaxCallBack<Object> callBack) {
		final String currMthod = "GetUsersGoodsList";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.MAKE_OFF_SALE;
		url = url.replace(MakeOffSaleParamOffsetGoodId, goodsId);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMthod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String AddProductParamTagCatId = "tag_cat_id";
	private static final String AddProductParamTagCatName = "tag_cat_name";
	private static final String AddProductParamTagId = "tag_ids";
	private static final String AddProductParamTagName = "tag_names";
	private static final String AddProductParamDescription = "goods_desc";
	private static final String AddProductParamName = "goods_name";
	private static final String AddProductParamLogo = "goods_img";
	private static final String AddProductParamGalleryIds = "gallery_ids";
	private static final String AddProductParamCityId = "city_id";

	/**
	 * 发布藏品
	 * 
	 * @param context
	 * @param categoryId
	 * @param description
	 * @param name
	 * @param logoFile
	 * @param keywords
	 * @param galleryIds
	 * @param callBack
	 */
	public void AddProduct(Context context, ProductRec productRec,
			File logoFile, String keywordsID, String keywordsName,
			String galleryIds, String cityId, AjaxCallBack<Object> callBack) {
		final String currMthod = "AddProduct:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ADD_PRODUCT_URL;
		AjaxParams params = new AjaxParams();
		if (!StringUtil.getInstance().IsEmpty(productRec.getTagCatId())) {
			params.put(AddProductParamTagCatId, productRec.getTagCatId());
		}
		if (!StringUtil.getInstance().IsEmpty(productRec.getTagCatName())) {
			params.put(AddProductParamTagCatName, productRec.getTagCatName());
		}
		if (!StringUtil.getInstance().IsEmpty(keywordsID)) {
			params.put(AddProductParamTagId, keywordsID);
		}
		if (!StringUtil.getInstance().IsEmpty(keywordsName)) {
			params.put(AddProductParamTagName, keywordsName);
		}
		if (!StringUtil.getInstance().IsEmpty(productRec.getDescription())) {
			params.put(AddProductParamDescription, productRec.getDescription());
		}
		if (!StringUtil.getInstance().IsEmpty(productRec.getName())) {
			params.put(AddProductParamName, productRec.getName());
		}
		if (!StringUtil.getInstance().IsEmpty(cityId)) {
			params.put(AddProductParamCityId, cityId);
		}
		try {
			if (null != logoFile && logoFile.exists()) {
				FileInputStream stream = new FileInputStream(logoFile);
				String logoName = logoFile.getName();
				int start = logoName.lastIndexOf(".") + 1;
				String endFix = logoName.substring(start, logoName.length());
				String mimeType = Util.getMimeTypeByFile(endFix);
				Trace.e(TAG, currMthod + "name->" + logoName + ",endFix"
						+ endFix + ",MimeType->" + mimeType);
				params.put(AddProductParamLogo, stream, logoName, mimeType);
			}
		} catch (Exception e) {
			Log.e(TAG, currMthod + "add logo fail error.", e);
		}
		if (!StringUtil.getInstance().IsEmpty(galleryIds)) {
			params.put(AddProductParamGalleryIds, galleryIds);
		}
		Trace.d(TAG, "url->" + url + ",params->" + params.toString());
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(url, params, callBack);
	}

	private static final String UploadProdPicParamUserFile = "userfile";
	private static final String UploadProdPicParamImgDesc = "img_desc";
	private static final String UploadProdPicParamGoodsId = "{goods_id}";

	/**
	 * 上传藏品图片
	 * 
	 * @param context
	 * @param picFile
	 * @param imgDesc
	 * @param callBack
	 */
	public void UploadProdPic(Context context, String prodId, File picFile,
			String imgDesc, AjaxCallBack<Object> callBack) {
		final String currMethod = "UploadProdPic:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.UPLOAD_PRODUCT_PIC_URL;
		url = url.replace(UploadProdPicParamGoodsId, prodId);
		AjaxParams params = new AjaxParams();
		if (!StringUtil.getInstance().IsEmpty(imgDesc)) {
			params.put(UploadProdPicParamImgDesc, imgDesc);
		}
		try {
			InputStream stream = new FileInputStream(picFile);
			String name = picFile.getName();
			int start = name.lastIndexOf(".") + 1;
			String endFix = name.substring(start, name.length());
			params.put(UploadProdPicParamUserFile, stream, name,
					Util.getMimeTypeByFile(endFix));
		} catch (FileNotFoundException e) {
			if (null != callBack) {
				callBack.onFailure(e, "avatar file not found.FileName:"
						+ picFile.getName());
			}
			Log.e(TAG, currMethod + "file not found.", e);
		}
		Trace.d(TAG, "url->" + url + ",params->" + params.toString());
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(url, params, callBack);
	}

	private static final String RemoveProductParamProdId = "[goods_id]";

	/**
	 * 移除藏品
	 * 
	 * @param context
	 * @param prodId
	 * @param callBack
	 */
	public void RemoveProduct(Context context, String prodId,
			AjaxCallBack<Object> callBack) {
		final String currMthod = "RemoveProduct:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.REMOVE_PRODUCT_URL;
		url = url.replace(RemoveProductParamProdId, prodId);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMthod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String GetCommentListParamProdId = "[goods_id]";
	private static final String GetCommentListParamOffset = "[offset]";
	private static final String GetCommentListParamCount = "[count]";

	/**
	 * 获取评论
	 * 
	 * @param context
	 * @param prodId
	 * @param offset
	 * @param count
	 * @param callBack
	 */
	public void GetCommentList(Context context, String prodId, String offset,
			String count, AjaxCallBack<Object> callBack) {
		final String currMethod = "GetCommentList:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_COMMENT_LIST_URL;
		url = url.replace(GetCommentListParamProdId, prodId);
		url = url.replace(GetCommentListParamOffset, offset);
		url = url.replace(GetCommentListParamCount, count);
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String AddCommentParamProdId = "[goods_id]";
	private static final String AddCommentParamContent = "content";
	private static final String AddCommentParamRank = "rank";

	/**
	 * 发表评论
	 * 
	 * @param context
	 * @param prodId
	 * @param content
	 * @param rank
	 * @param callBack
	 */
	public void AddComment(Context context, String prodId, String content,
			int rank, AjaxCallBack<Object> callBack) {
		final String currMethod = "AddProduct:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ADD_COMMENT_URL;
		if (null == prodId)
			prodId = "";
		url = url.replace(AddCommentParamProdId, prodId);
		AjaxParams params = new AjaxParams();
		if (!StringUtil.getInstance().IsEmpty(content)) {
			params.put(AddCommentParamContent, content);
		}
		if (rank > 0 && rank < 6) {
			params.put(AddCommentParamRank, String.valueOf(rank));
		}
		Trace.d(TAG,
				currMethod + "url->" + url + ",params->" + params.toString());
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(url, params, callBack);
	}

	private static final String GetSingleProductInfoParamProdId = "[goods_id]";

	/**
	 * 获取单个藏品信息
	 * 
	 * @param context
	 * @param prodId
	 * @param callBack
	 */
	public void GetSingleProductInfo(Context context, String prodId,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "GetSingleProductInfo:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_SINGLE_PRODUCT_URL;
		url = url.replace(GetSingleProductInfoParamProdId, prodId);
		Trace.e(TAG, currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}

	private static final String UpdateProductParamProdId = "goods_id";
	private static final String UpdateProductParamTagCatId = "tag_cat_id";
	private static final String UpdateProductParamTagCatName = "tag_cat_name";
	private static final String UpdateProductParamTagId = "tag_ids";
	private static final String UpdateProductParamTagName = "tag_names";
	private static final String UpdateProductParamDescription = "goods_desc";
	private static final String UpdateProductParamName = "goods_name";
	private static final String UpdateProductParamLogo = "goods_img";
	private static final String UpdateProductParamGalleryIds = "gallery_ids";

	/**
	 * 编辑藏品
	 * 
	 * @param context
	 * @param categoryId
	 * @param description
	 * @param name
	 * @param logoFile
	 * @param sellPrice
	 * @param weight
	 * @param keywords
	 * @param brief
	 * @param galleryIds
	 * @param callBack
	 */
	public void UpdateProduct(Context context, ProductRec productRec,
			File logoFile, String keywordsID, String keywordsName,
			String galleryIds, AjaxCallBack<Object> callBack) {
		final String currMthod = "AddProduct:";
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(15*1000);
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.UPDATE_RESOURCE;
		AjaxParams params = new AjaxParams();
		if (!StringUtil.getInstance().IsEmpty(productRec.getId())) {
			params.put(UpdateProductParamProdId, productRec.getId());
		}
		String tagId = productRec.getTagCatId();

		if (!StringUtil.getInstance().IsEmpty(tagId)) {
			params.put(UpdateProductParamTagCatId, tagId);
		}
		String tagName = productRec.getTagCatName();
		Log.e("wangxu", "getTagCatName=" + tagName);
		if (!StringUtil.getInstance().IsEmpty(productRec.getTagCatName())
				&& StringUtil.getInstance().IsEmpty(productRec.getTagCatId())) {
			params.put(UpdateProductParamTagCatName, tagName);
		}
		if (!StringUtil.getInstance().IsEmpty(keywordsID)) {
			params.put(UpdateProductParamTagId, keywordsID);
		}
		if (!StringUtil.getInstance().IsEmpty(keywordsName)) {
			params.put(UpdateProductParamTagName, keywordsName);
		}
		if (!StringUtil.getInstance().IsEmpty(productRec.getDescription())) {
			params.put(UpdateProductParamDescription,
					productRec.getDescription());
		}
		if (!StringUtil.getInstance().IsEmpty(productRec.getName())) {
			Log.e("wangxu", productRec.getName());
			params.put(UpdateProductParamName, productRec.getName());
		}
		try {
			if (null != logoFile && logoFile.exists()) {
				FileInputStream stream = new FileInputStream(logoFile);
				String logoName = logoFile.getName();
				int start = logoName.lastIndexOf(".") + 1;
				String endFix = logoName.substring(start, logoName.length());
				String mimeType = Util.getMimeTypeByFile(endFix);
				Trace.e(TAG, currMthod + "name->" + logoName + ",endFix"
						+ endFix + ",MimeType->" + mimeType);
				params.put(UpdateProductParamLogo, stream, logoName, mimeType);
			}
		} catch (Exception e) {
			Log.e(TAG, currMthod + "add logo fail error.", e);
		}
		if (!StringUtil.getInstance().IsEmpty(galleryIds)) {
			params.put(UpdateProductParamGalleryIds, galleryIds);
		}
		Trace.d(TAG, "url->" + url + ",params->" + params.toString());
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(url, params, callBack);
	}

	private static final String RemovePicParamPicId = "{img_id}";

	/**
	 * 删除藏品图片
	 * 
	 * @param context
	 * @param picId
	 * @param callBack
	 */
	public void RemovePic(Context context, String picId,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "RemovePic:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.REMOVE_PIC_URL;
		url = url.replace(RemovePicParamPicId, picId);
		Trace.e(TAG, currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}

	private static final String GetAttentionListParamOffset = "[offset]";
	private static final String GetAttentionListParamCount = "[count]";
	private static final String GetAttentionListParamIsRead = "is_read/[is_read]";

	/**
	 * 获取我关注的藏品列表
	 * 
	 * @param context
	 * @param offset
	 * @param count
	 * @param isRead
	 * @param callBack
	 */
	public void GetMyAttentionList(Context context, String offset,
			String count, AjaxCallBack<Object> callBack) {
		final String currMethod = "GetMyAttentionList:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_MY_ATTENTION_LIST_URL;
		url = url.replace(GetAttentionListParamOffset, offset);
		url = url.replace(GetAttentionListParamCount, count);
		Trace.e(TAG, currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}

	private static final String GetMyAttentionTagsParamOffset = "[offset]";
	private static final String GetMyAttentionTagsParamCount = "[count]";
	private static final String GetMyAttentionTagsParamType = "[type]";

	public void GetMyAttentionTags(Context context, String type, String offset,
			String count, AjaxCallBack<Object> callBack) {
		final String currMethod = "GetMyAttentionTags:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_MY_ATTENTION_TAGS_URL;
		url = url.replace(GetMyAttentionTagsParamOffset, offset);
		url = url.replace(GetMyAttentionTagsParamCount, count);
		url = url.replace(GetMyAttentionTagsParamType, type);
		Trace.e(TAG, currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}

	private static final String AddMyAttentionTagsParamType = "[type]";
	private static final String AddMyAttentionTagsParamFollowId = "[follow_id]";

	public void AddMyAttentionTags(Context context, String type,
			String followId, AjaxCallBack<Object> callBack) {
		final String currMethod = "AddMyAttentionTags:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.ADD_MY_ATTENTION_TAGS_URL;
		url = url.replace(AddMyAttentionTagsParamType, type);
		url = url.replace(AddMyAttentionTagsParamFollowId, followId);
		Trace.e(TAG, currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}

	private static final String AddTagParamTagName = "name";

	public void AddTag(Context context, String tagName,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "AddTag:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ADD_TAG_URL;
		AjaxParams params = new AjaxParams();
		if (!StringUtil.getInstance().IsEmpty(tagName)) {
			params.put(AddTagParamTagName, tagName);
		}
		Trace.d(TAG,
				currMethod + "url->" + url + ",params->" + params.toString());
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(url, params, callBack);
	}

	private static final String RemoveTagParamType = "[type]";
	private static final String RemoveTagParamFollowId = "[follow_id]";

	public void RemoveTag(Context context, String type, String followId,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "RemoveTag:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.REMOVE_TAG_URL;
		url = url.replace(RemoveTagParamType, type);
		url = url.replace(RemoveTagParamFollowId, followId);
		Trace.e(TAG, currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}

	/**
	 * 通过标签获取物品列表
	 */
	private static final String GetAllGoodsByTagIDParamOffset = "[offset]";
	private static final String GetAllGoodsByTagIDParamCount = "[count]";
	private static final String GetAllGoodsByTagIDParamID = "{tag_id}";

	public void GetAllGoodsByTagID(Context context, String tagId,
			String offset, String count, AjaxCallBack<Object> callBack) {
		final String currMethod = "GetAllGoodsByTagID:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_ALL_GOODS_BY_TAG_ID;
		url = url.replace(GetAllGoodsByTagIDParamOffset, offset);
		url = url.replace(GetAllGoodsByTagIDParamCount, count);
		url = url.replace(GetAllGoodsByTagIDParamID, tagId);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.post(url, callBack);
	}

	/**
	 * 通过分类获取标签
	 */
	private static final String GetKeyWordsParamOffset = "[offset]";
	private static final String GetKeyWordsParamCount = "[count]";
	private static final String GetKeyWordsParamID = "[tag_cat_id]";
	private static final String GetKeyWordsParamTagID = "[tag_id]";
	private static final String Selected = "[selected]";

	public void GetKeyWords(Context context, String tagCatId, String tagId,
			String serach, String selected, int offset, int count,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "GetKeyWords:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.GET_KEY_WORDS;
		Log.e("anshuai", url + "URl");
		url = url.replace(GetKeyWordsParamOffset, String.valueOf(offset));
		url = url.replace(GetKeyWordsParamCount, String.valueOf(count));
		url = url.replace(GetKeyWordsParamID, tagCatId);
		if (null != tagId)
			// url = url.replaceAll(GetKeyWordsParamTagID, tagId);
			url = url + "/tag_id/" + tagId;
		if (null != serach && !serach.equalsIgnoreCase(""))
			url = url + "/search/" + serach;

		if (null != selected) {
//			url = url.replaceAll(Selected, selected);
			url = url+"/selected/" + selected;
		}

		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e("wangxu", currMethod + "url->" + url);

		fh.get(url, callBack);

	}

	private static final String SearchProductParamOffset = "[offset]";
	private static final String SearchProductParamCount = "[count]";
	private static final String SearchProductParamKeywords = "{keyword}";

	/**
	 * 搜索功能
	 * 
	 * @param context
	 * @param keywords
	 * @param offset
	 * @param count
	 * @param callBack
	 */
	public void SearchProduct(Context context, String keywords, String offset,
			String count, AjaxCallBack<Object> callBack) {
		final String currMethod = "SearchProduct:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.SEARCH_PRODUCT_URL;
		url = url.replace(SearchProductParamKeywords, keywords);
		url = url.replace(SearchProductParamOffset, offset);
		url = url.replace(SearchProductParamCount, count);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String keyWordsManagerOffset = "[offset]";
	private static final String keyWordsManagerCount = "[count]";

	/**
	 * 标签管理
	 * 
	 * @param context
	 * @param keywords
	 * @param offset
	 * @param count
	 * @param callBack
	 */
	public void keyWordsManager(Context context, String offset, String count,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "keyWordsManager:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_ALL_KEY_WORDS;
		url = url.replace(keyWordsManagerOffset, offset);
		url = url.replace(keyWordsManagerCount, count);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String AddKeyWordsTagCatId = "tag_cat_id";
	private static final String AddKeyWordsTagCatName = "tag_cat_name";
	private static final String AddKeyWordsTagIds = "tag_ids";
	private static final String AddKeyWordsTagNames = "tag_names";
	private static final String AddKeyWordsMatchPercent = "match_percent";
	private static final String AddKeyWordsGoodsDesc = "goods_desc";
	private static final String AddKeyWordsCityId = "city_id";
	private static final String EditKeyWord = "goods_id";

	/**
	 * 添加标签
	 * 
	 * @param context
	 * @param compatibility
	 * @param ProductRec
	 * @param callBack
	 */
	public void addKeyWords(boolean isEdit, Context context,
			double compatibility, ProductRec rec, String tagIds,
			String tagNames, String cityId, AjaxCallBack<Object> callBack) {
		final String currMethod = "addKeyWords:";
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		String url = "";
		if (isEdit) {
			url = RequestUrls.SERVER_BASIC_URL + RequestUrls.EDIT_KEY_WORDS;
			if (!StringUtil.getInstance().IsEmpty(rec.getTagCatId())) {
				params.put(EditKeyWord, rec.getId());
			}
		} else {
			url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ADD_NEW_KEY_WORDS;
		}

		if (!StringUtil.getInstance().IsEmpty(String.valueOf(compatibility))) {
			params.put(AddKeyWordsMatchPercent, String.valueOf(compatibility));
		}
		String tagCatId = rec.getTagCatId();
		if (!StringUtil.getInstance().IsEmpty(tagCatId)) {
			params.put(AddKeyWordsTagCatId, tagCatId);
		}
		String tagCatName = rec.getTagCatName();
		if (!StringUtil.getInstance().IsEmpty(tagCatName)) {
			params.put(AddKeyWordsTagCatName, tagCatName);
		}
		if (!StringUtil.getInstance().IsEmpty(tagIds)) {
			params.put(AddKeyWordsTagIds, tagIds);
		}
		if (!StringUtil.getInstance().IsEmpty(tagNames)) {
			params.put(AddKeyWordsTagNames, tagNames);
		}
		if (!StringUtil.getInstance().IsEmpty(rec.getDescription())) {
			params.put(AddKeyWordsGoodsDesc, rec.getDescription());
		}
		if (!StringUtil.getInstance().IsEmpty(cityId)) {
			params.put(AddKeyWordsCityId, cityId);
		}
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.post(url, params, callBack);
	}

	private static final String AddViewCountGoodsID = "[goods_id]";

	/**
	 * 增加一次浏览记录
	 * 
	 * @param context
	 * @param id
	 */

	public void addViewCount(Context context, String id) {
		final String currMethod = "addViewCount:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ADD_VIEW_COUNT;
		url = url.replace(AddViewCountGoodsID, id);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
		};
		fh.get(url, callBack);
	}

	private static final String UploadImageGoodsID = "[goods_id]";

	/**
	 * 更新logo图片
	 * 
	 * @param context
	 * @param id
	 */
	public void uploadImage(Context context, String ID, File img,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "uploadImage:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.UPLOAD_IMAGE;
		AjaxParams params = new AjaxParams();

		try {
			if (null != img && img.exists()) {
				FileInputStream stream = new FileInputStream(img);
				String logoName = img.getName();
				int start = logoName.lastIndexOf(".") + 1;
				String endFix = logoName.substring(start, logoName.length());
				String mimeType = Util.getMimeTypeByFile(endFix);
				Trace.e(TAG, currMethod + "name->" + logoName + ",endFix"
						+ endFix + ",MimeType->" + mimeType);
				params.put(UpdateProductParamLogo, stream, logoName, mimeType);
			}
		} catch (Exception e) {
			Log.e(TAG, currMethod + "add logo fail error.", e);
		}
		url = url.replace(UploadImageGoodsID, ID);
		System.out.println("===uploadImage====" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMethod + "url->" + url);
		fh.post(url, params, callBack);
	}

	/**
	 * 获取匹配的数目
	 */
	private static final String getMatchMatchPercent = "[match_percent]";

	public void getMatchNum(Context context, String tagCatId,
			String matchPercent, String tagCatName, String tagIds,
			String tagNames, AjaxCallBack<Object> callBack) {
		final String currMethod = "getMatchNum:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.GET_MATCH_NUM;
		if (StringUtil.getInstance().IsEmpty(matchPercent)) {
			matchPercent = "0.5";
		}
		url = url.replace(getMatchMatchPercent, matchPercent);
		if (!StringUtil.getInstance().IsEmpty(tagCatId))
			url = url + "/tag_cat_id/" + tagCatId;
		if (!StringUtil.getInstance().IsEmpty(tagCatName))
			url = url + "/tag_cat_name/" + tagCatName;

		if (!StringUtil.getInstance().IsEmpty(tagIds))
			url = url + "/tag_ids/" + tagIds;
		if (!StringUtil.getInstance().IsEmpty(tagNames))
			url = url + "/tag_names/" + tagNames;

		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e("wangxu", currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	/**
	 * 举报
	 */

	public void ToReport(Context context, String Id,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "ToReport:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.REPORT_URL;
		AjaxParams params = new AjaxParams();
		params.put("goods_id", Id);
		params.put("type", "1");
		params.put("content", "垃圾信息");
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e(TAG, currMethod + "url->" + url);
		fh.post(url, params, callBack);
	}

	/**
	 * 赞
	 */
	private final String ToPriaseUserId = "[user_id]";

	public void ToPriase(Context context, String Id,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "ToPriase:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.PRAISE_URL;
		url = url.replace(ToPriaseUserId, Id);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	/**
	 * 拉黑
	 */
	private final String ToBlackUserId = "[user_id]";

	public void ToBlack(Context context, String Id,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "ToReport:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.BLACK_URL;
		url = url.replace(ToBlackUserId, Id);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	/**
	 * 取消拉黑
	 */
	private final String NotBlackUseId = "[user_id]";

	public void NotBlack(Context context, String Id,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "ToReport:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.NOT_Black_URL;
		url = url.replace(NotBlackUseId, Id);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);

	}

	/**
	 * 获取产品详情
	 */
	private final String GetGoodsDetail = "[goods_id]";

	public void GetGoodsDetail(Context context, String Id,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "GetGoodsDetail:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_PRODUCT_DETAIL_URL;
		url = url.replace(GetGoodsDetail, Id);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	/**
	 * 关注企业
	 */
	public void AttentionCom(Context context, String tag_cat_id, String eid,
			String tag_ids, AjaxCallBack<Object> callBack) {
		final String currMethod = "AttentionCom:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ATTENTION;
		AjaxParams params = new AjaxParams();
		params.put("tag_cat_id", tag_cat_id);
		params.put("eid", eid);
		params.put("tag_ids", tag_ids);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Log.e(TAG, currMethod + "url->" + url + ";params=" + params);
		fh.post(url, params, callBack);
	}

	/**
	 * 设备表示
	 */
	private final String devIdentifyAndroidId = "[dev_identify]";

	public void devIdentify(Context context, String androidId,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "devIdentify:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.DEV_IDENTIFY;
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			Log.e("wangxu", "devIdentify-cookie=" + cookie);
			fh.addHeader("cookie", cookie);
		}
		url = url.replace(devIdentifyAndroidId, androidId);
		Log.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	/**
	 * 获取企业信息
	 */
	private final String enterpriseEid = "[eid]";

	public void enterprise(Context context, String eid,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "devIdentify:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.ENTERPRISE;
		url = url.replace(enterpriseEid, eid);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			Log.e("wangxu", "devIdentify-cookie=" + cookie);
			fh.addHeader("cookie", cookie);
		}
		url = url.replace(devIdentifyAndroidId, eid);
		Log.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	private static final String RemoveAttentionParamProdId = "[goods_id]";

	/**
	 * 删除中意的信息
	 * 
	 * @param context
	 * @param prodId
	 * @param callBack
	 */
	public void RemoveAttention(Context context, String prodId,
			AjaxCallBack<Object> callBack) {
		final String currMthod = "RemoveProduct:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.REMOVE_ATTENTION_URL;
		if (prodId != null) {
			url = url.replace(RemoveProductParamProdId, prodId);

		} else {
			url = url.replace("/goods_ids/[goods_id]", "");
		}

		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG, currMthod + "url->" + url);
		System.out.println("====url====" + url);
		fh.get(url, callBack);
	}
}
