package com.lbj.stock.service;

import com.lbj.stock.pojo.entity.SysUser;
import com.lbj.stock.pojo.vo.req.LoginReqVo;
import com.lbj.stock.pojo.vo.resp.LoginRespVo;
import com.lbj.stock.pojo.vo.resp.R;

public interface UserService {

    /**
     * 根据用户查询用户信息
     * @param userName 用户名称
     * @return
     */
    SysUser getUserByUserName(String userName);

    R<LoginRespVo> login(LoginReqVo vo);
}
