package com.javaoffers.base.common.clazz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import com.javaoffers.base.common.concurrent.ConcurrentHashSet;
import com.javaoffers.base.common.definition.*;
import com.javaoffers.base.common.log.LogUtils;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.AnnotationNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import org.reflections.Reflections;

/**
 * 类工具
 * @author mj
 *
 */
@SuppressWarnings("restriction")
public class ClassUtils {
	//read only
	private static final Set<ClassDefinition> CLASS_DEFINITIONS = new ConcurrentHashSet<ClassDefinition>();

	static {
		try {
			Collection<URL> urls = ClassUtils.forResource(resourceName("com.javaoffers"), ClassUtils.class.getClassLoader());
			List<File> list = new LinkedList();
			for(URL url : urls){
				String path = url.getPath();
				List<File> ls = getAllClassFile(new File( path ));//此方法为获取classpath路径下所有的class文件（所有包）
				list.addAll(ls);
			}

			//类扫描所有class 在classpath 下
			File classPathFile = new File(ClassUtils.class.getResource("/").getPath());
			List<File> allClassFile = ClassUtils.getAllClassFile(classPathFile);
			if(allClassFile!=null&&allClassFile.size()>0) {
				for(File classFile : allClassFile) {

					ClassDefinition classDefinition = new ClassDefinition();//类定义
					String targetClazz = null;//目标类
					Map<String,AnnotationNode> targetClazzAnotations = new HashMap<String, AnnotationNode>();//目标类的注解,K 为注解名称
					List<MethodDefinition> methodDefinitions = new LinkedList<MethodDefinition>();  //类中方法的定义
					List<FieldDefinition> fieldDefinitions = new LinkedList<>();//类中的fields

					ClassReader classReader = new ClassReader(new FileInputStream(classFile));
					ClassNode classNode = new ClassNode();
					classReader.accept(classNode, 0);
					targetClazz = classNode.name.replaceAll("/", ".");
					List<AnnotationNode> classAnnotations = classNode.visibleAnnotations;

					if(classAnnotations != null) {
						HashMap<String,AnnotationNode> targetClazzAnotations_ = new HashMap<String, AnnotationNode>();
						for(AnnotationNode classAnnotation : classAnnotations) {
							//获取注解的描述信息
							String anno = classAnnotation.desc.replaceAll("/", ".");
							String annoName = anno.substring(1, anno.length()-1);
							targetClazzAnotations_.put(annoName, classAnnotation);
						}
						//不可修改的map
						targetClazzAnotations =  targetClazzAnotations_;
					}

					//初始方法信息
					List<MethodNode> methods = classNode.methods;

					if(methods!=null&&methods.size()>0) {
						for(MethodNode method : methods) {
							MethodDefinition methodDefinition = new MethodDefinition();//方法定义描述
							String methodName = method.name;
							List<String> methodAnnotationNames = new LinkedList<String>();
							Map<String,AnnotationNode> methodAnnotations = new HashMap<String, AnnotationNode>();

							List<AnnotationNode> visibleAnnotations = method.visibleAnnotations;
							if(visibleAnnotations != null) {
								for(AnnotationNode methodAnnotation : visibleAnnotations) {
									String anno = methodAnnotation.desc.replaceAll("/", ".");
									String annoName = anno.substring(1, anno.length()-1);
									methodAnnotationNames.add(annoName);
									methodAnnotations.put(annoName, methodAnnotation);
								}

							}
							methodDefinition.setMethodNode(method);
							methodDefinition.setMethodName(methodName);
							methodDefinition.setMethodAnnotationNames(Collections.unmodifiableList(methodAnnotationNames));
							methodDefinition.setMethodAnnotations(Collections.unmodifiableMap(methodAnnotations));
							methodDefinition.setClassDefinition(classDefinition);
							methodDefinitions.add(methodDefinition);
						}
					}

					//fields
					List<FieldNode> fields = classNode.fields;
					if(fields!=null&&fields.size()>0){
						for(FieldNode fieldNode : fields){
							FieldDefinition fieldDefinition = new FieldDefinition();//属性描述
							String fieldName = fieldNode.name;
							String fieldClassName = fieldNode.desc.replaceAll("/", ".");
							try {
								fieldClassName = fieldClassName.substring(1,fieldClassName.length()-1);
							}catch (Exception e){
								LogUtils.printLog(fieldClassName+"非引用类型－>目标类　"+targetClazz);
							}


							List<String> fieldAnnotationNames = new LinkedList<String>();
							Map<String,AnnotationNode> fieldAnnotations = new HashMap<String, AnnotationNode>();
							List<AnnotationNode> visibleAnnotations = fieldNode.visibleAnnotations;
							if(visibleAnnotations != null) {

								for(AnnotationNode methodAnnotation : visibleAnnotations) {
									String anno = methodAnnotation.desc.replaceAll("/", ".");
									String annoName = anno.substring(1, anno.length()-1);
									fieldAnnotationNames.add(annoName);
									fieldAnnotations.put(annoName, methodAnnotation);
								}

							}
							fieldDefinition.setClassDefinition(classDefinition);
							fieldDefinition.setFieldAnnotationNames(fieldAnnotationNames);
							fieldDefinition.setFieldAnnotations(fieldAnnotations);
							fieldDefinition.setFieldName(fieldName);
							fieldDefinition.setFieldNode(fieldNode);
							fieldDefinition.setFieldClassName(fieldClassName);
							fieldDefinitions.add(fieldDefinition);
						}


					}


					if(targetClazz != null) {
						classDefinition.setClassNode(classNode);//存放classNode
						classDefinition.setTargetClazz(targetClazz);
						classDefinition.setTargetClazzAnotations(Collections.unmodifiableMap(targetClazzAnotations));
						classDefinition.setMethodDefinition(Collections.unmodifiableList(methodDefinitions));
						classDefinition.setFieldDefinitions(fieldDefinitions);
						CLASS_DEFINITIONS.add(classDefinition);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String resourceName(String name) {
		if (name != null) {
			String resourceName = name.replace(".", "/");
			resourceName = resourceName.replace("\\", "/");
			if (resourceName.startsWith("/")) {
				resourceName = resourceName.substring(1);
			}
			return resourceName;
		}
		return null;
	}

	/**
	 * 获取所有的class 文件
	 * @param file
	 * @return
	 */
	private static List<File> getAllClassFile(File file ) {
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

	public static Collection<URL> forResource(String resourceName, ClassLoader... classLoaders) {
		final List<URL> result = new ArrayList<>();
		final ClassLoader[] loaders = classLoaders(classLoaders);
		for (ClassLoader classLoader : loaders) {
			try {
				final Enumeration<URL> urls = classLoader.getResources(resourceName);
				while (urls.hasMoreElements()) {
					final URL url = urls.nextElement();
					int index = url.toExternalForm().lastIndexOf(resourceName);
					if (index != -1) {
						// Add old url as contextUrl to support exotic url handlers
						result.add(new URL(url, url.toExternalForm().substring(0, index)));
					} else {
						result.add(url);
					}
				}
			} catch (IOException e) {
				if (Reflections.log != null) {
					Reflections.log.error("error getting resources for " + resourceName, e);
				}
			}
		}
		return distinctUrls(result);
	}


	public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
		if (classLoaders != null && classLoaders.length != 0) {
			return classLoaders;
		} else {
			ClassLoader contextClassLoader = contextClassLoader(), staticClassLoader = ClassUtils.class.getClassLoader();
			return contextClassLoader != null ?
					staticClassLoader != null && contextClassLoader != staticClassLoader ?
							new ClassLoader[]{contextClassLoader, staticClassLoader} :
							new ClassLoader[]{contextClassLoader} :
					new ClassLoader[] {};

		}
	}

	public static ClassLoader contextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	private static Collection<URL> distinctUrls(Collection<URL> urls) {
		Map<String, URL> distinct = new LinkedHashMap<>(urls.size());
		for (URL url : urls) {
			distinct.put(url.toExternalForm(), url);
		}
		return distinct.values();
	}

	public static final  Set<ClassDefinition> getClassDefinitions() {
		return Collections.unmodifiableSet(CLASS_DEFINITIONS);
	}

}
