package com.skycober.mineral.constant;

/**
 * 所有请求的url
 * 
 * @author Yes366
 * 
 */
public class RequestUrls {
	/**
	 * 服务器基地址
	 */

	// public static final String SERVER_BASIC_URL =
	// "http://moscoper.auauo.com:88";
	// public static final String SERVER_BASIC_URL =
	// "http://moscoper.godcoder.com:8080";
	// 运营服务器
	public static final String SERVER_BASIC_URL = "http://net.moscoper.com";
	// 测试服务器
	// public static final String SERVER_BASIC_URL =
	// "http://182.92.109.35:8081";
	// // http://moscoper.godcoder.com

	public static final String BLACK_LIST_URL = "/api/2/follow/myBlack/offset/[offset]/count/[count]";

	public static final String UPDATAAPK_URL = "/api/2/system/version";

	// 获取融云的用户Token
	public static final String URI = "https://api.cn.rong.io/user/getToken.json";
	// 首页企业列表
	public static final String HOMEPAGE_COMPANY_LIST_URL = "/api/2/enterprise/indexList";

	// 条形码识别
	public static final String TIAOXING_CODE_URL = "http://upccenter.com/apit/moscoper/[code]?key=4360fab59afabca3867dc81e857c1a3f";
	// 企业信息
	public static final String COMANY_ATTENTION_INFOS = "/api/2/enterprise/list";// 已关注的企业列表
	public static final String COMPANY_LIST = "/api/2/enterprise/inlist/offset/[offset]/count/[count]";// 入住企业列表
	public static final String COMPANY_SEARCH_LIST = "/api/2/enterprise/inlist/search/";// 搜索入住企业列表

	public static final String COMPANY_SEARCH_TAG_LIST = "/api/2/enterprise/tagList/eid/[eid]/search/";// 搜索企业标签

	public static final String COMPANY_TAGS = "/api/2/enterprise/tagList/eid/"; // 企业标签，eid是企业id
	public static final String COMPANY_INFO = "/api/2/enterprise/view/eid/";// 企业信息
	public static final String COMPANY_ADD_ATTENTION_TAG = "/api/2/enterprise/attention";// 添加关注
	public static final String COMPANY_CANNCEL_ATTENTION = "/api/2/enterprise/deleteAttention?eid=";

	/**
	 * 账户类
	 */
	public static final String REGIST_USER_URL = "/api/2/user/register";
	public static final String OAUTH_URL = "/api/2/user/login";
	public static final String GET_USER_INFO = "/api/2/user/view/[user_id]";
	public static final String UPDATE_USER_INFO = "/api/2/user/update";
	public static final String UPDATE_PWD_URL = "/api/2/user/updatePassword";
	public static final String UPLOAD_USER_AVATAR_URL = "/api/2/user/avatar";
	/**
	 * 收藏品类
	 */
	public static final String GET_CATEGORY = "/api/2/category/all/[cat_id]";
	public static final String GET_CATEGORY_NOID = "/api/2/category/all";
	/**
	 * 商品类
	 */
	public static final String GET_ALL_GOODS_BY_ID = "/api/2/goods/list/type/publish/count/[count]/offset/[offset]";
	public static final String ADD_FAV_URL = "/api/2/goods/addFav/[goods_id]";
	public static final String ADD_VIEW_URL = "/api/2/goods/addViewCount/goods_id/[goods_id]";
	public static final String REPORT_URL = "/api/2/report/add";
	public static final String PRAISE_URL = "/api/2/follow/praise/user_id/[user_id]";
	public static final String BLACK_URL = "/api/2/follow/black/user_id/[user_id]";
	public static final String NOT_Black_URL = "/api/2/follow/notBlack/user_id/[user_id]";
	public static final String CANCEL_FAV_URL = "/api/2/goods/cancelFav/goods_id/[goods_id]";
	public static final String GET_MY_FAV_LIST_URL = "/api/2/goods/myFavs/offset/[offset]/count/[count]";
	public static final String GET_MY_ATTENTION_LIST_URL = "/api/2/goods/myWantList/offset/[offset]/count/[count]";
	public static final String GET_PRODUCT_DETAIL_URL = "/api/2/goods/view/goods_id/[goods_id]";

