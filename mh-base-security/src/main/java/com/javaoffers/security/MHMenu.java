package com.javaoffers.security;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * create by cmj
 * 菜单表
 */
@Getter
@Setter
@Entity(name = "mh_menu")
public class MHMenu  implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "menu_id",columnDefinition = "int(11) not null auto_increment comment '菜单主键'")
    private Integer menuId;//菜单Id

    @Column(name = "menu_name" ,columnDefinition = "varchar(30) not null comment '菜单名称，最大长度为10个中文'")
    private String menuName;//菜单名称

    @Column(name="menu_url" ,columnDefinition="varchar(200) comment '菜单的url，最大长度为200英文字符'")
    private String menuUrl;//菜单的url

    @Column(name = "menu_icon",columnDefinition = "varchar(200) comment '菜单图标'")
    private String menuIcon;//菜单图标

    @Column(name = "menu_type",columnDefinition = "int(1) not null comment '菜单类型：0 目录菜单，1 页面链接菜单，2 按钮菜单'")
    private Integer menuType;//菜单类型：0 目录菜单，1 页面链接菜单，2 按钮菜单

    @Column(name = "menu_parent_id",columnDefinition = "int(11) comment '父级菜单id'")
    private Integer menuParentId;//父级菜单

    @Column(name = "menu_status",columnDefinition = "int(1) not null comment '菜单状态 ： 0 停止（隐藏），1 使用（显示）'")
    private Integer menuStatus;//菜单状态 ： 0 停止（隐藏），1 使用（显示）

    @Column(name = "create_user_id",columnDefinition = "int(11) comment '菜单的创建者id(通常为开发者或admin)'")
    private Integer createUserId;//菜单的创建者id(通常为开发者或admin)


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
