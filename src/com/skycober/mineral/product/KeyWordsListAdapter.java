package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.skycober.mineral.R;
import com.skycober.mineral.bean.KeyWordsRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.product.KeyWordsAdapter.OnItemDelectLongClick;
import com.skycober.mineral.util.CalendarUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Util;
/**
 * ¹Ø¼ü×ÖµÄÊÊÅäÆ÷
 * 
 * @author Yes366
 *
 */
public class KeyWordsListAdapter extends BaseAdapter {
	private Context context;
	private List<ProductRec> list;
	private OnDelectItemClick onDelectItemClick;//É¾³ý
	private OnEditItemClick onEditItem;//±à¼­
	
	
	
	public OnEditItemClick getOnEditItem() {
		return onEditItem;
	}

	public void setOnEditItem(OnEditItemClick onEditItem) {
		this.onEditItem = onEditItem;
	}

	public OnDelectItemClick getOnDelectItemClick() {
		return onDelectItemClick;
	}

	public void setOnDelectItemClick(OnDelectItemClick onDelectItemClick) {
		this.onDelectItemClick = onDelectItemClick;
	}

	public List<ProductRec> getList() {
		return list;
	}

	public void setList(List<ProductRec> list) {
		this.list = list;
	}
	private LayoutInflater inflater;

	public KeyWordsListAdapter(List<ProductRec> list, Context context) {
		// TODO Auto-generated constructor stub
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public ProductRec getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
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
			convertView = inflater
					.inflate(R.layout.adapter_keywords_list, null);
		}
		TextView key_words_name = (TextView) convertView
				.findViewById(R.id.key_words_name);
		key_words_name.setText(list.get(position).getTagCatName());
		TextView date = (TextView) convertView
				.findViewById(R.id.date);
		date.setText(CalendarUtil.GetPostCommentTime(list.get(position).getAddTime() * 1000l));
		GridView keywords_gridview=(GridView)convertView.findViewById(R.id.keywords_gridview);
//		keywords_gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				System.out.println("===keywords_gridview==="+position);
//				return false;
//			}
//		});
		List<String> tags=new ArrayList<String>();
		List<KeyWordsRec> recs=list.get(position).getTags();
		for (int i = 0; i < recs.size(); i++) {
			String tag=recs.get(i).getTagName();
			if (!StringUtil.getInstance().IsEmpty(tag)) {
				tags.add(tag);

			}
		}
		
		//±à¼­
		
		ImageView edit = (ImageView) convertView.findViewById(R.id.needEdit);
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onEditItem.onClick(list.get(position));
			}
		});
		
		//É¾³ý
		convertView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				onDelectItemClick.onClick(list.get(position).getId(),position);
				return false;
			}
		});
//		Button delect=(Button)convertView.findViewById(R.id.delect);
//		delect.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				onDelectItemClick.onClick(list.get(position).getId(),position);
//			}
//		});
		KeyWordsAdapter adapter=new KeyWordsAdapter(tags, context);
		adapter.setOnItemDelectLongClick(new OnItemDelectLongClick() {
			
			@Override
			public void OnItemLongClick(int pos) {
				// TODO Auto-generated method stub
				
			}
		});
		keywords_gridview.setAdapter(adapter);
		setGridViewHeightBasedOnChildren(keywords_gridview);
		return convertView;
	}
	public void setGridViewHeightBasedOnChildren(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) gridView
				.getLayoutParams();
		int count = listAdapter.getCount() % 4 == 0 ? listAdapter.getCount() / 4
				: listAdapter.getCount() / 4 + 1;
		params.height = (Util.dip2px(context, 60) * (count));
		gridView.setLayoutParams(params);
	}
	public interface OnDelectItemClick{
		void onClick(String id,int pos);
	}
	
	public interface OnEditItemClick{
		void onClick(ProductRec productRec);
	}
	
}
