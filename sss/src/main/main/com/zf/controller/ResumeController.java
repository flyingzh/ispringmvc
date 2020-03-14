package com.zf.controller;


import com.zf.dto.User;
import com.zf.pojo.Resume;
import com.zf.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简历管理Controller
 * zf
 */
@Controller
@RequestMapping("/resume")
public class ResumeController {


    @Autowired
    private ResumeService resumeService;


    /**
     *  查询列表
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<Resume> queryResumeList(){
        List<Resume> resumes = resumeService.queryResumeList();
        return resumes;
    }

    @RequestMapping("/add")
    @ResponseBody
    public Resume addResume(Resume re){
        System.out.println(re);
        Resume resume = resumeService.addResume(re);
        return resume;
    }

    /**
     * 更新
     * @param resume
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public Resume updateResume(Resume resume){
        System.out.println(resume);
        return resumeService.updateResume(resume);
    }


    /**
     * 删除
     * @param id
     */
    @RequestMapping("/delete/{id}")
    @ResponseBody
    public Map<String,String> deleteResume(@PathVariable("id") String id){
        System.out.println("id----->"+id);
        resumeService.deleteResume(id);
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("result","success");
        return resultMap;
    }


    /**
     *  查询详情
     * @param id
     * @return
     */
    @RequestMapping("/findResume/{id}")
    @ResponseBody
    public Resume findResume(@PathVariable("id") String id){
        System.out.println("id----->"+id);
        Resume resume = resumeService.findResume(id);
        return resume;
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> login(User user, HttpSession session){
        System.out.println(user);
        Map<String,Object> resultMap = new HashMap<>();
        String result = null;
        if (user.getUsername().equals("admin")&&user.getPassword().equals("admin")){
            session.setAttribute("loginName",user.getUsername());
            result = "success";
        }else{
            result = "fail";
        }
        resultMap.put("result",result);
        return resultMap;
    }


}
