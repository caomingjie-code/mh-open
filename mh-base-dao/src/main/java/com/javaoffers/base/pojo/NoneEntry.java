package com.javaoffers.base.pojo;

import com.javaoffers.base.common.annotation.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 该表为一张空表，为了mh-dao 随意使用
 * @Auther: create by cmj on 2021/2/21 19:47
 */
@BaseModel
@Getter
@Setter
@Entity(name = "brower_none")
public class NoneEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "none_id", columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id'")
    private Integer noneId;
}
