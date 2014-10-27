package com.skycober.mineral.product;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.skycober.mineral.R;
/**
 * ¹Ø¼ü×ÖµÄÊÊÅäÆ÷
 * @author Yes366
 *
 */
public class KeyWordsAdapter extends BaseAdapter {
	private List<String> keyWordsList;

	public List<String> getKeyWordsList() {
		return keyWordsList;
	}

	public void setKeyWordsList(List<String> keyWordsList) {
		this.keyWordsList = null;
		this.keyWordsList = keyWordsList;
	}

	private OnItemDelectLongClick onItemDelectLongClick;

	public OnItemDelectLongClick getOnItemDelectLongClick() {
		return onItemDelectLongClick;
	}

	public void setOnItemDelectLongClick(
			OnItemDelectLongClick onItemDelectLongClick) {
		this.onItemDelectLongClick = onItemDelectLongClick;
	}

	private LayoutInflater inflater;

	public KeyWordsAdapter(List<String> keyWordsList, Context context) {
		// TODO Auto-generated constructor stub
		inflater = LayoutInflater.from(context);
		this.keyWordsList = keyWordsList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return keyWordsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return keyWordsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.adapter_key_words, null);
		}

		String keyWords = keyWordsList.get(position);
		TextView tvName = (TextView) convertView
				.findViewById(R.id.key_words_name);
		tvName.setText(keyWords);
		CheckBox keywords_select = (CheckBox) convertView
				.findViewById(R.id.keywords_select);
		keywords_select.setVisibility(View.GONE);
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				onItemDelectLongClick.OnItemLongClick(position);
				return true;			}
		});
		return convertView;
	}

	public interface OnItemDelectLongClick {
		void OnItemLongClick(int pos);
	}
}
