package com.lbj.stock.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.lbj.stock.mapper.SysUserMapper;
import com.lbj.stock.pojo.entity.SysUser;
import com.lbj.stock.pojo.vo.req.LoginReqVo;
import com.lbj.stock.pojo.vo.resp.LoginRespVo;
import com.lbj.stock.pojo.vo.resp.R;
import com.lbj.stock.pojo.vo.resp.ResponseCode;
import com.lbj.stock.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据用户名称查询用户信息
     * @param userName 用户名称
     * @return
     */
    @Override
    public SysUser getUserByUserName(String userName) {
        return sysUserMapper.findByUserName(userName);
    }

    @Override
    public R<LoginRespVo> login(LoginReqVo vo) {
        if (vo == null || StrUtil.isEmpty(vo.getUsername()) || StrUtil.isEmpty(vo.getPassword())) {
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        SysUser user = sysUserMapper.findByUserName(vo.getUsername());
        if (user == null || !passwordEncoder.matches(vo.getPassword(), user.getPassword())) {
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR.getMessage());
        }
        LoginRespVo respVo = new LoginRespVo();
        BeanUtil.copyProperties(user, respVo);
        return R.ok(respVo);
    }
}
