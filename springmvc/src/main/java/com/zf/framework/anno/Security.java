package com.zf.framework.anno;


import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Security {

    String value() default "";

    String[] filter();

}
