package com.skycober.mineral.product;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycober.mineral.R;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.CalendarUtil;
import com.skycober.mineral.util.StringUtil;
/**
 * 信息详情适配器
 * @author Yes366
 *
 */
public class ProductAdapter extends BaseAdapter {

	private int layoutId;
	private List<ProductRec> prodList;
	private LayoutInflater inflater;
	public int offset = 0;
	public int count = 20;
	private FinalBitmap logo;
	Context context;
	private OnItemClickListener onItemClickListener, addFavClickListener,
			subFavClickListener, reportClickListener;

	public OnItemClickListener getReportClickListener() {
		return reportClickListener;
	}

	public void setReportClickListener(OnItemClickListener reportClickListener) {
		this.reportClickListener = reportClickListener;
	}

	private OnItemEmptyListener onItemEmptyListener;

	public OnItemClickListener getAddFavClickListener() {
		return addFavClickListener;
	}

	public void setAddFavClickListener(OnItemClickListener addFavClickListener) {
		this.addFavClickListener = addFavClickListener;
	}

	public OnItemClickListener getSubFavClickListener() {
		return subFavClickListener;
	}

	public void setSubFavClickListener(OnItemClickListener subFavClickListener) {
		this.subFavClickListener = subFavClickListener;
	}

	public OnItemEmptyListener getOnItemEmptyListener() {
		return onItemEmptyListener;
	}

	public void setOnItemEmptyListener(OnItemEmptyListener onItemEmptyListener) {
		this.onItemEmptyListener = onItemEmptyListener;
	}

	public ProductAdapter(Context context, int layoutId,
			List<ProductRec> prodList) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.prodList = prodList;
		this.layoutId = layoutId;
		logo = FinalBitmap.create(context);
		logo.configCalculateBitmapSizeWhenDecode(false);
		logo.configLoadingImage(R.drawable.default_mineral);
	}

	public List<ProductRec> getProdList() {
		return prodList;
	}

	public void setProdList(List<ProductRec> prodList) {
		if (null == this.prodList) {
			this.prodList = prodList;
		} else {
			this.prodList.clear();
			if (null != prodList && prodList.size() > 0) {
				this.prodList.addAll(prodList);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		int size = this.prodList.size();
		if (null != onItemEmptyListener) {
			if (size > 0) {
				onItemEmptyListener.onItemNotEmpty();
			} else {
				onItemEmptyListener.onItemEmpty();
			}
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		return prodList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = inflater.inflate(layoutId, null);

		}
		// 藏品logo
		ImageView ivLogo = (ImageView) convertView
				.findViewById(R.id.product_avatar);
		// 藏品名称
		TextView tvName = (TextView) convertView
				.findViewById(R.id.category_product_name);
		// 藏品描述
		TextView tvDescription = (TextView) convertView
				.findViewById(R.id.category_product_description);
		// 藏品包含图片数
		TextView tvPicCount = (TextView) convertView
				.findViewById(R.id.category_product_photo);
		// 藏品被收藏数
		TextView tvFavCount = (TextView) convertView
				.findViewById(R.id.category_product_liked);
		// 藏品浏览数
		final TextView tvVisitCount = (TextView) convertView
				.findViewById(R.id.category_product_viewed);
		// 藏品发布日期
		TextView tvSendDate = (TextView) convertView
				.findViewById(R.id.category_product_send_date);
		//收藏
		Button Fav = (Button) convertView
				.findViewById(R.id.category_product_follow);
		//举报
		Button report = (Button) convertView
				.findViewById(R.id.report_product_follow);

		final ProductRec rec = prodList.get(position);
		//举报
		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reportClickListener.onItemClick(rec, position);
			}
		});
		if (rec.isInFav()) {
			Fav.setText("取消收藏");
			Fav.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					subFavClickListener.onItemClick(rec, position);

				}
			});
		} else {
			Fav.setText("收藏");
			Fav.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					addFavClickListener.onItemClick(rec, position);
				}
			});
		}

		tvName.setText(rec.getName());
		tvDescription.setText(rec.getDescription());
		String avatar = rec.getThumb();
		if (!StringUtil.getInstance().IsEmpty(avatar)) {
			String url = RequestUrls.SERVER_BASIC_URL + "/" + avatar;
			logo.display(ivLogo, url);
			logo.configLoadfailImage(R.drawable.mineral_logo);
		} else {
			ivLogo.setImageResource(R.drawable.mineral_logo);
		}
		List<PicRec> picList = rec.getPicList();
		int picCount = null == picList ? 0 : picList.size();
		tvPicCount.setText(String.valueOf(picCount));
		tvFavCount.setText(String.valueOf(rec.getCollectUserNum()));
		tvVisitCount.setText(String.valueOf(rec.getViewNum()));

		long sendDate = rec.getAddTime();
		tvSendDate.setText(CalendarUtil.GetPostCommentTime(sendDate * 1000l));
		//点击进入信息详情
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemClickListener.onItemClick(rec, position);
				rec.setViewNum(rec.getViewNum()+1);
				tvVisitCount.setText(""+rec.getViewNum());
			}
		});
		return convertView;
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public interface OnItemClickListener {
		void onItemClick(ProductRec rec, int pos);
	}

	public interface OnItemEmptyListener {
		void onItemEmpty();

		void onItemNotEmpty();
	}
}