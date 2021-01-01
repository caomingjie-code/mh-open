package com.javaoffers.base.common.definition;

import jdk.internal.org.objectweb.asm.tree.AnnotationNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import java.util.List;
import java.util.Map;

/**
 * 属性定义
 */
public class FieldDefinition implements Definition{


    private FieldNode fieldNode = null;
    private String fieldName = null;
    private String fieldClassName = null;
    private List<String> fieldAnnotationNames = null;
    private Map<String, AnnotationNode> fieldAnnotations = null;//K 为方法的名称，V 为方法的所有注解对象
    private ClassDefinition classDefinition;//关联所属类定义

    public String getFieldClassName() {
        return fieldClassName;
    }

    public void setFieldClassName(String fieldClassName) {
        if(this.fieldClassName!=null){
            return;
        }
        this.fieldClassName = fieldClassName;
    }

    public FieldNode getFieldNode() {
        return fieldNode;
    }

    public void setFieldNode(FieldNode fieldNode) {
        if(this.fieldNode!=null){
            return;
        }
        this.fieldNode = fieldNode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        if(this.fieldName!=null){
            return;
        }
        this.fieldName = fieldName;
    }

    public List<String> getFieldAnnotationNames() {
        return fieldAnnotationNames;
    }

    public void setFieldAnnotationNames(List<String> fieldAnnotationNames) {
        if(this.fieldAnnotationNames!=null){
            return;
        }
        this.fieldAnnotationNames = fieldAnnotationNames;
    }

    public Map<String, AnnotationNode> getFieldAnnotations() {
        return fieldAnnotations;
    }

    public void setFieldAnnotations(Map<String, AnnotationNode> fieldAnnotations) {
        if(this.fieldAnnotations!=null){
            return;
        }
        this.fieldAnnotations = fieldAnnotations;
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    public void setClassDefinition(ClassDefinition classDefinition) {
        if(this.classDefinition!=null){
            return;
        }
        this.classDefinition = classDefinition;
    }
}
