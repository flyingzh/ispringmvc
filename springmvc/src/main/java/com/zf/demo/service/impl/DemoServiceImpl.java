package com.zf.demo.service.impl;

import com.zf.demo.service.IDemoService;
import com.zf.framework.anno.Service;


@Service
public class DemoServiceImpl implements IDemoService {
    @Override
    public String getName(String name) {
        System.out.printf("DemoServiceImpl-->"+name);
        return name;
    }
}
