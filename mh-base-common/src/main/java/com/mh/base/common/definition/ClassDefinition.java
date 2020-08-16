package com.mh.base.common.definition;
/**
 * class 描述
 * @author mj
 *
 */

import java.util.List;
import java.util.Map;

import jdk.internal.org.objectweb.asm.tree.AnnotationNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

public class ClassDefinition implements Definition {

	private String targetClazz;//目标类
	private  Map<String,AnnotationNode> targetClazzAnotations;//目标类的注解,K 为注解名称
	private  List<MethodDefinition> methodDefinition;  //类中方法的定义
	private List<FieldDefinition> fieldDefinitions;//属性定义
	private ClassNode classNode;


	public List<FieldDefinition> getFieldDefinitions() {
		return fieldDefinitions;
	}

	public void setFieldDefinitions(List<FieldDefinition> fieldDefinitions) {
		if(this.fieldDefinitions!=null){
			return ;
		}
		this.fieldDefinitions = fieldDefinitions;
	}

	public ClassNode getClassNode() {
		return classNode;
	}

	public void setClassNode(ClassNode classNode) {
		if(this.classNode!=null){
			return ;
		}
		this.classNode = classNode;
	}

	public String getTargetClazz() {
		return targetClazz;
	}
	public void setTargetClazz(String targetClazz) {
		if(this.targetClazz!=null) {
			return;
		}
		this.targetClazz = targetClazz;
	}
	public Map<String, AnnotationNode> getTargetClazzAnotations() {
		return targetClazzAnotations;
	}
	public void setTargetClazzAnotations(Map<String, AnnotationNode> targetClazzAnotations) {
		if(this.targetClazzAnotations != null) {
			return ;
		}
		this.targetClazzAnotations = targetClazzAnotations;
	}
	public List<MethodDefinition> getMethodDefinition() {
		return methodDefinition;
	}
	public void setMethodDefinition(List<MethodDefinition> methodDefinition) {
		if(this.methodDefinition != null) {
			return ;
		}
		this.methodDefinition = methodDefinition;
	}






}
