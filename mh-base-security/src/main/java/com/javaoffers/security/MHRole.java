package com.javaoffers.security;

import com.javaoffers.base.pojo.BasePojo;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色
 */
@Entity(name = "mh_role")
@Getter
@Setter
public class MHRole extends BasePojo implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "role_id" ,columnDefinition="int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id'")
    private Integer roleId;

    @Column(name = "role_name",columnDefinition="varchar(60) not null comment '角色名'" )
    private String roleName;//角色名称

    @Column(name = "create_user_id",columnDefinition="int(11) not null comment '创建者Id'")
    private Integer createUserId;//创建者Id

    @Column(name = "role_discribe" , columnDefinition="varchar(300) comment '角色的描述信息'")
    private String roleDiscribe;//角色描述信息

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
