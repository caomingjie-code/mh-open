package com.javaoffers.base.exception;

import java.sql.SQLException;

/**
 * 数据源Exception
 * @author cmj
 *
 */
public class BaseDataSourceException extends SQLException{

	public BaseDataSourceException(String error) {
		super(error);
			}
	public static BaseDataSourceException getException(String msg) {
		return new BaseDataSourceException(msg);
	}

}
