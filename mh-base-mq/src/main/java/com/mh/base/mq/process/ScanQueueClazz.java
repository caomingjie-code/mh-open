package com.mh.base.mq.process;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import com.mh.base.mq.exception.MHMQException;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.*;
/**
 *  扫描queue 注解
 *  
 * @author cmj
 *
 */
@SuppressWarnings({ "restriction", "unused" })
public class ScanQueueClazz {
	//存放所有队列名称
	private static final ConcurrentHashMap<String, Class> queues = new ConcurrentHashMap<String, Class>();
	private static final ConcurrentHashMap<String, Class> listenerQueues = new ConcurrentHashMap<String, Class>();
	private static final ConcurrentHashMap<String, QueueBeanDefinition> listenerQueueMethods = new ConcurrentHashMap<String, QueueBeanDefinition>();//key: 类名+方法拼接
    private static final String QUEUE_ANNOTATION_NAME = "com.mh.base.mq.annotation.CreateQueue";
    private static final String LISTENER_QUEUE_ANNOTATION_NAME = "com.mh.base.mq.annotation.ListenerQueue";
	static {
    	try {
    		String path = ScanQueueClazz.class.getResource("/").getPath();//获取classpath路径
        	List<File> list = getAllClassFile(new File( path ));//此方法为获取classpath路径下所有的class文件（所有包）
        	for(File f: list) {//便利每一个class文件
    			//getClassName(f);
    			 ClassReader reader = new ClassReader(new FileInputStream(f));
    			 ClassNode cn = new ClassNode();//创建ClassNode,读取的信息会封装到这个类里面
    	         reader.accept(cn, 0);//开始读取
    	         List<AnnotationNode> annotations = cn.visibleAnnotations;//获取声明的所有注解
    	         List<MethodNode> methods = cn.methods;
    	         if(methods!=null&&methods.size()>0) {
    	        	 for( MethodNode method: methods) {
    	        		 List<AnnotationNode> visibleAnnotations = method.visibleAnnotations;//获取方法上的注解
    	        		 if(visibleAnnotations != null) {
    	        			 for(AnnotationNode mt:visibleAnnotations) {
    	                 		//获取注解的描述信息
    	                 		 String anno = mt.desc.replaceAll("/", ".");
    	                 		 String annoName = anno.substring(1, anno.length()-1);
    	                 		 if(LISTENER_QUEUE_ANNOTATION_NAME.equals(annoName)) { 
    	                 			 
    	                 			 String className = cn.name.replaceAll("/", ".");
    	                  			//获取注解的属性名对应的值，（values是一个集合，它将注解的属性和属性值都放在了values中，通常奇数为值偶数为属性名）
    	          		        	 String valu = mt.values.get(1).toString();
    	          		        	 System.out.println(className);
    	          		        	 System.out.println(valu);
    	          		        	 //判断是否已经存在
    	          		        	 String methodName = method.name;
    	          		        	 String nn = method.desc.substring(2, method.desc.length()-3).replaceAll("/", ".");
    	          		        	 Class<?> beanClazz = Class.forName(className);
    	          		        	 Class<?> methodParamClazz = Class.forName(nn);
    	          		        	 String key = className+"_"+methodName;
    	          		        	 Method listenerMethod = beanClazz.getDeclaredMethod(methodName, methodParamClazz); //监听的方法
    	          		        	 /**
    	          		        	  * private Class beanClazz;
										private String listenerQueueName;
										private Method listenerMehod;// 监听的方法
										private Class<Object> queueDataClazz  ; //队列数据类型,默认为Object类型，不允许其他类型
    	          		        	  */
    	          		        	QueueBeanDefinition beanDefinition = new QueueBeanDefinition(beanClazz, preparBuild(valu), listenerMethod, Object.class);
    	          		        	listenerQueueMethods.put(key, beanDefinition);
    	          		        	
    	                 		 }
    	                 	 }
    	        		 }
        	         }
    	         }
    	         
    	         if(annotations!=null) {//便利注解
                	 for(AnnotationNode an: annotations) {
                		 //获取注解的描述信息
                		 String anno = an.desc.replaceAll("/", ".");
                		 String annoName = anno.substring(1, anno.length()-1);
                		 if(QUEUE_ANNOTATION_NAME.equals(annoName)) {
                			 String className = cn.name.replaceAll("/", ".");
                			//获取注解的属性名对应的值，（values是一个集合，它将注解的属性和属性值都放在了values中，通常奇数为值偶数为属性名）
        		        	 String valu = an.values.get(1).toString();
        		        	 System.out.println(className);
        		        	 System.out.println(valu);
        		        	 //判断是否已经存在
        		        	 Class class1 = queues.get(valu);
        		        	 if(class1!=null) {
        		        		 throw new MHMQException("在注解 CreateQueue 的使用过程中，发现存在相同的name 值："+valu);
        		        	 }if(StringUtils.isNoneBlank(valu)) {
        		        		 queues.put(valu, Class.forName(className)) ;//根据匹配的注解，将其封装给具体的业务使用
        		        	 }
        		        	 
                		 }
    		         }
                	
                 }
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * 预处理
	 * @param valu
	 * @return
	 */
	public static String preparBuild(String valu) {
		String a = valu;
		if(a.contains("[")||a.contains("]")) {
			System.out.println(true);
			a = a.replace("[", "");
			a = a.replace("]", "");
			return a;
		}
		
		return valu;
	}
	
	public static ConcurrentHashMap<String, QueueBeanDefinition> getlistenerQueueMethods(){
		return listenerQueueMethods;
	}
	/**
	 * 获取所有的class 文件
	 * @param file
	 * @return
	 */
	public static List<File> getAllClassFile(File file ) {
		List<File> list =  new ArrayList<File>();
		if(file==null) {
			return list;
		}
		File[] listFiles = file.listFiles();
		if(listFiles!=null&&listFiles.length>0) {
			for(File f : listFiles) {
			    if(!f.isDirectory()&&f.getName().endsWith("class")) {
			    	list.add(f);
				}
			    if(f.isDirectory()) {
			    	List<File> allClassFile = getAllClassFile(f);
			    	list.addAll(allClassFile);
			    }
			}
		}
		return list;
	}
	
	
	public static ConcurrentHashMap<String, Class>  getQueues(){
		return queues;
	}
	
	public static ConcurrentHashMap<String, Class>  getListenerQueues(){
		return listenerQueues;
	}
	
	public static class QueueBeanDefinition{
		private Class beanClazz;
		private String listenerQueueName;
		private Method listenerMehod;// 监听的方法
		private Class<Object> queueDataClazz  ; //队列数据类型,默认为Object类型，不允许其他类型
		
		public Class getBeanClazz() {
			return beanClazz;
		}
		public void setBeanClazz(Class beanClazz) {
			this.beanClazz = beanClazz;
		}
		public String getListenerQueueName() {
			return listenerQueueName;
		}
		public void setListenerQueueName(String listenerQueueName) {
			this.listenerQueueName = listenerQueueName;
		}
		public Method getListenerMehod() {
			return listenerMehod;
		}
		public void setListenerMehod(Method listenerMehod) {
			this.listenerMehod = listenerMehod;
		}
		public Class<Object> getQueueDataClazz() {
			return queueDataClazz;
		}
		public void setQueueDataClazz(Class<Object> queueDataClazz) {
			this.queueDataClazz = queueDataClazz;
		}
		public QueueBeanDefinition(Class beanClazz, String listenerQueueName, Method listenerMehod,
				Class<Object> queueDataClazz) {
			super();
			this.beanClazz = beanClazz;
			this.listenerQueueName = listenerQueueName;
			this.listenerMehod = listenerMehod;
			this.queueDataClazz = queueDataClazz;
		}
	}
	
	
	
	
	
	
}
