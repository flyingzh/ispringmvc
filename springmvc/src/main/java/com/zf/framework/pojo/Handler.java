package com.zf.framework.pojo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Handler {

    private Object controller;
    private Pattern pattern;
    private Method method;
    private Map<String,Integer> argsList;
    private List<String> lmts;//权限限制

    public Handler(Object controller, Pattern pattern, Method method) {
        this.controller = controller;
        this.pattern = pattern;
        this.method = method;
        this.argsList = new HashMap<>();
        this.lmts = new LinkedList<>();
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, Integer> getArgsList() {
        return argsList;
    }

    public void setArgsList(Map<String, Integer> argsList) {
        this.argsList = argsList;
    }

    public List<String> getLmts() {
        return lmts;
    }

    public void setLmts(List<String> lmts) {
        this.lmts = lmts;
    }
}
