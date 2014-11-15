package com.skycober.mineral.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DBHelper helper;

	public DBManager(Context context) {
		helper = new DBHelper(context);
	}

	public void intsert(String table, ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.insert(table, null, values);
	}

	public Cursor query(String table, String selection, String[] selectionArgs,
			String limit) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(table, null, selection, selectionArgs, null,
				null, null, limit);

		return cursor;
	}

	public void delete(String table, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(table, whereClause, whereArgs);
	}

}
