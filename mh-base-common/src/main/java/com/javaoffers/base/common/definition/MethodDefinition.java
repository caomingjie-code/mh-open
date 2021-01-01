package com.javaoffers.base.common.definition;

import java.util.List;
import java.util.Map;

import jdk.internal.org.objectweb.asm.tree.*;
/**
 * 方法定义
 * @author mj
 *
 */
public class MethodDefinition implements Definition {
	
	private MethodNode methodNode = null;
	private String methodName = null;
	private List<String> methodAnnotationNames = null;
	private Map<String,AnnotationNode>  methodAnnotations = null;//K 为方法的名称，V 为方法的所有注解对象
	private ClassDefinition classDefinition;//关联所属类定义
	
	
	public ClassDefinition getClassDefinition() {
		return classDefinition;
	}
	public void setClassDefinition(ClassDefinition classDefinition) {
		if(this.classDefinition != null) {
			return ;
		}
		this.classDefinition = classDefinition;
	}
	public MethodNode getMethodNode() {
		return methodNode;
	}
	public void setMethodNode(MethodNode methodNode) {
		if(this.methodNode!=null) {
			return ;
		}
		this.methodNode = methodNode;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		if(this.methodName != null) {
			return;
		}
		this.methodName = methodName;
	}
	public List<String> getMethodAnnotationNames() {
		return methodAnnotationNames;
	}
	public void setMethodAnnotationNames(List<String> methodAnnotationNames) {
		if(this.methodAnnotationNames != null) {
			return ;
		}
		this.methodAnnotationNames = methodAnnotationNames;
	}
	public Map<String, AnnotationNode> getMethodAnnotations() {
		return methodAnnotations;
	}
	public void setMethodAnnotations(Map<String, AnnotationNode> methodAnnotations) {
		if(this.methodAnnotations != null) {
			return ;
		}
		this.methodAnnotations = methodAnnotations;
	}
	
	
	
}
