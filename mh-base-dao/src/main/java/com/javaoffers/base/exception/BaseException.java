package com.javaoffers.base.exception;

public class BaseException extends Exception{

	private String error;

	public BaseException(String error) {
		super(error);
		this.error = error;
	}
	
	
}
