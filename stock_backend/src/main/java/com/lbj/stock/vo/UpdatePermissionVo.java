package com.lbj.stock.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 */
@Data
@ApiModel(description = "更新菜单权限VO")
public class UpdatePermissionVo {

    @ApiModelProperty(value = "主键id")
    private String id;

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

    @ApiModelProperty(value = "name与vue路由name一致")
    private String name;

    @ApiModelProperty(value = "父级菜单权限id")
    private String pid;

    @ApiModelProperty(value = "排序")
    private Integer orderNum;


    @ApiModelProperty(value = "菜单权限类型(1:目录;2:菜单;3:按钮)")
    private Integer type;
}
