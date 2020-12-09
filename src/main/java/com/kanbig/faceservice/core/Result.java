package com.kanbig.faceservice.core;

import java.io.Serializable;

/**
 * Controller返回对象
 *
 * @author TanZhen
 * @since 2018年10月19日 下午6:55:57
 */
public class Result implements Serializable {
	private static final long serialVersionUID = 9023057198146675331L;

	private boolean success;
	private int code;
	private String errorMsg;
	private Object data;

	public static Result success() {
		return new Result();
	}

	public static Result success(Object data) {
		return new Result(data);
	}

	public static Result error(String errorMsg) {
		return new Result(500, errorMsg);
	}

	public static Result error(int code, String errorMsg) {
		return new Result(code, errorMsg);
	}

	/**
	 * 失败
	 *
	 * @param errorCode 错误码
	 * @param error     错误信息
	 */
	public Result(int errorCode, String error) {
		this(false, errorCode, error, "");
	}

	/**
	 * 成功
	 */
	public Result() {
		this(true, 0, "", "");
	}

	/**
	 * 成功
	 *
	 * @param data 返回数据
	 */
	public Result(Object data) {
		this(true, 0, "", data);
	}

	public Result(boolean success, int code, String errorMsg, Object data) {
		this.success = success;
		this.code = code;
		this.errorMsg = errorMsg;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
