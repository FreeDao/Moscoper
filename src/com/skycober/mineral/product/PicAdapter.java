package com.skycober.mineral.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.skycober.mineral.R;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
/**
 * Õº∆¨¡–±Ìµƒ  ≈‰∆˜
 * @author Yes366
 *
 */
public class PicAdapter extends BaseAdapter {
	private List<PicRec> picList;
	private int layoutId;
	private LayoutInflater inflater;
	private boolean isSupportAdd = true;

	private Map<String, Bitmap> cacheMap = new HashMap<String, Bitmap>();
	public PicAdapter(Context context, int layoutId, List<PicRec> picList, boolean isSupportAdd){
		inflater = LayoutInflater.from(context);
		this.layoutId = layoutId;
		this.picList = picList;
		this.isSupportAdd = isSupportAdd;
	}
	
	
	public List<PicRec> getPicList() {
		return picList;
	}
	public void setPicList(List<PicRec> picList) {
		if(null == this.picList){
			this.picList = picList;
		}else{
			this.picList.clear();
			if(null != picList && picList.size() > 0){
				this.picList.addAll(picList);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return picList.size();
	}

	@Override
	public Object getItem(int position) {
		return picList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(null == convertView){
			convertView = inflater.inflate(layoutId, null);
		}
		ImageView ivPic = (ImageView) convertView.findViewById(R.id.ivPic);

		if(isSupportAdd && position == 0){
			ivPic.setImageResource(R.drawable.gallary_add_btn);
		}else{
			PicRec mRec = picList.get(position);
			String path = mRec.getThumb();
			String url = null;
			if(!StringUtil.getInstance().IsEmpty(path)){
				url = RequestUrls.SERVER_BASIC_URL + "/" +path;
				ivPic.setTag(url);
				if(cacheMap.containsKey(url)){
					ivPic.setImageBitmap(cacheMap.get(url));
				}else{
					ivPic.setImageResource(R.drawable.dynamic_default_icon);
					PicAsynTask task = new PicAsynTask(url, cacheMap, ivPic);
					task.execute(url);
				}
			}else{
				ivPic.setTag(null);
			}
		}
		return convertView;
	}

	class PicAsynTask extends AsyncTask<String, Integer, Bitmap>{
		private String url;
		private Map<String, Bitmap> cacheMap;
		private ImageView ivPic;
		public PicAsynTask(String url, Map<String, Bitmap> cacheMap, ImageView ivPic){
			this.url = url;
			this.cacheMap = cacheMap;
			this.ivPic = ivPic;
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			try {
				bitmap = SettingUtil.getBitmapFromUrl(this.url);
			} catch (Exception e) {
				Log.e("PicAsynTask", "Decode pic async task error:", e);
			}
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if(null != result){
				Object obj = ivPic.getTag();
				if(null != obj){
					String currUrl = obj.toString();
					if(currUrl.equalsIgnoreCase(url)){
						ivPic.setImageBitmap(result);
					}
				}
				cacheMap.put(url, result);
			}
			super.onPostExecute(result);
		}
	}
	
	public void clearCache(){
		if(null != cacheMap && cacheMap.size() > 0){
			for (String key : cacheMap.keySet()) {
				String url = key;
				Bitmap bitmap = cacheMap.get(url);
				if(null != bitmap && !bitmap.isRecycled()){
					bitmap.recycle();
					System.gc();
				}
				bitmap = null;
			} 
			cacheMap.clear();
		}
		cacheMap = null;
	}

}
