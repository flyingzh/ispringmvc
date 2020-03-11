package com.zf.demo;


import com.zf.demo.service.IDemoService;
import com.zf.framework.anno.Autowired;
import com.zf.framework.anno.Controller;
import com.zf.framework.anno.RequestMapping;
import com.zf.framework.anno.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/demo")
@Security(filter = {"zhangsan"})
public class DemoController {


    @Autowired
    private IDemoService demoService;


    @RequestMapping("/handle01")
    @Security(filter = {"lisi"})
    public void handle01(HttpServletRequest request, HttpServletResponse response,String username){
        demoService.getName(username);
        try {
            response.getWriter().write("username---->"+username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/handle02")
    @Security(filter = {"wangwu","zhaoliu"})
    public void handle02(HttpServletRequest request, HttpServletResponse response,String username){
        demoService.getName(username);
        try {
            response.getWriter().write("username---->"+username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/handle03")
    @Security(filter = {"lisi","wangwu","zhaoliu"})
    public void handle03(HttpServletRequest request, HttpServletResponse response,String username){
        demoService.getName(username);
        try {
            response.getWriter().write("username---->"+username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
