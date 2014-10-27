package com.skycober.mineral.product;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycober.mineral.R;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.CalendarUtil;
import com.skycober.mineral.util.StringUtil;
/**
 * 我的信息适配器
 * @author Yes366
 *
 */
public class MyProductAdapter extends BaseAdapter {
	private int layoutId;
	private List<ProductRec> prodList;
	private LayoutInflater inflater;
	public int offset = 0;
	public int count = 20;
	private FinalBitmap logo;
	private onClick makeOnSaleListenner, makeOffSaleListenner;
	private onItemClickListenner itemClickListenner;
	private OnItemLongClickListener onItemLongClickListener;

	public OnItemLongClickListener getOnItemLongClickListener() {
		return onItemLongClickListener;
	}

	public void setOnItemLongClickListener(
			OnItemLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	public MyProductAdapter(Context context, int layoutId,
			List<ProductRec> prodList) {
		inflater = LayoutInflater.from(context);
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
		return this.prodList.size();
	}

	@Override
	public Object getItem(int position) {
		return prodList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean RemoveItem(ProductRec rec) {
		boolean isSucRemove = false;
		if (null != prodList) {
			isSucRemove = prodList.remove(rec);
			if (isSucRemove)
				notifyDataSetChanged();
		}
		return isSucRemove;
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
				.findViewById(R.id.product_name);
		// 藏品描述
		TextView tvDescription = (TextView) convertView
				.findViewById(R.id.product_description);
		// 藏品包含图片数
		TextView tvPicCount = (TextView) convertView
				.findViewById(R.id.product_photo);
		// 藏品被收藏数
		TextView tvFavCount = (TextView) convertView
				.findViewById(R.id.product_liked);
		// 藏品浏览数
		TextView tvVisitCount = (TextView) convertView
				.findViewById(R.id.product_viewed);
		// 藏品发布日期
		TextView tvSendDate = (TextView) convertView
				.findViewById(R.id.product_send_date);

		final ProductRec rec = prodList.get(position);
		String avatar = rec.getThumb();
		if (!StringUtil.getInstance().IsEmpty(avatar)) {
			String url = RequestUrls.SERVER_BASIC_URL + "/" + avatar;
			logo.display(ivLogo, url);
			logo.configLoadfailImage(R.drawable.mineral_logo);
		} else {
			ivLogo.setImageResource(R.drawable.mineral_logo);
		}
		tvDescription.setText(rec.getDescription());
		tvName.setText(rec.getName());
		List<PicRec> picList = rec.getPicList();
		int picCount = null == picList ? 0 : picList.size();
		tvPicCount.setText(String.valueOf(picCount));
		tvFavCount.setText(String.valueOf(rec.getCollectUserNum()));
		tvVisitCount.setText(String.valueOf(rec.getViewNum()));
		long sendDate = rec.getAddTime();
		tvSendDate.setText(CalendarUtil.GetPostCommentTime(sendDate * 1000l));
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				itemClickListenner.onItemClick(rec);
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onItemLongClickListener.onItemClick(rec.getId());
				return false;
			}
		});

		return convertView;
	}

	public interface onClick {
		void onProdOperationBtnClick(ProductRec rec);
	}

	public interface onItemClickListenner {
		void onItemClick(ProductRec pRec);
	}

	public interface OnItemLongClickListener {
		void onItemClick(String ID);

	}

	public onClick getMakeOnSaleListenner() {
		return makeOnSaleListenner;
	}

	public void setMakeOnSaleListenner(onClick makeOnSaleListenner) {
		this.makeOnSaleListenner = makeOnSaleListenner;
	}

	public onClick getMakeOffSaleListenner() {
		return makeOffSaleListenner;
	}

	public void setMakeOffSaleListenner(onClick makeOffSaleListenner) {
		this.makeOffSaleListenner = makeOffSaleListenner;
	}

	public onItemClickListenner getItemClickListenner() {
		return itemClickListenner;
	}

	public void setItemClickListenner(onItemClickListenner itemClickListenner) {
		this.itemClickListenner = itemClickListenner;
	}
}
