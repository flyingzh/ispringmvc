package com.zf.framework.servlet;

import com.zf.framework.anno.*;
import com.zf.framework.pojo.Handler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDispatcherServlet extends HttpServlet {


    private Properties properties = new Properties();

    //存放扫描到的所有的类
    private List<String> classList = new LinkedList<>();
    //IOC容器
    private Map<String,Object> iocMap = new HashMap<String,Object>();
    //url和 method映射集合
    private List<Handler> handlers = new LinkedList<>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
//        加载配置文件
        doInitConfig(contextConfigLocation);
//      扫描相关的类，扫描注解
        doScan(properties.getProperty("packageScan"));
        //装配IoC
        doIoC();
        //处理DI
        doAutowired();
        //处理url和method映射
        doRequestMapping();

    }

    /**
     *  处理映射
     */
    private void doRequestMapping() {
        if(iocMap.isEmpty()) {return;}

        for(Map.Entry<String,Object> entry: iocMap.entrySet()) {
            Class<?> aClass = entry.getValue().getClass();

            if(!aClass.isAnnotationPresent(Controller.class)) {continue;}

            String baseUrl = "";
            if(aClass.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);
                baseUrl = annotation.value();
            }


            Method[] methods = aClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                List<String> limts = new LinkedList<>();
                if(aClass.isAnnotationPresent(Security.class)){
                    Security annotation = aClass.getAnnotation(Security.class);
                    String[] filters = annotation.filter();
                    if(filters !=null && filters.length !=0){
                        for(String filter:filters){
                            limts.add(filter);
                        }
                    }
                }


                Method method = methods[i];

                if(!method.isAnnotationPresent(RequestMapping.class)) {continue;}

                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                String methodUrl = annotation.value();
                String url = baseUrl + methodUrl;

                // 把method所有信息及url封装为一个Handler
                Handler handler = new Handler(entry.getValue(),Pattern.compile(url),method);


                // 计算方法的参数位置信息
                Parameter[] parameters = method.getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];

                    if(parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class) {
                        // 如果是request和response对象，那么参数名称写HttpServletRequest和HttpServletResponse
                        handler.getArgsList().put(parameter.getType().getSimpleName(),j);
                    }else{
                        handler.getArgsList().put(parameter.getName(),j);
                    }
                }

                if(method.isAnnotationPresent(Security.class)){
                    Security securityAnnotation = method.getAnnotation(Security.class);
                    String[] filters = securityAnnotation.filter();
                    if(filters !=null && filters.length !=0){
                        for(String filter:filters){
                            limts.add(filter);
                        }
                    }
                }

                handler.setLmts(limts);

                // 建立url和method之间的映射关系
                handlers.add(handler);

            }
        }
    }


    /**
     *  处理自动注入
     */
    private void doAutowired() {
        if(iocMap.isEmpty()) return;

        Set<String> keySet = iocMap.keySet();
        for(String key:keySet){
            Object o = iocMap.get(key);
            Field[] declaredFields = o.getClass().getDeclaredFields();
            for(Field field:declaredFields){
                if(field.isAnnotationPresent(Autowired.class)){
                    Autowired annotation = field.getAnnotation(Autowired.class);
                    String beanName = annotation.value();
                    if(StringUtils.isBlank(beanName)){
                        beanName = field.getType().getName();
                    }
                    field.setAccessible(true);
                    try {
                        field.set(o,iocMap.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     *  类扫描放入list
     * @param packageScan
     */
    private void doScan(String packageScan) {
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + packageScan.replaceAll("\\.", "/");
        File pack = new File(scanPackagePath);

        File[] files = pack.listFiles();

        for(File file: files) {
            if(file.isDirectory()) { // 子package
                // 递归
                doScan(packageScan + "." + file.getName());  // com.lagou.demo.controller
            }else if(file.getName().endsWith(".class")) {
                String className = packageScan + "." + file.getName().replaceAll(".class", "");
                classList.add(className);
            }
        }


    }

    /**
     *  首字母转小写
     *
     * @param arg
     * @return
     */
    private String lowerFirst(String arg){
        char[] chars = arg.toCharArray();
        if('A' <= chars[0] && chars[0] <= 'Z') {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }


    /**
     * 添加到ioc容器
     */
    private void doIoC() {
        if(classList.isEmpty()){
            return;
        }

        try {
            for(String path:classList){
                Class<?> aClass = Class.forName(path);
                if(aClass.isAnnotationPresent(Controller.class)){
                    String beanName = lowerFirst(aClass.getSimpleName());
                    iocMap.put(beanName,aClass.newInstance());
                }else if(aClass.isAnnotationPresent(Service.class)){
                    Service annotation = aClass.getAnnotation(Service.class);
                    String beanName = annotation.value();
                    if(StringUtils.isBlank(beanName)){
                        beanName = lowerFirst(aClass.getSimpleName());
                    }
                    iocMap.put(beanName,aClass.newInstance());

                    Class<?>[] interfaces = aClass.getInterfaces();
                    for(Class<?> interFace:interfaces){
                        iocMap.put(interFace.getName(),aClass.newInstance());
                    }
                }else{
                    continue;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载配置文件
     * @param configStr
     */
    private void doInitConfig(String configStr){
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(configStr));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Handler handler = getHandler(req);

        if(handler == null) {
            resp.getWriter().write("404 not found");
            return;
        }

        String username = req.getParameter("username");
        if(!handler.getLmts().contains(username)){
            resp.getWriter().write("ERROR Permission denied");
            return;
        }


        // 参数绑定
        // 获取所有参数类型数组，这个数组的长度就是我们最后要传入的args数组的长度
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();


        // 根据上述数组长度创建一个新的数组（参数数组，是要传入反射调用的）
        Object[] paraValues = new Object[parameterTypes.length];

        Map<String, String[]> parameterMap = req.getParameterMap();

        // 遍历request中所有参数  （填充除了request，response之外的参数）
        for(Map.Entry<String,String[]> param: parameterMap.entrySet()) {
            // name=1&name=2   name [1,2]
            String value = StringUtils.join(param.getValue(), ",");  // 如同 1,2

            // 如果参数和方法中的参数匹配上了，填充数据
            if(!handler.getArgsList().containsKey(param.getKey())) {continue;}

            // 方法形参确实有该参数，找到它的索引位置，对应的把参数值放入paraValues
            Integer index = handler.getArgsList().get(param.getKey());//name在第 2 个位置

            paraValues[index] = value;  // 把前台传递过来的参数值填充到对应的位置去

        }


        int requestIndex = handler.getArgsList().get(HttpServletRequest.class.getSimpleName()); // 0
        paraValues[requestIndex] = req;


        int responseIndex = handler.getArgsList().get(HttpServletResponse.class.getSimpleName()); // 1
        paraValues[responseIndex] = resp;

        try {
            handler.getMethod().invoke(handler.getController(),paraValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    private Handler getHandler(HttpServletRequest req) {
        if(handlers.isEmpty()){return null;}

        String url = req.getRequestURI();

        for(Handler handler: handlers) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){continue;}
            return handler;
        }
        return null;

    }
}
