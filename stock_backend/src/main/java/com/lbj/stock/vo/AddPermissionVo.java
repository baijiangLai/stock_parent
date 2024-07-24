package com.lbj.stock.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 */
@Data
@ApiModel(description = "新增菜单权限VO")
public class AddPermissionVo {

    @ApiModelProperty(value = "菜单权限编码")
    private String code;

    @ApiModelProperty(value = "菜单权限名称")
    private String title;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "授权")
    private String perms;

    @ApiModelProperty(value = "访问地址")
    private String url;

    @ApiModelProperty(value = "资源请求类型")
    private String method;

    @ApiModelProperty(value = "name与前端路由一致")
    private String name;

    @ApiModelProperty(value = "父级菜单权限id")
    private String pid;

    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    @ApiModelProperty(value = "菜单权限类型")
    private Integer type;


}
