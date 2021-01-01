package com.javaoffers.base.common.log;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
public class BaseLog {
	public static final Level DEBUG = Level.DEBUG;

	public static final Level INFO = Level.INFO;

	public static final Level WARN = Level.WARN;

	public static final Level ERROR = Level.ERROR;

	public static final Level FATAL = Level.FATAL;
	
	//static  Logger logger = LoggerFactory.getLogger("");
	public static final Log logger = LogFactory.getLog(BaseLog.class);
	
	public static void printLog(String log,Level level){
	    
		if(level.equals(INFO)){
			logger.info(log);
		}
		if(level.equals(DEBUG)){
			logger.debug(log);
		}
		if(level.equals(WARN)){
			logger.warn(log);
		}
		if(level.equals(ERROR)){
			logger.error(log);
		}
		if(level.equals(FATAL)){
			logger.fatal(log);
		}
	}
	
   public static void printLog(String log){
	   printLog( log,INFO);
	}
	
}
