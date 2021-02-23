/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.repository.config;

import com.javaoffers.base.common.log.LogUtils;
import com.javaoffers.base.dao.BaseBatis;
import com.javaoffers.base.dao.BaseBatisJpa;
import com.javaoffers.base.dao.BaseJPA;
import com.javaoffers.base.dao.BaseJpaBatis;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Value object for a discovered Repository fragment interface.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 * @author caomingjie 修改 2021-02-22
 * @since 2.1
 */
@RequiredArgsConstructor
public class FragmentMetadata {

    private final MetadataReaderFactory factory;

    public FragmentMetadata() {
        this.factory = null;
        LogUtils.printLog("update FragmentMetadata for base dao");
    }

    /**
     * 曹明杰修改
     * Returns all interfaces to be considered fragment ones for the given source interface.
     *
     * @param interfaceName must not be {@literal null} or empty.
     * @return
     */
    public Stream<String> getFragmentInterfaces(String interfaceName) {

        Assert.hasText(interfaceName, "Interface name must not be null or empty!");
        String[] interfaceNames = getClassMetadata(interfaceName).getInterfaceNames();
        if(interfaceName!=null&&interfaceName.length()>0){
            List<String> list = CollectionUtils.arrayToList(interfaceNames);
            HashSet<String> set = new HashSet<>(list);
            for(String clazzName : list ){
                if(BaseBatisJpa.class.getName().equals(clazzName)|| BaseJpaBatis.class.getName().equals(clazzName)){
                    set.add(BaseBatis.class.getName());
                    set.add(BaseJPA.class.getName());
                    set.remove(clazzName);
                }
            }
            interfaceNames = set.toArray(new String[set.size()]);
        }
        return Arrays.stream(interfaceNames) //
                .filter(this::isCandidate);
    }

    /**
     * Returns whether the given interface is a fragment candidate.
     *
     * @param interfaceName must not be {@literal null} or empty.
     * @return
     */
    private boolean isCandidate(String interfaceName) {

        Assert.hasText(interfaceName, "Interface name must not be null or empty!");

        AnnotationMetadata metadata = getAnnotationMetadata(interfaceName);
        return !metadata.hasAnnotation(NoRepositoryBean.class.getName());

    }

    private AnnotationMetadata getAnnotationMetadata(String className) {

        try {
            return factory.getMetadataReader(className).getAnnotationMetadata();
        } catch (IOException e) {
            throw new BeanDefinitionStoreException(String.format("Cannot parse %s metadata.", className), e);
        }
    }

    private ClassMetadata getClassMetadata(String className) {

        try {
            return factory.getMetadataReader(className).getClassMetadata();
        } catch (IOException e) {
            throw new BeanDefinitionStoreException(String.format("Cannot parse %s metadata.", className), e);
        }
    }
}
