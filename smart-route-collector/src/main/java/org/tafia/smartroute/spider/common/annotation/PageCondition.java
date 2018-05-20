package org.tafia.smartroute.spider.common.annotation;

import us.codecraft.webmagic.Page;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.function.BiPredicate;

/**
 * Created by Dason on 2018/5/20.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageCondition {

    Class<? extends BiPredicate<Page, Method>> value();
}
