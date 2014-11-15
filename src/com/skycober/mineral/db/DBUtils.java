package com.skycober.mineral.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.skycober.mineral.bean.ProductRec;

public class DBUtils {
	private DBManager dbManager;

	public DBUtils(Context context) {
		dbManager = new DBManager(context);
	}

	public List<ProductRec> query() {
		List<ProductRec> productRecList = null;

		Cursor cursor = dbManager.query("collect", null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			productRecList = new ArrayList<ProductRec>();
			while (cursor.moveToNext()) {
				ProductRec productRec = new ProductRec();
				productRec.setTagCatId(cursor.getString(cursor
						.getColumnIndex("tagCatId")));
				productRec.setTagCatName(cursor.getString(cursor
						.getColumnIndex("tagCatName")));
				productRecList.add(productRec);
			}
		}
		return productRecList;

	}

	public boolean insert(ProductRec productRec) {
		ContentValues values = new ContentValues();
		values.put("tagCatId", productRec.getTagCatId());
		values.put("tagCatName", productRec.getTagCatName());
		dbManager.intsert("collect", values);
		return true;
	}
}
