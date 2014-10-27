package com.skycober.mineral.bean;

import java.io.Serializable;
import java.util.List;

public class KeyWordsListRec implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3447597910398764665L;
	private List<KeyWordsRec> recs;

	public List<KeyWordsRec> getRecs() {
		return recs;
	}

	public void setRecs(List<KeyWordsRec> recs) {
		this.recs = recs;
	}
}
