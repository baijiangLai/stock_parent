package com.lbj.stock.pojo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 */
@Data
@ApiModel(description = "菜单树Domain")
public class PermissionsTreeDomain {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "菜单权限名称")
    private String title;

    @ApiModelProperty(value = "菜单权限类型")
    private Integer level;
}
