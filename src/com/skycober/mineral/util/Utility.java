package com.skycober.mineral.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
/**
 * 动态设置listview高度的工具类
 * @author Yes366
 *
 */
public class Utility {
	public static void setListViewHeightBasedOnChildren(
			ExpandableListView listView, BaseExpandableListAdapter adapter) {
		// 获取ListView对应的Adapter
		if (adapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = adapter.getGroupCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getGroupView(i, false, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		for (int i = 0, len = adapter.getGroupCount(); i < len; i++) {
			if (listView.isGroupExpanded(i)) {
				View childItem = adapter.getChildView(i, 0, false, null,
						listView);
				childItem.measure(0, 0);
				totalHeight += childItem.getMeasuredHeight();
			}
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getGroupCount() - 1))
				+ 10;
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
}
