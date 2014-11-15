package com.skycober.mineral.db;

import android.content.Context;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "happyKitchen.db", null, 2,
				new DefaultDatabaseErrorHandler());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql1 = "create table collect (tagCatId Integer primary key ,"
				+ "tagCatName verchar(128))";
		db.execSQL(sql1);
		//
		// String sql2 =
		// "create table category (id Integer primary key autoincrement,"
		// + "name verchar(128),imageurl verchar(128),"
		// + "typeid verchar(32),type verchar(32))";
		// db.execSQL(sql2);
		//
		// String sql3 =
		// "create table tag (id Integer primary key autoincrement,"
		// + "name verchar(32),tagid verchar(32),"
		// + "categoryname verchar(32),type verchar(32))";
		// db.execSQL(sql3);
		//
		// String slq4 =
		// "create table kitchenbook (id Integer primary key autoincrement,"
		// + "username verchar(32),title verchar(32),"
		// + "url verchar(128),imageurl verchar(128),"
		// + "content verchar(512),type verchar(32))";
		// db.execSQL(slq4);
		//
		// String aql5 =
		// "create table user (id Integer primary key autoincrement ,"
		// + "username verchar(32),password verchar(32))";
		// db.execSQL(aql5);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion < newVersion) {
			System.out.println("========onUpgrade==========");
			// String sql = "ALTER TABLE collect RENAME TO __temp__collect";
			//
			// db.execSQL(sql);
		}
	}

}
