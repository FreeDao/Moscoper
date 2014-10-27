package com.skycober.mineral.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class KeyWordsRec implements Serializable,Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4067084742661450486L;
	public static final String ResponseTagId = "tag_id";
	private String tagID;
	public static final String ResponseTagName = "tag_name";
	private String tagName;
	public static final String ResponseUseNum = "use_num";
	private String useNum;
	private boolean ischacked;

	public KeyWordsRec(){}
	public KeyWordsRec(String tagID, String tagName, String useNum,
			boolean ischacked) {
		super();
		this.tagID = tagID;
		this.tagName = tagName;
		this.useNum = useNum;
		this.ischacked = ischacked;
	}

	public boolean isIschacked() {
		return ischacked;
	}

	public void setIschacked(boolean ischacked) {
		this.ischacked = ischacked;
	}

	public String getTagID() {
		return tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getUseNum() {
		return useNum;
	}

	public void setUseNum(String useNum) {
		this.useNum = useNum;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(tagID);
		dest.writeString(tagName);
		dest.writeString(useNum);
		dest.writeBooleanArray(new boolean[] { ischacked });
	}

	public static final Parcelable.Creator<KeyWordsRec> creator = new Creator<KeyWordsRec>() {

		@Override
		public KeyWordsRec[] newArray(int size) {
			// TODO Auto-generated method stub
			return new KeyWordsRec[size];
		}

		@Override
		public KeyWordsRec createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			String tagID = source.readString();
			String tagName = source.readString();
			String useNum = source.readString();
			boolean[] isChacked = new boolean[1];
			source.readBooleanArray(isChacked);
			return new KeyWordsRec(source.readString(), source.readString(),
					source.readString(), isChacked[0]);
		}
	};

}
