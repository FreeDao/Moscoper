package com.skycober.mineral.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
/**
 * ��̬����listview�߶ȵĹ�����
 * @author Yes366
 *
 */
public class Utility {
	public static void setListViewHeightBasedOnChildren(
			ExpandableListView listView, BaseExpandableListAdapter adapter) {
		// ��ȡListView��Ӧ��Adapter
		if (adapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = adapter.getGroupCount(); i < len; i++) { // listAdapter.getCount()�������������Ŀ
			View listItem = adapter.getGroupView(i, false, null, listView);
			listItem.measure(0, 0); // ��������View �Ŀ��
			totalHeight += listItem.getMeasuredHeight(); // ͳ������������ܸ߶�
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
		// listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�
		// params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�
		listView.setLayoutParams(params);
	}
}
