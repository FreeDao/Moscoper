package com.skycober.mineral.product;

import java.util.Comparator;

import com.skycober.mineral.bean.TagCategoryRec;

public class PinyinComparator1 implements Comparator<TagCategoryRec> {

	@Override
	public int compare(TagCategoryRec o1, TagCategoryRec o2) {
		// TODO Auto-generated method stub

		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