	// 我有列表
	public static final String GET_USERS_GOODS_LIST_URL = "/api/2/goods/myList/type/publish/offset/[offset]/count/[count]";
	public static final String GET_RANDOM_GOODS_URL = "/api/2/goods/random";
	public static final String MAKE_ON_SALE = "/api/2/goods/makeOnSale/[goods_id]";
	public static final String MAKE_OFF_SALE = "/api/2/goods/makeOffSale/[goods_id]";
	public static final String ADD_PRODUCT_URL = "/api/2/goods/addResource";
	public static final String UPDATE_PRODUCT_URL = "/api/2/goods/update/[goods_id]";
	public static final String UPLOAD_PRODUCT_PIC_URL = "/api/2/goods/uploadGallery/{goods_id}";
	public static final String REMOVE_PRODUCT_URL = "/api/2/goods/delete/[goods_id]";
	public static final String REMOVE_ATTENTION_URL = "/api/2/goods/delWant/goods_ids/[goods_id]";

	public static final String GET_SINGLE_PRODUCT_URL = "/api/2/goods/view/goods_id/[goods_id]";

	public static final String GET_COMMENT_LIST_URL = "/api/2/goods/comments/[goods_id]/offset/[offset]/count/[count]";
	public static final String ADD_COMMENT_URL = "/api/2/goods/addComment/[goods_id]";

	public static final String REMOVE_PIC_URL = "/api/2/goods/delGallery/{img_id}";
	public static final String GET_PRODUCT_PIC_LIST_URL = "/api/2/goods/gallerys/[goods_id]";

	public static final String GET_MY_ATTENTION_TAGS_URL = "/api/2/follow/myFollowList/type/[type]/offset/[offset]/count/[count]";
	public static final String ADD_MY_ATTENTION_TAGS_URL = "/api/2/follow/addFollow/type/[type]/follow_id/[follow_id]";
	public static final String ADD_TAG_URL = "/api/2/follow/addTag";
	public static final String REMOVE_TAG_URL = "/api/2/follow/cancelFollow/type/[type]/follow_id/[follow_id]";
	public static final String GET_ALL_GOODS_BY_TAG_ID = "/api/2/follow/goodsByTag/{tag_id}/offset/[offset]/count/[count]";
	// public static final String GET_KEY_WORDS =
	// "/api/2/tag/list/tag_cat_id/[tag_cat_id]/offset/[offset]/count/[count]";
	public static final String GET_KEY_WORDS = "/api/2/tag/list/tag_cat_id/[tag_cat_id]/offset/[offset]/count/[count]";

	// 获取标签分类
	public static final String GET_TAG_CATEGORY = "/api/2/tagCategory/list/type/[type]/offset/[offset]/count/[count]";
	public static final String SEARCH_PRODUCT_URL = "/api/2/goods/searchByKeyword/{keyword}/offset/[offset]/count/[count]";
	// 我要列表
	public static final String GET_ALL_KEY_WORDS = "/api/2/goods/myList/type/find/count/[count]/offset/[offset]";
	// 添加新标签(我要)
	public static final String ADD_NEW_KEY_WORDS = "/api/2/goods/addNeed";
	// 编辑我要
	public static final String EDIT_KEY_WORDS = "/api/2/goods/updateNeed";
	// 添加一次浏览
	public static final String ADD_VIEW_COUNT = "/api/2/goods/addViewCount/goods_id/[goods_id]";
	// 更换logo
	public static final String UPLOAD_IMAGE = "/api/2/goods/uploadImage/[goods_id]";
	// 更换logo
	public static final String UPDATE_RESOURCE = "/api/2/goods/updateResource";
	// 获取匹配数量
	public static final String GET_MATCH_NUM = "/api/2/goods/getMatchNum/match_percent/[match_percent]";
	// 关注企业
	public static final String ATTENTION = "/api/2/enterprise/attention";
	// 获取唯一设备标示
	public static final String DEV_IDENTIFY = "/api/2/system/dev/dev_identify/[dev_identify]";
	// 获取唯一设备标示
	public static final String ENTERPRISE = "/api/2/enterprise/view/eid/[eid]";

}
