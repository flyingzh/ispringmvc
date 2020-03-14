package com.zf.interceptor;

import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String name= (String) request.getSession().getAttribute("loginName");
        System.out.println("preHandle--->"+name);
        if (!StringUtils.isNullOrEmpty(name)){
//            response.sendRedirect(request.getContextPath() + "/index.html");
//            request.getRequestDispatcher("/index.html").forward(request,response);
            return true;
        }else {
            String json = "{\"error\":\"login\"}";
            response.getWriter().write("login");
//            String url = request.getContextPath() + "login";
//            response.sendRedirect(url);
//            request.getRequestDispatcher("/login.html").forward(request, response);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
