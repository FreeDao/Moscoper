package com.skycober.mineral.network;

public class BaseResponse {
	public static final String ResponseErrors = "Errors";
	public static final String ResponseErrorCode = "Code";
	private int errorCode;

	public static final String ResponseMessage = "Message";
	private String message;

	public static final String ResponseResult = "Result";

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "BaseResponse [errorCode=" + errorCode + ", message=" + message
				+ "]";
	}
}
