package org.tafia.smartroute.spider.common.annotation;

import us.codecraft.webmagic.Page;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.function.BiPredicate;

/**
 * Created by Dason on 2018/5/20.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PageCondition(PageUrlStartsWith.PageUrlStartsWithImpl.class)
@Inherited
public @interface PageUrlStartsWith {

    String value();

    class PageUrlStartsWithImpl implements BiPredicate<Page, Method> {

        @Override
        public boolean test(Page page, Method method) {
            if (!method.isAnnotationPresent(PageUrlStartsWith.class))
                return false;
            String prefix = method.getAnnotation(PageUrlStartsWith.class).value();
            return page.getUrl().toString().startsWith(prefix);
        }
    }
}
