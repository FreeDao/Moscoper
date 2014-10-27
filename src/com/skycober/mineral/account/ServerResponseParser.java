package com.skycober.mineral.account;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.skycober.mineral.bean.AvatarRec;
import com.skycober.mineral.bean.CategoryRec;
import com.skycober.mineral.bean.CommentRec;
import com.skycober.mineral.bean.EnterpriseItemRec;
import com.skycober.mineral.bean.KeyWordsRec;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.bean.TagCategoryRec;
import com.skycober.mineral.bean.TagRec;
import com.skycober.mineral.bean.UserRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseAddComment;
import com.skycober.mineral.network.ResponseAddProduct;
import com.skycober.mineral.network.ResponseAddTag;
import com.skycober.mineral.network.ResponseAllGoodsByCatID;
import com.skycober.mineral.network.ResponseCategory;
import com.skycober.mineral.network.ResponseEnterpriseInfo;
import com.skycober.mineral.network.ResponseFavPost;
import com.skycober.mineral.network.ResponseGetAttentionTag;
import com.skycober.mineral.network.ResponseGetCommentList;
import com.skycober.mineral.network.ResponseGetMatchNum;
import com.skycober.mineral.network.ResponseGetMyAttentionList;
import com.skycober.mineral.network.ResponseGetMyFavsList;
import com.skycober.mineral.network.ResponseGetProductDetail;
import com.skycober.mineral.network.ResponseGetSingleProduct;
import com.skycober.mineral.network.ResponseGetUserGoodsList;
import com.skycober.mineral.network.ResponseKeyWords;
import com.skycober.mineral.network.ResponseMakeSale;
import com.skycober.mineral.network.ResponseOauth;
import com.skycober.mineral.network.ResponsePraiseAndBlack;
import com.skycober.mineral.network.ResponseRandom;
import com.skycober.mineral.network.ResponseRegistUser;
import com.skycober.mineral.network.ResponseRemovePic;
import com.skycober.mineral.network.ResponseRemoveProd;
import com.skycober.mineral.network.ResponseRemoveTag;
import com.skycober.mineral.network.ResponseSearchProduct;
import com.skycober.mineral.network.ResponseTagCategory;
import com.skycober.mineral.network.ResponseUploadAvatar;
import com.skycober.mineral.network.ResponseUploadPic;
import com.skycober.mineral.network.ResponseUser;
import com.skycober.mineral.util.Trace;

/**
 * 解析json
 * 
 * @author Yes366
 * 
 */
public class ServerResponseParser {
	private static final String TAG = "ServerResponseParser";

