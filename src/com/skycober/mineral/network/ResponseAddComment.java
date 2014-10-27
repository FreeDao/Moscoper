package com.skycober.mineral.network;

import com.skycober.mineral.bean.CommentRec;

public class ResponseAddComment extends BaseResponse {
	private CommentRec commentRec;

	public CommentRec getCommentRec() {
		return commentRec;
	}

	public void setCommentRec(CommentRec commentRec) {
		this.commentRec = commentRec;
	}

	@Override
	public String toString() {
		return "ResponseAddComment [commentRec=" + commentRec + ", toString()="
				+ super.toString() + "]";
	}
	
}
