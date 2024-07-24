package com.lbj.stock.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbj.stock.constant.StockConstant;
import com.lbj.stock.utils.JwtTokenUtil;
import com.lbj.stock.vo.resp.R;
import com.lbj.stock.vo.resp.ResponseCode;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description 定义授权过滤器，核心作用：
 *      1.过滤请求，获取请求头中的token字符串
 *      2.解析token字符串，并获取token中信息：username role
 *      3.将用户名和权限信息封装到UsernamePassowrdAuthentionToken票据对象下
 *      4.将票据对象放入安全上下文，方便校验权限时，随时获取
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    /**
     * 访问过滤的方法
     * @param request
     * @param response
     * @param filterChain 过滤器链
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //1.从request对象下获取token数据，约定key：Authorization
        String tokenStr = request.getHeader(StockConstant.TOKEN_HEADER);
        //判断token字符串是否存在
        if (tokenStr==null) {
            //如果票据为null，可能用户还没有认证，正准备去认证,所以放行请求
            //放行后，会不会访问当受保护的资源呢？不会，因为没有生成UsernamePassowrdAuthentionToken
            filterChain.doFilter(request,response);
            return;
        }
        //2.解析tokenStr,获取用户详情信息
        Claims claims = JwtTokenUtil.checkJWT(tokenStr);
        //token字符串失效的情况
        if (claims==null) {
            //说明 票据解析出现异常，票据就失效了
            R<Object> r = R.error(ResponseCode.TOKEN_NO_AVAIL);
            String respStr = new ObjectMapper().writeValueAsString(r);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(respStr);
            return;
        }
        //获取用户名和权限信息
        String userName = JwtTokenUtil.getUsername(tokenStr);
        //生成token时，权限字符串的格式是：[P8,ROLE_ADMIN]
        String perms = JwtTokenUtil.getUserRole(tokenStr);
        //生成权限集合对象
        //P8,ROLE_ADMIN
        String strip = StringUtils.strip(perms, "[]");
        List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(strip);
        //将用户名和权限信息封装到token对象下
        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(userName,null,authorityList);
        //将token对象存入安全上限文，这样，线程无论走到哪里，都可以获取token对象，验证当前用户访问对应资源是否被授权
        SecurityContextHolder.getContext().setAuthentication(token);
        //资源发行
        filterChain.doFilter(request,response);
    }
}