	public BaseResponse parseRegistUser(String json) {
		final String currMethod = "parseRegistUser:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseRegistUser response = new ResponseRegistUser();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						UserRec mRec = new UserRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {
							hasRecord = true;
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(UserRec.ResponseUserId)) {
								try {
									mRec.setUserId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse userId error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseUserName)) {
								try {
									mRec.setUserName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse username error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseEmail)) {
								try {
									mRec.setEmail(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse email error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseRealName)) {
								try {
									mRec.setRealName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse RealName error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseAvatar)) {

								if (ErrorCodeStant.getInstance().isSucceed(
										response.getErrorCode())) {
									reader.beginObject();
									AvatarRec avatarRec = new AvatarRec();
									while (reader.hasNext()) {
										String subTagPicName = reader
												.nextName();
										if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseSmall)) {
											try {
												avatarRec.setSmall(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setSmall error",
														e);
											}

										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseNormal)) {

											try {
												avatarRec.setNormal(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicThumb error",
														e);
											}
										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseBig)) {

											try {
												avatarRec.setBig(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicUrl error",
														e);
											}
										} else {
											reader.skipValue();
										}

									}
									reader.endObject();

									mRec.setAvatar(avatarRec);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseBirthday)) {
								try {
									mRec.setBirthday(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse birthday error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseRegTime)) {
								try {
									mRec.setRegTime(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse regTime error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseSex)) {
								try {
									mRec.setSex(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse Sex error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseVisitCount)) {
								try {
									mRec.setVisitCount(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse VisitCount error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseMSN)) {
								try {
									mRec.setMsn(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse MSN error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseQQ)) {
								try {
									mRec.setQq(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse QQ error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseOfficePhone)) {
								try {
									mRec.setOfficePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse OfficePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseHomePhone)) {
								try {
									mRec.setHomePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse HomePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseMobilePhone)) {
								try {
									mRec.setMobilePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse MobilePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseIsValidated)) {
								try {
									mRec.setIsValidated(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse IsValidated error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseSignature)) {
								try {
									mRec.setSignature(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse Signature error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseIsFollow)) {
								try {
									mRec.setFollowed(reader.nextBoolean());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse isFollowed error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGOnSaleNum)) {
								try {
									mRec.setOnSaleNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse onSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGOffSaleNum)) {
								try {
									mRec.setOffSaleNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse offSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGDeleteSaleNum)) {
								try {
									mRec.setDeleteNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse deleteSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGFavNum)) {
								try {
									mRec.setFavNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse onSaleNum error.", e);
								}
							} else {
								reader.skipValue();
							}
						}
						if (hasRecord)
							response.setUserRec(mRec);
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseOauth(String json) {
		final String currMethod = "parseOauth:";
		Log.e("wangxu", currMethod + "json->" + json);
		ResponseOauth response = new ResponseOauth();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
							Log.e("wangxu",
									"ResponseMessage=" + response.getMessage());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						UserRec mRec = new UserRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {

							hasRecord = true;
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(UserRec.ResponseUserId)) {
								try {
									mRec.setUserId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse userId error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseUserName)) {
								try {
									mRec.setUserName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse username error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseEmail)) {
								try {
									mRec.setEmail(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse username error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseRealName)) {
								try {
									mRec.setRealName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse RealName error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseAvatar)) {

								if (ErrorCodeStant.getInstance().isSucceed(
										response.getErrorCode())) {
									reader.beginObject();
									AvatarRec avatarRec = new AvatarRec();
									while (reader.hasNext()) {
										String subTagPicName = reader
												.nextName();
										if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseSmall)) {
											try {
												avatarRec.setSmall(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setSmall error",
														e);
											}

										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseNormal)) {

											try {
												avatarRec.setNormal(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicThumb error",
														e);
											}
										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseBig)) {

											try {
												avatarRec.setBig(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicUrl error",
														e);
											}
										} else {
											reader.skipValue();
										}

									}
									reader.endObject();

									mRec.setAvatar(avatarRec);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseRegTime)) {
								try {
									mRec.setRegTime(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse regTime error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseBirthday)) {
								try {
									mRec.setBirthday(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse birthday error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseSex)) {
								try {
									mRec.setSex(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse Sex error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseVisitCount)) {
								try {
									mRec.setVisitCount(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse VisitCount error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseMSN)) {
								try {
									mRec.setMsn(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse MSN error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseQQ)) {
								try {
									mRec.setQq(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse QQ error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseOfficePhone)) {
								try {
									mRec.setOfficePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse OfficePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseHomePhone)) {
								try {
									mRec.setHomePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse HomePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseMobilePhone)) {
								try {
									mRec.setMobilePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse MobilePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseIsValidated)) {
								try {
									mRec.setIsValidated(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse IsValidated error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseSignature)) {
								try {
									mRec.setSignature(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse Signature error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseIsFollow)) {
								try {
									mRec.setFollowed(reader.nextBoolean());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse isFollowed error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGOnSaleNum)) {
								try {
									mRec.setOnSaleNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse onSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGOffSaleNum)) {
								try {
									mRec.setOffSaleNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse offSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGDeleteSaleNum)) {
								try {
									mRec.setDeleteNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse deleteSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGFavNum)) {
								try {
									mRec.setFavNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse onSaleNum error.", e);
								}
							} else {
								reader.skipValue();
							}

						}
						if (hasRecord)
							response.setUserRec(mRec);
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseGetUserInfo(String json) {
		final String currMethod = "parseGetUserInfo:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseUser response = new ResponseUser();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						UserRec mRec = new UserRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {

							hasRecord = true;
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(UserRec.ResponseUserId)) {
								try {
									mRec.setUserId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse userId error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseUserName)) {
								try {
									mRec.setUserName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse username error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseEmail)) {
								try {
									mRec.setEmail(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse username error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseRealName)) {
								try {
									mRec.setRealName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse RealName error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseAvatar)) {

								if (ErrorCodeStant.getInstance().isSucceed(
										response.getErrorCode())) {
									reader.beginObject();
									AvatarRec avatarRec = new AvatarRec();
									while (reader.hasNext()) {
										String subTagPicName = reader
												.nextName();
										if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseSmall)) {
											try {
												avatarRec.setSmall(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setSmall error",
														e);
											}

										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseNormal)) {

											try {
												avatarRec.setNormal(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicThumb error",
														e);
											}
										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseBig)) {

											try {
												avatarRec.setBig(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicUrl error",
														e);
											}
										} else {
											reader.skipValue();
										}

									}
									reader.endObject();

									mRec.setAvatar(avatarRec);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseRegTime)) {
								try {
									mRec.setRegTime(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse regTime error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseBirthday)) {
								try {
									mRec.setBirthday(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse birthday error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseAttendNum)) {
								try {
									mRec.setAttendNum(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setAttendNum error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseSex)) {
								try {
									mRec.setSex(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse Sex error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseVisitCount)) {
								try {
									mRec.setVisitCount(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse VisitCount error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseMSN)) {
								try {
									mRec.setMsn(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse MSN error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseQQ)) {
								try {
									mRec.setQq(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse QQ error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseOfficePhone)) {
								try {
									mRec.setOfficePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse OfficePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseHomePhone)) {
								try {
									mRec.setHomePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse HomePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseMobilePhone)) {
								try {
									mRec.setMobilePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse MobilePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseIsValidated)) {
								try {
									mRec.setIsValidated(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse IsValidated error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseSignature)) {
								try {
									mRec.setSignature(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse Signature error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseIsFollow)) {
								try {
									mRec.setFollowed(reader.nextBoolean());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse isFollowed error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGOnSaleNum)) {
								try {
									mRec.setOnSaleNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse onSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGOffSaleNum)) {
								try {
									mRec.setOffSaleNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse offSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGDeleteSaleNum)) {
								try {
									mRec.setDeleteNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse deleteSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGFavNum)) {
								try {
									mRec.setFavNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse onSaleNum error.", e);
								}
							} else {
								reader.skipValue();
							}

						}
						if (hasRecord)
							response.setUserRec(mRec);
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseUpdateUserInfo(String json) {
		final String currMethod = "parseUserInfo:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseUser response = new ResponseUser();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());

						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						UserRec mRec = new UserRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {

							hasRecord = true;
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(UserRec.ResponseUserId)) {
								try {
									mRec.setUserId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse userId error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseUserName)) {
								try {
									mRec.setUserName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse username error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseEmail)) {
								try {
									mRec.setEmail(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse username error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseRealName)) {
								try {
									mRec.setRealName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse RealName error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseAvatar)) {

								if (ErrorCodeStant.getInstance().isSucceed(
										response.getErrorCode())) {
									reader.beginArray();
									AvatarRec avatarRec = new AvatarRec();
									while (reader.hasNext()) {
										String subTagPicName = reader
												.nextName();
										if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseSmall)) {
											try {
												avatarRec.setSmall(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setSmall error",
														e);
											}

										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseNormal)) {

											try {
												avatarRec.setNormal(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicThumb error",
														e);
											}
										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseBig)) {

											try {
												avatarRec.setBig(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicUrl error",
														e);
											}
										} else {
											reader.skipValue();
										}

									}
									reader.endArray();

									mRec.setAvatar(avatarRec);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseRegTime)) {
								try {
									mRec.setRegTime(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse regTime error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseBirthday)) {
								try {
									mRec.setBirthday(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse birthday error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseSex)) {
								try {
									mRec.setSex(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse Sex error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseVisitCount)) {
								try {
									mRec.setVisitCount(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse VisitCount error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseMSN)) {
								try {
									mRec.setMsn(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse MSN error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseQQ)) {
								try {
									mRec.setQq(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse QQ error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseOfficePhone)) {
								try {
									mRec.setOfficePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse OfficePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseHomePhone)) {
								try {
									mRec.setHomePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse HomePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseMobilePhone)) {
								try {
									mRec.setMobilePhone(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse MobilePhone error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseIsValidated)) {
								try {
									mRec.setIsValidated(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse IsValidated error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseSignature)) {
								try {
									mRec.setSignature(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse Signature error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseIsFollow)) {
								try {
									mRec.setFollowed(reader.nextBoolean());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse isFollowed error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGOnSaleNum)) {
								try {
									mRec.setOnSaleNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse onSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGOffSaleNum)) {
								try {
									mRec.setOffSaleNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse offSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGDeleteSaleNum)) {
								try {
									mRec.setDeleteNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse deleteSaleNum error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(UserRec.ResponseGFavNum)) {
								try {
									mRec.setFavNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse onSaleNum error.", e);
								}
							} else {
								reader.skipValue();
							}

						}
						if (hasRecord)
							response.setUserRec(mRec);
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseUpdatePwd(String json) {
		final String currMethod = "parseUpdatePwd:";
		Trace.d(TAG, currMethod + "json->" + json);
		BaseResponse response = new BaseResponse();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					reader.skipValue();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseUploadAvatar(String json) {
		final String currMethod = "parseUploadAvatar:";
		Log.e("json", "json=" + json);
		ResponseUploadAvatar response = new ResponseUploadAvatar();
		try {

			JsonReader reader = new JsonReader(new StringReader(json));
			reader.setLenient(true);
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						AvatarRec rec = new AvatarRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {
							String avatarTagName = reader.nextName();
							hasRecord = true;
							if (avatarTagName
									.equalsIgnoreCase(AvatarRec.ResponseBig)) {
								try {
									rec.setBig(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse big avatar error.", e);
								}
							} else if (avatarTagName
									.equalsIgnoreCase(AvatarRec.ResponseNormal)) {
								try {
									rec.setNormal(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse middle avatar error.", e);
								}
							} else if (avatarTagName
									.equalsIgnoreCase(AvatarRec.ResponseSmall)) {
								try {
									rec.setSmall(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse small avatar error.", e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
						if (hasRecord)
							response.setAvatarRec(rec);
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		Trace.d(TAG, currMethod + response.toString());
		return response;
	}

	// 获取某个分类列表
	public BaseResponse parseGetAllGoodsByCatID(String json) {
		final String currMethod = "GetAllGoodsByCatID:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseAllGoodsByCatID response = new ResponseAllGoodsByCatID();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginArray();
						List<ProductRec> plist = new ArrayList<ProductRec>();

						while (reader.hasNext()) {
							reader.beginObject();
							ProductRec productRec = new ProductRec();
							boolean hasRecord = false;
							while (reader.hasNext()) {
								String subTagName = reader.nextName();
								hasRecord = true;
								if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
									try {
										productRec.setId(reader.nextString());

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setId error", e);
									}

								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTagCatId)) {
									try {
										productRec.setTagCatId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTagCatName)) {
									try {
										productRec.setTagCatName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatName error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsDescription)) {
									try {
										productRec.setDescription(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setDescription error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseCollectUserNum)) {
									try {
										productRec
												.setCollectUserNum(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setDescription error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseAddTime)) {
									try {
										productRec
												.setAddTime(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setAddTime error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseUserIsBlack)) {
									try {
										String isBlack = reader.nextString();
										if (isBlack.equals("1")) {
											productRec.setBlackUser(true);
										} else {
											productRec.setBlackUser(false);
										}

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setBlackUser error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseViewNum)) {
									try {
										productRec
												.setViewNum(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setViewNum error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseUserId)) {
									try {
										productRec.setUserId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setUserId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseRealName)) {
									try {
										productRec.setRealName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setRealName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseIsFav)) {
									try {
										productRec.setInFav(reader.nextString()
												.equalsIgnoreCase("1"));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTags)) {
									reader.beginArray();
									List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										while (reader.hasNext()) {
											String tagsName = reader.nextName();
											KeyWordsRec rec = new KeyWordsRec();
											if (tagsName
													.equalsIgnoreCase(KeyWordsRec.ResponseTagId)) {
												try {
													rec.setTagID(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setTagID error",
															e);
												}
											} else if (tagsName
													.equalsIgnoreCase(KeyWordsRec.ResponseTagName)) {
												try {
													rec.setTagName(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setTagName error",
															e);
												}
											} else {
												reader.skipValue();
											}
											recs.add(rec);
										}
										reader.endObject();
									}
									reader.endArray();
									productRec.setTags(recs);
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseName)) {
									try {
										productRec.setName(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsThumb)) {
									try {
										productRec
												.setThumb(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setThumb error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
									try {
										productRec.setImg(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setImg error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseOriginalImg)) {
									try {
										productRec.setOriginalImg(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setOriginalImg error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGallery)) {
									if (ErrorCodeStant.getInstance().isSucceed(
											response.getErrorCode())) {
										reader.beginArray();
										List<PicRec> list = new ArrayList<PicRec>();
										while (reader.hasNext()) {
											reader.beginObject();
											PicRec pictRec = new PicRec();
											boolean Record = false;
											while (reader.hasNext()) {
												Record = true;
												String subTagPicName = reader
														.nextName();
												if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgId)) {
													try {
														pictRec.setId(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicId error",
																e);
													}

												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseThumb)) {

													try {
														pictRec.setThumb(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicThumb error",
																e);
													}
												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgUrl)) {

													try {
														pictRec.setUrl(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicUrl error",
																e);
													}
												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgDescription)) {

													try {
														pictRec.setDescription(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicDescription error",
																e);
													}
												} else {
													reader.skipValue();
												}

											}
											reader.endObject();
											if (Record) {
												list.add(pictRec);
											}
										}
										reader.endArray();
										productRec.setPicList(list);
									}

								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
							if (hasRecord) {
								plist.add(productRec);

							}
						}
						reader.endArray();
						response.setProductRecs(plist);

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;

	}

	/**
	 * 
	 */
	public BaseResponse parseGetCategory(String json) {
		final String currMethod = "GetCollectionClassification:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseCategory response = new ResponseCategory();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginArray();

						List<CategoryRec> clist = new ArrayList<CategoryRec>();

						while (reader.hasNext()) {
							reader.beginObject();
							CategoryRec categoryRec = new CategoryRec();
							boolean hasRecord = false;
							while (reader.hasNext()) {
								hasRecord = true;
								String subTagName = reader.nextName();
								if (subTagName
										.equalsIgnoreCase(CategoryRec.ResponseCatId)) {
									try {
										categoryRec.setId(reader.nextString());

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse catid error", e);
									}

								} else if (subTagName
										.equalsIgnoreCase(CategoryRec.ResponseCatName)) {
									try {
										categoryRec
												.setName(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse catname error", e);
									}

								} else if (subTagName
										.equalsIgnoreCase(CategoryRec.ResponseParentId)) {
									try {
										categoryRec.setParentId(reader
												.nextString());

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse ParentId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CategoryRec.ResponseIsFollow)) {
									try {
										categoryRec.setFollowed(reader
												.nextBoolean());
									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse isFollowed error", e);
									}
								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
							if (hasRecord) {
								clist.add(categoryRec);
							}
						}
						reader.endArray();
						response.setCategoryRec(clist);

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseAddFav(String json) {
		ResponseFavPost response = new ResponseFavPost();
		final String currMethod = "parseAddFav:";

		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			Log.e(TAG, "beginObject");
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					Log.e(TAG, "tagName");
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						Log.e(TAG, "beginObject");
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							Log.e(TAG, "nextName");
							if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
								try {
									response.setId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse fid error.",
											e);
								}

							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseChanceFav(String json) {
		ResponseFavPost response = new ResponseFavPost();
		final String currMethod = "parseChanceFav:";

		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			Log.e(TAG, "beginObject");
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					Log.e(TAG, "tagName");
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						Log.e(TAG, "beginObject");
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							Log.e(TAG, "nextName");
							if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
								try {
									response.setId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse fid error.",
											e);
								}

							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	// 获取我的收藏(和我要)列表
	public BaseResponse parseGetMyFavsList(String json) {
		final String currMethod = "GetMyFavsList:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseGetMyFavsList response = new ResponseGetMyFavsList();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginArray();
						List<ProductRec> plist = new ArrayList<ProductRec>();

						while (reader.hasNext()) {
							reader.beginObject();
							ProductRec productRec = new ProductRec();
							boolean hasRecord = false;
							while (reader.hasNext()) {
								String subTagName = reader.nextName();
								hasRecord = true;
								if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
									try {
										productRec.setId(reader.nextString());

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setId error", e);
									}

								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTagCatId)) {
									try {
										productRec.setTagCatId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseCityName)) {
									try {
										productRec.setCityName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseProName)) {
									try {
										productRec.setProName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseCityId)) {
									try {
										productRec.setCityId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseProId)) {
									try {
										productRec
												.setProId(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								}

								else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTagCatName)) {
									try {
										productRec.setTagCatName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatName error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsDescription)) {
									try {
										productRec.setDescription(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setDescription error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseUserIsBlack)) {
									try {
										String isBlack = reader.nextString();
										if (isBlack.equals("1")) {
											productRec.setBlackUser(true);
										} else {
											productRec.setBlackUser(false);
										}

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setBlackUser error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseCollectUserNum)) {
									try {
										productRec
												.setCollectUserNum(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setDescription error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseAddTime)) {
									try {
										productRec
												.setAddTime(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setAddTime error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseViewNum)) {
									try {
										productRec
												.setViewNum(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setViewNum error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseUserId)) {
									try {
										productRec.setUserId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setUserId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseRealName)) {
									try {
										productRec.setRealName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setRealName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseIsFav)) {
									try {
										productRec.setInFav(reader.nextString()
												.equalsIgnoreCase("1"));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTags)) {
									reader.beginArray();
									List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										KeyWordsRec rec = new KeyWordsRec();

										while (reader.hasNext()) {
											String tagsName = reader.nextName();
											if (tagsName
													.equalsIgnoreCase(KeyWordsRec.ResponseTagId)) {
												try {
													rec.setTagID(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setTagID error",
															e);
												}
											} else if (tagsName
													.equalsIgnoreCase(KeyWordsRec.ResponseTagName)) {
												try {
													rec.setTagName(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setTagName error",
															e);
												}
											} else {
												reader.skipValue();
											}
										}
										recs.add(rec);
										reader.endObject();
									}
									reader.endArray();
									productRec.setTags(recs);
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseName)) {
									try {
										productRec.setName(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsThumb)) {
									try {
										productRec
												.setThumb(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setThumb error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
									try {
										productRec.setImg(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setImg error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseOriginalImg)) {
									try {
										productRec.setOriginalImg(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setOriginalImg error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGallery)) {
									if (ErrorCodeStant.getInstance().isSucceed(
											response.getErrorCode())) {
										reader.beginArray();
										List<PicRec> list = new ArrayList<PicRec>();
										while (reader.hasNext()) {
											reader.beginObject();
											PicRec pictRec = new PicRec();
											boolean Record = false;
											while (reader.hasNext()) {
												Record = true;
												String subTagPicName = reader
														.nextName();
												if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgId)) {
													try {
														pictRec.setId(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicId error",
																e);
													}

												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseThumb)) {

													try {
														pictRec.setThumb(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicThumb error",
																e);
													}
												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgUrl)) {

													try {
														pictRec.setUrl(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicUrl error",
																e);
													}
												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgDescription)) {

													try {
														pictRec.setDescription(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicDescription error",
																e);
													}
												} else {
													reader.skipValue();
												}

											}
											reader.endObject();
											if (Record) {
												list.add(pictRec);
											}
										}
										reader.endArray();
										productRec.setPicList(list);
									}

								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
							if (hasRecord) {
								plist.add(productRec);
							}
						}
						reader.endArray();
						response.setProductRecs(plist);

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;

	}

	/**
	 * 我发布的藏品
	 */
	public BaseResponse parseGetUserGoodsList(String json) {
		final String currMethod = "parseGetUserGoodsList:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseGetUserGoodsList response = new ResponseGetUserGoodsList();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginArray();
						List<ProductRec> plist = new ArrayList<ProductRec>();

						while (reader.hasNext()) {
							reader.beginObject();
							ProductRec productRec = new ProductRec();
							boolean hasRecord = false;
							while (reader.hasNext()) {
								String subTagName = reader.nextName();
								hasRecord = true;
								if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
									try {
										productRec.setId(reader.nextString());

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setId error", e);
									}

								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTagCatId)) {
									try {
										productRec.setTagCatId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTagCatName)) {
									try {
										productRec.setTagCatName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatName error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsDescription)) {
									try {
										productRec.setDescription(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setDescription error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseCollectUserNum)) {
									try {
										productRec
												.setCollectUserNum(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setDescription error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseAddTime)) {
									try {
										productRec
												.setAddTime(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setAddTime error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseViewNum)) {
									try {
										productRec
												.setViewNum(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setViewNum error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseUserId)) {
									try {
										productRec.setUserId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setUserId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseRealName)) {
									try {
										productRec.setRealName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setRealName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseIsFav)) {
									try {
										productRec.setInFav(Boolean
												.valueOf(reader.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTags)) {
									reader.beginArray();
									List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										while (reader.hasNext()) {
											String tagsName = reader.nextName();
											KeyWordsRec rec = new KeyWordsRec();
											if (tagsName
													.equalsIgnoreCase(KeyWordsRec.ResponseTagId)) {
												try {
													rec.setTagID(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setTagID error",
															e);
												}
											} else if (tagsName
													.equalsIgnoreCase(KeyWordsRec.ResponseTagName)) {
												try {
													rec.setTagName(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setTagName error",
															e);
												}
											} else {
												reader.skipValue();
											}
											recs.add(rec);
										}
										reader.endObject();
									}
									reader.endArray();
									productRec.setTags(recs);
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseName)) {
									try {
										productRec.setName(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsThumb)) {
									try {
										productRec
												.setThumb(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setThumb error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
									try {
										productRec.setImg(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setImg error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseOriginalImg)) {
									try {
										productRec.setOriginalImg(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setOriginalImg error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGallery)) {
									if (ErrorCodeStant.getInstance().isSucceed(
											response.getErrorCode())) {
										reader.beginArray();
										List<PicRec> list = new ArrayList<PicRec>();
										while (reader.hasNext()) {
											reader.beginObject();
											PicRec pictRec = new PicRec();
											boolean Record = false;
											while (reader.hasNext()) {
												Record = true;
												String subTagPicName = reader
														.nextName();
												if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgId)) {
													try {
														pictRec.setId(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicId error",
																e);
													}

												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseThumb)) {

													try {
														pictRec.setThumb(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicThumb error",
																e);
													}
												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgUrl)) {

													try {
														pictRec.setUrl(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicUrl error",
																e);
													}
												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgDescription)) {

													try {
														pictRec.setDescription(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicDescription error",
																e);
													}
												} else {
													reader.skipValue();
												}

											}
											reader.endObject();
											if (Record) {
												list.add(pictRec);
											}
										}
										reader.endArray();
										productRec.setPicList(list);
									}

								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
							if (hasRecord) {
								plist.add(productRec);

							}
						}
						reader.endArray();
						response.setProductRecs(plist);

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;

	}

	/**
	 * 摇一摇
	 * 
	 * @param json
	 * @return
	 */
	public BaseResponse parseGetRandomGoods(String json) {
		final String currMethod = "parseGetRandomGoods:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseRandom response = new ResponseRandom();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {

						reader.beginObject();
						ProductRec productRec = new ProductRec();
						boolean hasRecord = false;
						// while (reader.hasNext()) {
						// hasRecord = true;
						// String subTagName = reader.nextName();
						// if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
						// try {
						// productRec.setId(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod + "parse ID error", e);
						// }
						//
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseUserId)) {
						// try {
						// productRec.setUserId(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse UserId error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsUserName))
						// {
						// try {
						// productRec.setUserName(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse UserId error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseCatId)) {
						// try {
						// productRec.setCatId(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG,
						// currMethod + "parse CatId error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseCatName)) {
						// try {
						// productRec.setCatName(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse setCatName error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsSn)) {
						// try {
						// productRec.setSn(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod + "parse Sn error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseName)) {
						// try {
						// productRec.setName(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod + "parse Name error",
						// e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseNameStyle)) {
						// try {
						// productRec
						// .setNameStyle(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse NameStyle error", e);
						// }
						//
						// }
						//
						// else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseFavNum)) {
						// try {
						// productRec.setFavNum(Integer.valueOf(reader
						// .nextString()));
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse NameStyle error", e);
						// }
						//
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseClickCount)) {
						// try {
						// productRec.setClickCount(Long
						// .valueOf(reader.nextString()));
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse ClickCount error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseBrandId)) {
						// try {
						// productRec.setBrandId(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse BrandId error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseProviderName)) {
						// try {
						// productRec.setProviderName(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse ProviderName error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsNumber)) {
						// try {
						// productRec.setNumber(Long.valueOf(reader
						// .nextString()));
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse Number error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsWeight)) {
						// try {
						// productRec.setWeight(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse Number error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseMarketPrice)) {
						// try {
						// productRec.setMarketPrice(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse MarketPrice error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseShopPrice)) {
						// try {
						// productRec
						// .setShopPrice(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse ShopPrice error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponsePromotePrice)) {
						// try {
						// productRec.setPromotePrice(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse PromotePrice error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsBrief)) {
						// try {
						// productRec.setBrief(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG,
						// currMethod + "parse Brief error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsDescription))
						// {
						// try {
						// productRec.setDescription(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse Description error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsThumb)) {
						// try {
						// productRec.setThumb(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG,
						// currMethod + "parse Thumb error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
						// try {
						// productRec.setImg(reader.nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod + "parse Img error",
						// e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseOriginalImg)) {
						// try {
						// productRec.setOriginalImg(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse setOriginalImg error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseIsFav)) {
						// try {
						// productRec.setInFav(reader.nextBoolean());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse setOriginalImg error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseAddTime)) {
						// try {
						// productRec.setAddTime(Long.valueOf(reader
						// .nextString()));
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse setAddTime error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseSuppliesId)) {
						// try {
						// productRec.setSuppliersId(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse setSuppliersId error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseIsOnSale)) {
						// try {
						// productRec.setOnSale(reader.nextString()
						// .equals("0") ? false : true);
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG, currMethod
						// + "parse setSuppliersId error", e);
						// }
						// } else if (subTagName
						// .equalsIgnoreCase(ProductRec.ResponseGallery)) {
						// if (ErrorCodeStant.getInstance().isSucceed(
						// response.getErrorCode())) {
						// reader.beginArray();
						// List<PicRec> list = new ArrayList<PicRec>();
						// while (reader.hasNext()) {
						// reader.beginObject();
						// PicRec pictRec = new PicRec();
						// boolean Record = false;
						// while (reader.hasNext()) {
						// Record = true;
						// String subTagPicName = reader
						// .nextName();
						// if (subTagPicName
						// .equalsIgnoreCase(PicRec.ResponseImgId)) {
						// try {
						// pictRec.setId(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG,
						// currMethod
						// + "parse setPicId error",
						// e);
						// }
						//
						// } else if (subTagPicName
						// .equalsIgnoreCase(PicRec.ResponseThumb)) {
						//
						// try {
						// pictRec.setThumb(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG,
						// currMethod
						// + "parse setPicThumb error",
						// e);
						// }
						// } else if (subTagPicName
						// .equalsIgnoreCase(PicRec.ResponseImgUrl)) {
						//
						// try {
						// pictRec.setUrl(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG,
						// currMethod
						// + "parse setPicUrl error",
						// e);
						// }
						// } else if (subTagPicName
						// .equalsIgnoreCase(PicRec.ResponseImgDescription)) {
						//
						// try {
						// pictRec.setDescription(reader
						// .nextString());
						//
						// } catch (Exception e) {
						//
						// reader.skipValue();
						// Log.w(TAG,
						// currMethod
						// + "parse setPicDescription error",
						// e);
						// }
						// } else {
						// reader.skipValue();
						// }
						//
						// }
						// reader.endObject();
						// if (Record) {
						// list.add(pictRec);
						// }
						// }
						// reader.endArray();
						// productRec.setPicList(list);
						// }
						//
						// } else {
						// reader.skipValue();
						// }
						// }
						reader.endObject();
						if (hasRecord) {
							response.setProductRec(productRec);
						}

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;

	}

	public BaseResponse parseMakeOnSale(String json) {
		ResponseMakeSale response = new ResponseMakeSale();
		final String currMethod = "parseMakeOnSale:";

		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							Log.e(TAG, "nextName");
							if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
								try {
									response.setId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse id error.",
											e);
								}

							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	// 下架

	public BaseResponse parseMakeOffSale(String json) {
		ResponseMakeSale response = new ResponseMakeSale();
		final String currMethod = "parseMakeOffSale:";

		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							Log.e(TAG, "nextName");
							if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
								try {
									response.setId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse id error.",
											e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseAddProduct(String json) {
		ResponseAddProduct response = new ResponseAddProduct();
		final String currMethod = "parseAddProduct:";
		Trace.d(TAG, currMethod + "json->" + json);
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						ProductRec productRec = new ProductRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							hasRecord = true;
							if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
								try {
									productRec.setId(reader.nextString());

								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG,
											currMethod + "parse setId error", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTagCatId)) {
								try {
									productRec.setTagCatId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTagCatName)) {
								try {
									productRec.setTagCatName(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsDescription)) {
								try {
									productRec.setDescription(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setDescription error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseCollectUserNum)) {
								try {
									productRec.setCollectUserNum(Long
											.parseLong(reader.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setDescription error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseAddTime)) {
								try {
									productRec.setAddTime(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setAddTime error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseViewNum)) {
								try {
									productRec.setViewNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setViewNum error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseUserId)) {
								try {
									productRec.setUserId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setUserId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseRealName)) {
								try {
									productRec.setRealName(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setRealName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseIsFav)) {
								try {
									productRec.setInFav(reader.nextString()
											.equalsIgnoreCase("1"));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTags)) {
								reader.beginArray();
								List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
								while (reader.hasNext()) {
									reader.beginObject();
									while (reader.hasNext()) {
										String tagsName = reader.nextName();
										KeyWordsRec rec = new KeyWordsRec();
										if (tagsName
												.equalsIgnoreCase(KeyWordsRec.ResponseTagId)) {
											try {
												rec.setTagID(reader
														.nextString());
											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setTagID error",
														e);
											}
										} else if (tagsName
												.equalsIgnoreCase(KeyWordsRec.ResponseTagName)) {
											try {
												rec.setTagName(reader
														.nextString());
											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setTagName error",
														e);
											}
										} else {
											reader.skipValue();
										}
										recs.add(rec);
									}
									reader.endObject();
								}
								reader.endArray();
								productRec.setTags(recs);
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseName)) {
								try {
									productRec.setName(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsThumb)) {
								try {
									productRec.setThumb(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setThumb error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
								try {
									productRec.setImg(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setImg error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseOriginalImg)) {
								try {
									productRec.setOriginalImg(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setOriginalImg error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGallery)) {
								if (ErrorCodeStant.getInstance().isSucceed(
										response.getErrorCode())) {
									reader.beginArray();
									List<PicRec> list = new ArrayList<PicRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										PicRec pictRec = new PicRec();
										boolean Record = false;
										while (reader.hasNext()) {
											Record = true;
											String subTagPicName = reader
													.nextName();
											if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgId)) {
												try {
													pictRec.setId(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicId error",
															e);
												}

											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseThumb)) {

												try {
													pictRec.setThumb(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicThumb error",
															e);
												}
											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgUrl)) {

												try {
													pictRec.setUrl(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicUrl error",
															e);
												}
											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgDescription)) {

												try {
													pictRec.setDescription(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicDescription error",
															e);
												}
											} else {
												reader.skipValue();
											}

										}
										reader.endObject();
										if (Record) {
											list.add(pictRec);
										}
									}
									reader.endArray();
									productRec.setPicList(list);
								}

							} else {
								reader.skipValue();
							}

						}
						if (hasRecord) {
							response.setProductRec(productRec);
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseUploadPic(String json) {
		ResponseUploadPic response = new ResponseUploadPic();
		final String currMethod = "parseUploadPic:";
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						PicRec picRec = new PicRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							hasRecord = true;
							if (subTagName
									.equalsIgnoreCase(PicRec.ResponseImgId)) {
								try {
									picRec.setId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod
											+ "parse imgId error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(PicRec.ResponseGoodsId)) {
								try {
									picRec.setGoodsId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod
											+ "parse goodsId error.", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(PicRec.ResponseImgUrl)) {
								try {
									picRec.setUrl(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod
											+ "parse imgUrl error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(PicRec.ResponseImgDescription)) {
								try {
									picRec.setDescription(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod
											+ "parse imgDesc error.", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(PicRec.ResponseThumb)) {
								try {
									picRec.setThumb(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod
											+ "parse thumb error.", e);
								}
							} else {
								reader.skipValue();
							}
						}
						if (hasRecord)
							response.setPicRec(picRec);
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseRemoveProduct(String json) {
		ResponseRemoveProd response = new ResponseRemoveProd();
		final String currMethod = "parseRemoveProduct:";
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(ResponseRemoveProd.ResponseProdId)) {
								try {
									response.setProdId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod
											+ "parse prodId error.", e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseGetCommentList(String json) {
		final String currMethod = "parseGetCommentList:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseGetCommentList response = new ResponseGetCommentList();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginArray();
						List<CommentRec> plist = new ArrayList<CommentRec>();

						while (reader.hasNext()) {
							reader.beginObject();
							CommentRec rec = new CommentRec();
							boolean hasRecord = false;
							while (reader.hasNext()) {
								hasRecord = true;
								String subTagName = reader.nextName();
								if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseCommentId)) {
									try {
										rec.setId(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse ID error", e);
									}

								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseProdId)) {
									try {
										rec.setProdId(reader.nextString());

									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse ProdId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseUserName)) {
									try {
										rec.setUserName(reader.nextString());
									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse userName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseContent)) {
									try {
										rec.setContent(reader.nextString());
									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse content error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseCommentRank)) {
									try {
										rec.setRank(Integer.parseInt(reader
												.nextString()));
									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse rank error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseAddTime)) {
									try {
										rec.setSendTime(Long.parseLong(reader
												.nextString()));
									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse addTime error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseStatus)) {
									try {
										String checkStatus = reader
												.nextString();
										if (null != checkStatus
												&& checkStatus
														.equalsIgnoreCase("1")) {
											rec.setChecked(true);
										} else {
											rec.setChecked(false);
										}
									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse status error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseParentId)) {
									try {
										rec.setParentId(reader.nextString());
									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse parentId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseUserId)) {
									try {
										rec.setUserId(reader.nextString());
									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse userId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(CommentRec.ResponseAvatar)) {
									try {
										reader.beginObject();
										AvatarRec avatarRec = new AvatarRec();
										boolean recorded = false;
										while (reader.hasNext()) {
											String subTagPicName = reader
													.nextName();
											recorded = true;
											if (subTagPicName
													.equalsIgnoreCase(AvatarRec.ResponseSmall)) {
												try {
													avatarRec.setSmall(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setSmall error",
															e);
												}

											} else if (subTagPicName
													.equalsIgnoreCase(AvatarRec.ResponseNormal)) {

												try {
													avatarRec.setNormal(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicThumb error",
															e);
												}
											} else if (subTagPicName
													.equalsIgnoreCase(AvatarRec.ResponseBig)) {

												try {
													avatarRec.setBig(reader
															.nextString());

												} catch (Exception e) {
													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicUrl error",
															e);
												}
											} else {
												reader.skipValue();
											}
										}
										if (recorded)
											rec.setAvatar(avatarRec);
										reader.endObject();

									} catch (Exception e) {
										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse avatar error", e);
									}
								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
							if (hasRecord) {
								plist.add(rec);

							}
						}
						reader.endArray();
						response.setCommentList(plist);
					} else {
						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseAddComment(String json) {
		final String currMethod = "parseAddComment:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseAddComment response = new ResponseAddComment();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						CommentRec rec = new CommentRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {
							hasRecord = true;
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseCommentId)) {
								try {
									rec.setId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod + "parse ID error", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseProdId)) {
								try {
									rec.setProdId(reader.nextString());

								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse ProdId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseUserName)) {
								try {
									rec.setUserName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse userName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseContent)) {
								try {
									rec.setContent(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse content error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseCommentRank)) {
								try {
									rec.setRank(Integer.parseInt(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse rank error",
											e);
								}
							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseAddTime)) {
								try {
									rec.setSendTime(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse addTime error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseStatus)) {
								try {
									String checkStatus = reader.nextString();
									if (null != checkStatus
											&& checkStatus
													.equalsIgnoreCase("1")) {
										rec.setChecked(true);
									} else {
										rec.setChecked(false);
									}
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse status error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseParentId)) {
								try {
									rec.setParentId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse parentId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseUserId)) {
								try {
									rec.setUserId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse userId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(CommentRec.ResponseAvatar)) {
								try {
									reader.beginObject();
									AvatarRec avatarRec = new AvatarRec();
									boolean recorded = false;
									while (reader.hasNext()) {
										String subTagPicName = reader
												.nextName();
										recorded = true;
										if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseSmall)) {
											try {
												avatarRec.setSmall(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setSmall error",
														e);
											}

										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseNormal)) {

											try {
												avatarRec.setNormal(reader
														.nextString());

											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicThumb error",
														e);
											}
										} else if (subTagPicName
												.equalsIgnoreCase(AvatarRec.ResponseBig)) {

											try {
												avatarRec.setBig(reader
														.nextString());

											} catch (Exception e) {
												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setPicUrl error",
														e);
											}
										} else {
											reader.skipValue();
										}
									}
									if (recorded)
										rec.setAvatar(avatarRec);
									reader.endObject();

								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse avatar error", e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
						if (hasRecord) {
							response.setCommentRec(rec);
						}
					} else {
						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseGetSingleProductInfo(String json) {
		final String currMethod = "parseGetSingleProductInfo:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseGetSingleProduct response = new ResponseGetSingleProduct();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						ProductRec productRec = new ProductRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {

							String subTagName = reader.nextName();
							hasRecord = true;
							if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
								try {
									productRec.setId(reader.nextString());

								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG,
											currMethod + "parse setId error", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTagCatId)) {
								try {
									productRec.setTagCatId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTagCatName)) {
								try {
									productRec.setTagCatName(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsDescription)) {
								try {
									productRec.setDescription(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setDescription error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseCollectUserNum)) {
								try {
									productRec.setCollectUserNum(Long
											.parseLong(reader.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setDescription error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseAddTime)) {
								try {
									productRec.setAddTime(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setAddTime error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseViewNum)) {
								try {
									productRec.setViewNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setViewNum error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseUserId)) {
								try {
									productRec.setUserId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setUserId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseRealName)) {
								try {
									productRec.setRealName(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setRealName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseCityId)) {
								try {
									productRec.setCityId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setCityId error", e);
								}
							}

							else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseCityName)) {
								try {
									productRec.setCityName(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setCityName error", e);
								}
							}

							else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseProId)) {
								try {
									productRec.setProId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setProId error", e);
								}
							}

							else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseProName)) {
								try {
									productRec.setProName(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatId error", e);
								}
							}

							else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseIsFav)) {
								try {
									productRec.setInFav(Boolean.valueOf(reader
											.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTags)) {
								reader.beginArray();
								List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
								while (reader.hasNext()) {
									reader.beginObject();
									KeyWordsRec rec = new KeyWordsRec();

									while (reader.hasNext()) {
										String tagsName = reader.nextName();
										if (tagsName
												.equalsIgnoreCase(KeyWordsRec.ResponseTagId)) {
											try {
												rec.setTagID(reader
														.nextString());
											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setTagID error",
														e);
											}
										} else if (tagsName
												.equalsIgnoreCase(KeyWordsRec.ResponseTagName)) {
											try {
												rec.setTagName(reader
														.nextString());
											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setTagName error",
														e);
											}
										} else {
											reader.skipValue();
										}
									}
									recs.add(rec);
									reader.endObject();
								}
								reader.endArray();
								productRec.setTags(recs);
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseName)) {
								try {
									productRec.setName(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsThumb)) {
								try {
									productRec.setThumb(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setThumb error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
								try {
									productRec.setImg(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setImg error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseOriginalImg)) {
								try {
									productRec.setOriginalImg(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setOriginalImg error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGallery)) {
								if (ErrorCodeStant.getInstance().isSucceed(
										response.getErrorCode())) {
									reader.beginArray();
									List<PicRec> list = new ArrayList<PicRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										PicRec pictRec = new PicRec();
										boolean Record = false;
										while (reader.hasNext()) {
											Record = true;
											String subTagPicName = reader
													.nextName();
											if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgId)) {
												try {
													pictRec.setId(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicId error",
															e);
												}

											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseThumb)) {

												try {
													pictRec.setThumb(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicThumb error",
															e);
												}
											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgUrl)) {

												try {
													pictRec.setUrl(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicUrl error",
															e);
												}
											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgDescription)) {

												try {
													pictRec.setDescription(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicDescription error",
															e);
												}
											} else {
												reader.skipValue();
											}

										}
										reader.endObject();
										if (Record) {
											list.add(pictRec);
										}
									}
									reader.endArray();
									productRec.setPicList(list);
								}

							} else {
								reader.skipValue();
							}

						}
						reader.endObject();
						if (hasRecord) {
							response.setProductRec(productRec);
						}
					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseRemovePic(String json) {
		final String currMethod = "parseRemovePic:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseRemovePic response = new ResponseRemovePic();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(ResponseRemovePic.ResponsePicId)) {
								try {
									response.setPicId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse ID error", e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseGetMyAttentionList(String json) {
		final String currMethod = "parseGetMyAttentionList:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseGetMyAttentionList response = new ResponseGetMyAttentionList();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginArray();
						List<ProductRec> plist = new ArrayList<ProductRec>();

						while (reader.hasNext()) {
							reader.beginObject();
							ProductRec productRec = new ProductRec();
							boolean hasRecord = false;
							while (reader.hasNext()) {
								String subTagName = reader.nextName();
								hasRecord = true;
								if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
									try {
										productRec.setId(reader.nextString());

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setId error", e);
									}

								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTagCatId)) {
									try {
										productRec.setTagCatId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTagCatName)) {
									try {
										productRec.setTagCatName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatName error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.PRAISE_NUM)) {
									try {
										productRec.setPraise_num(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setAddTime error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.BLACK_NUM)) {
									try {
										productRec.setBlack_num(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setAddTime error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseUserIsBlack)) {
									try {
										String isBlack = reader.nextString();
										if (isBlack.equals("1")) {
											productRec.setBlackUser(true);
										} else {
											productRec.setBlackUser(false);
										}

									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setBlackUser error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsDescription)) {
									try {
										productRec.setDescription(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setDescription error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseCollectUserNum)) {
									try {
										productRec
												.setCollectUserNum(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setDescription error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseAddTime)) {
									try {
										productRec
												.setAddTime(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setAddTime error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseViewNum)) {
									try {
										productRec
												.setViewNum(Long
														.parseLong(reader
																.nextString()));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setViewNum error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseUserId)) {
									try {
										productRec.setUserId(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setUserId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseIsView)) {
									try {
										productRec.setIsView(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setIsView error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseRealName)) {
									try {
										productRec.setRealName(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setRealName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseIsFav)) {
									try {
										productRec.setInFav(reader.nextString()
												.equalsIgnoreCase("1"));
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setTagCatId error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseTags)) {
									reader.beginArray();
									List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										while (reader.hasNext()) {
											String tagsName = reader.nextName();
											KeyWordsRec rec = new KeyWordsRec();
											if (tagsName
													.equalsIgnoreCase(KeyWordsRec.ResponseTagId)) {
												try {
													rec.setTagID(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setTagID error",
															e);
												}
											} else if (tagsName
													.equalsIgnoreCase(KeyWordsRec.ResponseTagName)) {
												try {
													rec.setTagName(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setTagName error",
															e);
												}
											} else {
												reader.skipValue();
											}
											recs.add(rec);
										}
										reader.endObject();
									}
									reader.endArray();
									productRec.setTags(recs);
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseName)) {
									try {
										productRec.setName(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setName error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsThumb)) {
									try {
										productRec
												.setThumb(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setThumb error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
									try {
										productRec.setImg(reader.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setImg error", e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseOriginalImg)) {
									try {
										productRec.setOriginalImg(reader
												.nextString());
									} catch (Exception e) {

										reader.skipValue();
										Log.w(TAG, currMethod
												+ "parse setOriginalImg error",
												e);
									}
								} else if (subTagName
										.equalsIgnoreCase(ProductRec.ResponseGallery)) {
									if (ErrorCodeStant.getInstance().isSucceed(
											response.getErrorCode())) {
										reader.beginArray();
										List<PicRec> list = new ArrayList<PicRec>();
										while (reader.hasNext()) {
											reader.beginObject();
											PicRec pictRec = new PicRec();
											boolean Record = false;
											while (reader.hasNext()) {
												Record = true;
												String subTagPicName = reader
														.nextName();
												if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgId)) {
													try {
														pictRec.setId(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicId error",
																e);
													}

												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseThumb)) {

													try {
														pictRec.setThumb(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicThumb error",
																e);
													}
												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgUrl)) {

													try {
														pictRec.setUrl(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicUrl error",
																e);
													}
												} else if (subTagPicName
														.equalsIgnoreCase(PicRec.ResponseImgDescription)) {

													try {
														pictRec.setDescription(reader
																.nextString());

													} catch (Exception e) {

														reader.skipValue();
														Log.w(TAG,
																currMethod
																		+ "parse setPicDescription error",
																e);
													}
												} else {
													reader.skipValue();
												}

											}
											reader.endObject();
											if (Record) {
												list.add(pictRec);
											}
										}
										reader.endArray();
										productRec.setPicList(list);
									}

								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
							if (hasRecord) {
								plist.add(productRec);

							}
						}
						reader.endArray();
						response.setProductRecs(plist);

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseGetMyAttentionTags(String json) {
		ResponseGetAttentionTag response = new ResponseGetAttentionTag();
		final String currMethod = "parseGetMyAttentionTags:";
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(ResponseGetAttentionTag.ResponseCat)) {
								try {
									reader.beginArray();
									List<CategoryRec> categoryRecList = new ArrayList<CategoryRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										CategoryRec categoryRec = new CategoryRec();
										boolean hasRecord = false;
										while (reader.hasNext()) {
											hasRecord = true;
											String catTagName = reader
													.nextName();
											if (catTagName
													.equalsIgnoreCase(CategoryRec.ResponseCatId)) {
												try {
													categoryRec.setId(reader
															.nextString());
												} catch (Exception e) {
													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse catid error",
															e);
												}

											} else if (catTagName
													.equalsIgnoreCase(CategoryRec.ResponseCatName)) {
												try {
													categoryRec.setName(reader
															.nextString());
												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse catname error",
															e);
												}

											} else if (catTagName
													.equalsIgnoreCase(CategoryRec.ResponseParentId)) {
												try {
													categoryRec
															.setParentId(reader
																	.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse ParentId error",
															e);
												}

											} else if (subTagName
													.equalsIgnoreCase(CategoryRec.ResponseIsFollow)) {
												try {
													categoryRec
															.setFollowed(reader
																	.nextBoolean());
												} catch (Exception e) {
													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse isFollowed error",
															e);
												}
											} else {
												reader.skipValue();
											}
										}
										if (hasRecord)
											categoryRecList.add(categoryRec);
										reader.endObject();
									}
									response.setCategoryRecList(categoryRecList);
									reader.endArray();
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse cat error.",
											e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ResponseGetAttentionTag.ResponseTag)) {
								try {
									reader.beginArray();
									List<TagRec> tagRecList = new ArrayList<TagRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										TagRec tagRec = new TagRec();
										boolean hasRecord = false;
										while (reader.hasNext()) {
											hasRecord = true;
											String attentionTagName = reader
													.nextName();
											if (attentionTagName
													.equalsIgnoreCase(TagRec.ResponseId)) {
												try {
													tagRec.setId(reader
															.nextString());
												} catch (Exception e) {
													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse tagId error",
															e);
												}

											} else if (attentionTagName
													.equalsIgnoreCase(TagRec.ResponseName)) {
												try {
													tagRec.setName(reader
															.nextString());
												} catch (Exception e) {
													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse tagName error",
															e);
												}

											} else if (attentionTagName
													.equalsIgnoreCase(TagRec.ResponseWeight)) {
												try {
													tagRec.setWeight(Long.parseLong(reader
															.nextString()));
												} catch (Exception e) {
													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse weight error",
															e);
												}
											} else {
												reader.skipValue();
											}
										}
										if (hasRecord)
											tagRecList.add(tagRec);
										reader.endObject();
									}
									response.setTagRecList(tagRecList);
									reader.endArray();
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse tag error.",
											e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseAddTag(String json) {
		ResponseAddTag response = new ResponseAddTag();
		final String currMethod = "parseAddTag:";
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						TagRec rec = new TagRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							hasRecord = true;
							if (subTagName.equalsIgnoreCase(TagRec.ResponseId)) {
								try {
									rec.setId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse id error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(TagRec.ResponseName)) {
								try {
									rec.setName(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod + "parse name error",
											e);
								}
							} else if (subTagName
									.equalsIgnoreCase(TagRec.ResponseWeight)) {
								try {
									rec.setWeight(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse weight error", e);
								}
							} else {
								reader.skipValue();
							}
						}
						if (hasRecord)
							response.setTagRec(rec);
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseRemoveTag(String json) {
		ResponseRemoveTag response = new ResponseRemoveTag();
		final String currMethod = "parseRemoveTag:";
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(ResponseRemoveTag.ResponseFollowId)) {
								try {
									response.setFollowId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse followId error", e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseAddAttentionTags(String json) {
		return this.parseRemoveTag(json);
	}

	public BaseResponse parseAttention(String json) {
		return this.parseRemoveTag(json);
	}

	public BaseResponse parseSearchProduct(String json) {
		final String currMethod = "parseSearchProduct:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseSearchProduct response = new ResponseSearchProduct();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginArray();
						List<ProductRec> plist = new ArrayList<ProductRec>();

						while (reader.hasNext()) {
							reader.beginObject();
							ProductRec productRec = new ProductRec();
							boolean hasRecord = false;
							// while (reader.hasNext()) {
							// hasRecord = true;
							// String subTagName = reader.nextName();
							// if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
							// try {
							// productRec.setId(reader.nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse ID error", e);
							// }
							//
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseUserId)) {
							// try {
							// productRec.setUserId(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse UserId error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsUserName))
							// {
							// try {
							// productRec.setUserName(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse UserId error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseCatName)) {
							// try {
							// productRec.setCatName(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse setCatName error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseCatId)) {
							// try {
							// productRec
							// .setCatId(reader.nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse CatId error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsSn)) {
							// try {
							// productRec.setSn(reader.nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse Sn error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseName)) {
							// try {
							// productRec.setName(reader.nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse Name error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseNameStyle))
							// {
							// try {
							// productRec.setNameStyle(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse NameStyle error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseClickCount))
							// {
							// try {
							// productRec.setClickCount(Long
							// .valueOf(reader.nextString()));
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse ClickCount error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseBrandId)) {
							// try {
							// productRec.setBrandId(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse BrandId error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseProviderName))
							// {
							// try {
							// productRec.setProviderName(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse ProviderName error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsNumber))
							// {
							// try {
							// productRec.setNumber(Long
							// .valueOf(reader.nextString()));
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse Number error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsWeight))
							// {
							// try {
							// productRec.setWeight(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse Number error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseMarketPrice))
							// {
							// try {
							// productRec.setMarketPrice(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse MarketPrice error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseShopPrice))
							// {
							// try {
							// productRec.setShopPrice(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse ShopPrice error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponsePromotePrice))
							// {
							// try {
							// productRec.setPromotePrice(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse PromotePrice error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsBrief))
							// {
							// try {
							// productRec
							// .setBrief(reader.nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse Brief error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsDescription))
							// {
							// try {
							// productRec.setDescription(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse Description error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsThumb))
							// {
							// try {
							// productRec
							// .setThumb(reader.nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse Thumb error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
							// try {
							// productRec.setImg(reader.nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse Img error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseOriginalImg))
							// {
							// try {
							// productRec.setOriginalImg(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse setOriginalImg error",
							// e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseIsFav)) {
							// try {
							// productRec.setInFav(reader
							// .nextBoolean());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse setOriginalImg error",
							// e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseAddTime)) {
							// try {
							// productRec.setAddTime(Long
							// .valueOf(reader.nextString()));
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse setAddTime error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseSuppliesId))
							// {
							// try {
							// productRec.setSuppliersId(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse setSuppliersId error",
							// e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseFavNum)) {
							// try {
							// productRec.setFavNum(Integer
							// .valueOf(reader.nextString()));
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse setFavNum error", e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseIsOnSale)) {
							// try {
							// productRec
							// .setOnSale(reader.nextString()
							// .equals("0") ? false
							// : true);
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG, currMethod
							// + "parse setSuppliersId error",
							// e);
							// }
							// } else if (subTagName
							// .equalsIgnoreCase(ProductRec.ResponseGallery)) {
							// if (ErrorCodeStant.getInstance().isSucceed(
							// response.getErrorCode())) {
							// reader.beginArray();
							// List<PicRec> list = new ArrayList<PicRec>();
							// while (reader.hasNext()) {
							// reader.beginObject();
							// PicRec pictRec = new PicRec();
							// boolean Record = false;
							// while (reader.hasNext()) {
							// Record = true;
							// String subTagPicName = reader
							// .nextName();
							// if (subTagPicName
							// .equalsIgnoreCase(PicRec.ResponseImgId)) {
							// try {
							// pictRec.setId(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG,
							// currMethod
							// + "parse setPicId error",
							// e);
							// }
							//
							// } else if (subTagPicName
							// .equalsIgnoreCase(PicRec.ResponseThumb)) {
							//
							// try {
							// pictRec.setThumb(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG,
							// currMethod
							// + "parse setPicThumb error",
							// e);
							// }
							// } else if (subTagPicName
							// .equalsIgnoreCase(PicRec.ResponseImgUrl)) {
							//
							// try {
							// pictRec.setUrl(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG,
							// currMethod
							// + "parse setPicUrl error",
							// e);
							// }
							// } else if (subTagPicName
							// .equalsIgnoreCase(PicRec.ResponseImgDescription))
							// {
							//
							// try {
							// pictRec.setDescription(reader
							// .nextString());
							//
							// } catch (Exception e) {
							//
							// reader.skipValue();
							// Log.w(TAG,
							// currMethod
							// + "parse setPicDescription error",
							// e);
							// }
							// } else {
							// reader.skipValue();
							// }
							//
							// }
							// reader.endObject();
							// if (Record) {
							// list.add(pictRec);
							// }
							// }
							// reader.endArray();
							// productRec.setPicList(list);
							// }
							//
							// } else {
							// reader.skipValue();
							// }
							// }
							reader.endObject();
							if (hasRecord) {
								plist.add(productRec);

							}
						}
						reader.endArray();
						response.setProductRecs(plist);

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseKeyWords(String json) {
		final String currMethod = "parseRemovePic:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseKeyWords response = new ResponseKeyWords();
		try {
			JSONObject jsonObject = new JSONObject(json)
					.getJSONObject(BaseResponse.ResponseErrors);

			response.setErrorCode(jsonObject
					.getInt(BaseResponse.ResponseErrorCode));
			response.setMessage(BaseResponse.ResponseMessage);

			JSONObject object = new JSONObject(json)
					.getJSONObject(BaseResponse.ResponseResult);
			response.setIshavekey(object.getString("ishavekey"));

			JSONArray jsonArray = object.getJSONArray("list");
			List<KeyWordsRec> list = new ArrayList<KeyWordsRec>();
			for (int i = 0; i < jsonArray.length(); i++) {
				KeyWordsRec rec = new KeyWordsRec();
				JSONObject rec_object = jsonArray.getJSONObject(i);
				rec.setTagID(rec_object.getString(KeyWordsRec.ResponseTagId));
				rec.setTagName(rec_object
						.getString(KeyWordsRec.ResponseTagName));
				rec.setUseNum(rec_object.getString(KeyWordsRec.ResponseUseNum));
				list.add(rec);
			}

			response.setList(list);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// JsonReader reader = new JsonReader(new StringReader(json));
		// reader.beginObject();
		// while (reader.hasNext()) {
		// String tagName = reader.nextName();
		// if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
		// reader.beginObject();
		// while (reader.hasNext()) {
		// String subTagName = reader.nextName();
		// if (subTagName
		// .equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
		// int errorCode = reader.nextInt();
		// response.setErrorCode(errorCode);
		// } else if (subTagName
		// .equalsIgnoreCase(BaseResponse.ResponseMessage)) {
		// response.setMessage(reader.nextString());
		// } else {
		// reader.skipValue();
		// }
		// }
		// reader.endObject();
		// } else if (tagName
		// .equalsIgnoreCase(BaseResponse.ResponseResult)) {
		//
		// if (ErrorCodeStant.getInstance().isSucceed(
		// response.getErrorCode())) {
		// reader.beginArray();
		// List<KeyWordsRec> list = new ArrayList<KeyWordsRec>();
		// while (reader.hasNext()) {
		// reader.beginObject();
		//
		// KeyWordsRec rec = new KeyWordsRec();
		// while (reader.hasNext()) {
		// String subName = reader.nextName();
		// if (subName
		// .equalsIgnoreCase(KeyWordsRec.ResponseTagId)) {
		// rec.setTagID(reader.nextString());
		// } else if (subName
		// .equalsIgnoreCase(KeyWordsRec.ResponseTagName)) {
		// rec.setTagName(reader.nextString());
		// } else if (subName
		// .equalsIgnoreCase(KeyWordsRec.ResponseUseNum)) {
		// rec.setUseNum(reader.nextString());
		// } else {
		// reader.skipValue();
		// }
		// }
		// reader.endObject();
		// list.add(rec);
		// }
		// reader.endArray();
		// response.setList(list);
		//
		// } else {
		// reader.skipValue();
		// }
		// } else {
		// reader.skipValue();
		// }
		// }
		// reader.endObject();
		// reader.close();
		// } catch (Exception e) {
		// Log.e(TAG, currMethod + ",JsonParseException", e);
		// response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
		// response.setMessage("Json Parse Error.");
		// }
		return response;
	}

	public BaseResponse parseTagCategory(String json) {
		final String currMethod = "parseRemovePic:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseTagCategory response = new ResponseTagCategory();
		try {
			JSONObject jsonObject = new JSONObject(json)
					.getJSONObject(BaseResponse.ResponseErrors);

			response.setErrorCode(jsonObject
					.getInt(BaseResponse.ResponseErrorCode));
			response.setMessage(BaseResponse.ResponseMessage);

			JSONObject object = new JSONObject(json)
					.getJSONObject(BaseResponse.ResponseResult);
			response.setIshavekey(object.getString("ishavekey"));

			JSONArray jsonArray = object.getJSONArray("list");
			List<TagCategoryRec> list = new ArrayList<TagCategoryRec>();
			for (int i = 0; i < jsonArray.length(); i++) {
				TagCategoryRec rec = new TagCategoryRec();
				JSONObject rec_object = jsonArray.getJSONObject(i);
				rec.setTagCatID(rec_object
						.getString(TagCategoryRec.ResponseTagCatId));
				rec.setTagCatName(rec_object
						.getString(TagCategoryRec.ResponseTagCatName));
				rec.setUseNum(rec_object
						.getString(TagCategoryRec.ResponseUseNum));
				list.add(rec);
			}

			response.setList(list);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// JsonReader reader = new JsonReader(new StringReader(json));
		// reader.beginObject();
		// while (reader.hasNext()) {
		// String tagName = reader.nextName();
		// if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
		// reader.beginObject();
		// while (reader.hasNext()) {
		// String subTagName = reader.nextName();
		// if (subTagName
		// .equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
		// int errorCode = reader.nextInt();
		// response.setErrorCode(errorCode);
		// } else if (subTagName
		// .equalsIgnoreCase(BaseResponse.ResponseMessage)) {
		// response.setMessage(reader.nextString());
		// } else {
		// reader.skipValue();
		// }
		// }
		// reader.endObject();
		// } else if (tagName
		// .equalsIgnoreCase(BaseResponse.ResponseResult)) {
		//
		// if (ErrorCodeStant.getInstance().isSucceed(
		// response.getErrorCode())) {
		// reader.beginArray();
		// List<TagCategoryRec> list = new ArrayList<TagCategoryRec>();
		// while (reader.hasNext()) {
		// reader.beginObject();
		// TagCategoryRec rec = new TagCategoryRec();
		// while (reader.hasNext()) {
		// String subName = reader.nextName();
		// if (subName
		// .equalsIgnoreCase(TagCategoryRec.ResponseTagCatId)) {
		// rec.setTagCatID(reader.nextString());
		// } else if (subName
		// .equalsIgnoreCase(TagCategoryRec.ResponseTagCatName)) {
		// rec.setTagCatName(reader.nextString());
		// } else if (subName
		// .equalsIgnoreCase(TagCategoryRec.ResponseUseNum)) {
		// rec.setUseNum(reader.nextString());
		// } else {
		// reader.skipValue();
		// }
		// }
		// reader.endObject();
		// list.add(rec);
		// }
		// reader.endArray();
		// response.setList(list);
		//
		// } else {
		// reader.skipValue();
		// }
		// } else {
		// reader.skipValue();
		// }
		// }
		// reader.endObject();
		// reader.close();
		// } catch (Exception e) {
		// Log.e(TAG, currMethod + ",JsonParseException", e);
		// response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
		// response.setMessage("Json Parse Error.");
		// }
		return response;
	}

	public BaseResponse parseAddKeyWords(String json) {
		BaseResponse response = new ResponseFavPost();
		final String currMethod = "parseAddKeyWords:";

		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			Log.e(TAG, "beginObject");
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public ResponseGetMatchNum parseGetMatchNum(String json) {
		ResponseGetMatchNum response = new ResponseGetMatchNum();
		final String currMethod = "parseGetMatchNum:";

		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						while (reader.hasNext()) {
							String subName = reader.nextName();
							if (subName
									.equalsIgnoreCase(ResponseGetMatchNum.ResponseMatchNum)) {
								response.setMatchNum(reader.nextString());
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();

					} else {
						reader.skipValue();
					}
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parseReport(String json) {
		BaseResponse response = new BaseResponse();
		final String currMethod = "parseReport:";

		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			Log.e(TAG, "beginObject");
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	public BaseResponse parsePraiseAndBlack(String json) {
		ResponsePraiseAndBlack response = new ResponsePraiseAndBlack();
		final String currMethod = "parseAddFav:";

		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			Log.e(TAG, "beginObject");
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(ResponsePraiseAndBlack.KEY_B_ID)) {
								try {
									response.setbId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse fid error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(ResponsePraiseAndBlack.KEY_USER_ID)) {
								try {
									response.setUserId(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse fid error.",
											e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;
	}

	// 获取中意信息详情
	public BaseResponse parseGetProductDetail(String json) {
		final String currMethod = "parseGetProductDetail:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseGetProductDetail response = new ResponseGetProductDetail();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						ProductRec productRec = new ProductRec();
						boolean hasRecord = false;
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							hasRecord = true;
							if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsId)) {
								try {
									productRec.setId(reader.nextString());

								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG,
											currMethod + "parse setId error", e);
								}

							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTagCatId)) {
								try {
									productRec.setTagCatId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTagCatName)) {
								try {
									productRec.setTagCatName(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatName error", e);
								}
							}  else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsDescription)) {
								try {
									productRec.setDescription(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setDescription error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseCollectUserNum)) {
								try {
									productRec.setCollectUserNum(Long
											.parseLong(reader.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setDescription error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseAddTime)) {
								try {
									productRec.setAddTime(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setAddTime error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.START_TIME)) {
								try {
									productRec.setStart_time(Long
											.parseLong(reader.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setAddTime error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.END_TIME)) {
								try {
									productRec.setEnd_time(Long
											.parseLong(reader.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setAddTime error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.PRAISE_NUM)) {
								try {
									productRec.setPraise_num(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setAddTime error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.BLACK_NUM)) {
								try {
									productRec
											.setBlack_num(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setAddTime error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseUserIsBlack)) {
								try {
									String isBlack = reader.nextString();
									if (isBlack.equals("1")) {
										productRec.setBlackUser(true);
									} else {
										productRec.setBlackUser(false);
									}

								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setBlackUser error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseViewNum)) {
								try {
									productRec.setViewNum(Long.parseLong(reader
											.nextString()));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setViewNum error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseUserId)) {
								try {
									productRec.setUserId(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setUserId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseRealName)) {
								try {
									productRec.setRealName(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setRealName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseIsFav)) {
								try {
									productRec.setInFav(reader.nextString()
											.equalsIgnoreCase("1"));
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setTagCatId error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseTags)) {
								reader.beginArray();
								List<KeyWordsRec> recs = new ArrayList<KeyWordsRec>();
								while (reader.hasNext()) {
									reader.beginObject();
									while (reader.hasNext()) {
										String tagsName = reader.nextName();
										KeyWordsRec rec = new KeyWordsRec();
										if (tagsName
												.equalsIgnoreCase(KeyWordsRec.ResponseTagId)) {
											try {
												rec.setTagID(reader
														.nextString());
											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setTagID error",
														e);
											}
										} else if (tagsName
												.equalsIgnoreCase(KeyWordsRec.ResponseTagName)) {
											try {
												rec.setTagName(reader
														.nextString());
											} catch (Exception e) {

												reader.skipValue();
												Log.w(TAG,
														currMethod
																+ "parse setTagName error",
														e);
											}
										} else {
											reader.skipValue();
										}
										recs.add(rec);
									}
									reader.endObject();
								}
								reader.endArray();
								productRec.setTags(recs);
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseName)) {
								try {
									productRec.setName(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setName error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsThumb)) {
								try {
									productRec.setThumb(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setThumb error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGoodsImg)) {
								try {
									productRec.setImg(reader.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setImg error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseOriginalImg)) {
								try {
									productRec.setOriginalImg(reader
											.nextString());
								} catch (Exception e) {

									reader.skipValue();
									Log.w(TAG, currMethod
											+ "parse setOriginalImg error", e);
								}
							} else if (subTagName
									.equalsIgnoreCase(ProductRec.ResponseGallery)) {
								if (ErrorCodeStant.getInstance().isSucceed(
										response.getErrorCode())) {
									reader.beginArray();
									List<PicRec> list = new ArrayList<PicRec>();
									while (reader.hasNext()) {
										reader.beginObject();
										PicRec pictRec = new PicRec();
										boolean Record = false;
										while (reader.hasNext()) {
											Record = true;
											String subTagPicName = reader
													.nextName();
											if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgId)) {
												try {
													pictRec.setId(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicId error",
															e);
												}

											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseThumb)) {

												try {
													pictRec.setThumb(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicThumb error",
															e);
												}
											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgUrl)) {

												try {
													pictRec.setUrl(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicUrl error",
															e);
												}
											} else if (subTagPicName
													.equalsIgnoreCase(PicRec.ResponseImgDescription)) {

												try {
													pictRec.setDescription(reader
															.nextString());

												} catch (Exception e) {

													reader.skipValue();
													Log.w(TAG,
															currMethod
																	+ "parse setPicDescription error",
															e);
												}
											} else {
												reader.skipValue();
											}

										}
										reader.endObject();
										if (Record) {
											list.add(pictRec);
										}
									}
									reader.endArray();
									productRec.setPicList(list);
								}

							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
						response.setProductRec(productRec);

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;

	}

	// 获取企业信息
	public BaseResponse parseGetEnterpriseInfo(String json) {
		final String currMethod = "parseGetEnterpriseInfo:";
		Trace.d(TAG, currMethod + "json->" + json);
		ResponseEnterpriseInfo response = new ResponseEnterpriseInfo();
		try {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equalsIgnoreCase(BaseResponse.ResponseErrors)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subTagName = reader.nextName();
						if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseErrorCode)) {
							int errorCode = reader.nextInt();
							response.setErrorCode(errorCode);
						} else if (subTagName
								.equalsIgnoreCase(BaseResponse.ResponseMessage)) {
							response.setMessage(reader.nextString());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tagName
						.equalsIgnoreCase(BaseResponse.ResponseResult)) {

					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						reader.beginObject();
						EnterpriseItemRec rec = new EnterpriseItemRec();
						while (reader.hasNext()) {
							String subTagName = reader.nextName();
							if (subTagName
									.equalsIgnoreCase(EnterpriseItemRec.EID)) {
								try {
									rec.setEid(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse fid error.",
											e);
								}

							} else if (subTagName
									.equalsIgnoreCase(EnterpriseItemRec.ELOGO)) {
								try {
									rec.setElogo(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse fid error.",
											e);
								}
							} else if (subTagName
									.equalsIgnoreCase(EnterpriseItemRec.ENAME)) {
								try {
									rec.setEname(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse fid error.",
											e);
								}
							} else if (subTagName
									.equalsIgnoreCase(EnterpriseItemRec.INDUSTRY)) {
								try {
									rec.setIndustry(reader.nextString());
								} catch (Exception e) {
									reader.skipValue();
									Log.e(TAG, currMethod + "parse fid error.",
											e);
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
						response.setEnterpriseInfo(rec);

					} else {

						reader.skipValue();
					}

				} else {
					reader.skipValue();
				}

			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, currMethod + ",JsonParseException", e);
			response.setErrorCode(ErrorCodeStant.ErrorCodeForJsonParseError);
			response.setMessage("Json Parse Error.");
		}
		return response;

	}
}