package com.mh.security;

import com.mh.base.pojo.BasePojo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * create by cmj
 * 用户和角色对应的中间表（用户和角色是多对多关系）
 */
@Getter
@Setter
@Entity(name = "mh_user_role")
public class MHUserRole  implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "user_role_id",columnDefinition = "int(11) not null auto_increment comment '主键id'")
    private Integer userRoleId;

    @Column(name = "user_id" , columnDefinition = "int(11) not null comment '用户Id'")
    private String userId;

    @Column(name = "role_id",columnDefinition = "varchar(11) not null comment '角色Id'")
    private String roleId;

    @Column(name = "create_time",columnDefinition="datetime comment '创建时间'")
    private Date createTime;//创建时间

    @Column(name = "update_time", columnDefinition = "datetime comment '更新时间'")
    private Date updateTime;//更新时间

    @Column(name = "base_user1",columnDefinition = "varchar(300) comment '备用字段1'")
    private String baseUse1;

    @Column(name = "base_user2",columnDefinition = "varchar(300) comment '备用字段2'")
    private String baseUse2;

    @Column(name = "base_user3",columnDefinition = "varchar(300) comment '备用字段3'")
    private String baseUse3;


}
