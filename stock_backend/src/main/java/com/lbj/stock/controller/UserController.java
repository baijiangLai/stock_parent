package com.lbj.stock.controller;

import com.lbj.stock.pojo.entity.SysUser;
import com.lbj.stock.pojo.vo.req.LoginReqVo;
import com.lbj.stock.pojo.vo.resp.LoginRespVo;
import com.lbj.stock.pojo.vo.resp.R;
import com.lbj.stock.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    @GetMapping("/user/{userName}")
    public SysUser getUserByUserName(@PathVariable("userName") String userName){
        return userService.getUserByUserName(userName);
    }

    @PostMapping("/login")
    public R<LoginRespVo> login(@RequestBody LoginReqVo vo) {
        R<LoginRespVo> respVoR = userService.login(vo);
        return respVoR;
    }

}
