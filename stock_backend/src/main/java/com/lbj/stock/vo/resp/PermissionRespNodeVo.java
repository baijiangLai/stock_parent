package com.lbj.stock.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "菜单树VO")
public class PermissionRespNodeVo {

    @ApiModelProperty(value = "角色id")
    private String id;

    @ApiModelProperty(value = "角色标题")
    private String title;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "路由地址")
    private String path;

    @ApiModelProperty(value = "路由名称")
    private String name;

    @ApiModelProperty(value = "菜单树结构")
    private List<PermissionRespNodeVo> children;
}
