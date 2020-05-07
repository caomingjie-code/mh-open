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
 * create By cmj
 *  角色与菜单中间表
 *  角色与菜单是多对多的关系
 */
@Entity(name = "mh_role_menu")
@Getter
@Setter
public class MHRoleMenu  implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "role_menu_id",columnDefinition = "int(11) not null auto_increment comment '主键'")
    private Integer roleMenuId;//主键id

    @Column(name = "role_id",columnDefinition = "int(11) not null comment '角色id'")
    private Integer roleId;//角色Id

    @Column(name = "menu_id" ,columnDefinition = "int(11) not null comment '菜单id'")
    private Integer menuId; //菜单id

    @Column(name = "menu_status" ,columnDefinition = "int(1) not null default 1 comment '菜单状态 ： 0隐藏，1 显示 （主要作用于当前分配的role） 默认为 1'")
    private Integer menuStatus;//菜单状态 ： 0隐藏，1 显示 （主要作用于当前分配的role）

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
