package com.lbj.stock.vo.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
public class LoginRespVo {
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 电话
     */
    private String phone;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 真名称
     */
    private String realName;
    /**
     * 性别
     */
    private int sex;
    /**
     * 状态
     */
    private int status;
    /**
     * 邮件
     */
    private String email;
    /**
     * 侧边栏权限树（不包含按钮权限）
     */
    private List<Menu> menus;
    /**
     * 按钮权限标识
     */
    private List<String> permissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Menu {
        /**
         * 权限ID
         */
        private String id;
        /**
         * 权限标题
         */
        private String title;
        /**
         * 权限图标（按钮权限无图片）
         */
        private String icon;
        /**
         * 请求地址
         */
        private String path;
        /**
         * 权限名称对应前端vue组件名称
         */
        private String name;
        /**
         * 子菜单
         */
        private List<Menu> children;
    }
}
