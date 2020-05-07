package com.mh.base.mq.exception;

public class MHMQException extends Exception{

	private String error;

	public MHMQException(String error) {
		super(error);
		this.error = error;
	}
	
}
