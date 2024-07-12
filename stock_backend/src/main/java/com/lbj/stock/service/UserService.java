package com.lbj.stock.service;

import com.lbj.stock.pojo.entity.SysUser;
import com.lbj.stock.vo.req.LoginReqVo;
import com.lbj.stock.vo.resp.LoginRespVo;
import com.lbj.stock.vo.resp.R;

import java.util.Map;

public interface UserService {

    /**
     * 根据用户查询用户信息
     * @param userName 用户名称
     * @return
     */
    SysUser getUserByUserName(String userName);

    R<LoginRespVo> login(LoginReqVo vo);

    R<Map> getCaptchaCode();
}
