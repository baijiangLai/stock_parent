package com.lbj.stock.vo.req;

import lombok.Data;

/**
 * @Description 登录请求vo
 */
@Data
public class LoginReqVo {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 验证码
     */
    private String code;

    /**
     * 存入redis的随机码的key
     */
    private String sessionId;
}
