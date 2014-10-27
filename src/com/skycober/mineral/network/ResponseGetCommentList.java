package com.skycober.mineral.network;

import com.skycober.mineral.bean.CommentRec;

import java.util.List;

public class ResponseGetCommentList extends BaseResponse {
	
	private List<CommentRec> commentList;

	public List<CommentRec> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<CommentRec> commentList) {
		this.commentList = commentList;
	}
	
}
