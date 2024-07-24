package com.lbj.stock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbj.stock.vo.resp.R;
import com.lbj.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description 定义没有权限，访问拒绝的处理器
 * 用户认证成功，但是没有访问的权限，则会除非拒绝处理器
 * 如果是匿名用户访问被拒绝则使用匿名拒绝的处理器
 */
@Slf4j
public class StockAccessDenyHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException, ServletException {
        log.info("访问拒绝，异常信息：{}",ex.getMessage());
        //说明 票据解析出现异常，票据就失效了
        R<Object> r = R.error(ResponseCode.NOT_PERMISSION);
        String respStr = new ObjectMapper().writeValueAsString(r);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(respStr);
    }
}
