package com.lbj.stock.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 登录后响应前端的vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "登录后响应前端VO")
public class LoginRespVo {

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "权限树")
    private List<PermissionRespNodeVo> menus;

    @ApiModelProperty(value = "按钮权限集合")
    private List<String> permissions;

    @ApiModelProperty(value = "登录校验码")
    private String accessToken;

}