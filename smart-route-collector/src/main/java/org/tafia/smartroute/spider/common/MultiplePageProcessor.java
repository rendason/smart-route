package org.tafia.smartroute.spider.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tafia.smartroute.spider.common.annotation.PageCondition;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by Dason on 2018/5/20.
 */
public class MultiplePageProcessor implements PageProcessor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Site site;

    private List<PredicatedProcessor> processors = new ArrayList<>();

    public MultiplePageProcessor(Site site) {
        this.site = site;
    }

    public void addProcessMethod(Predicate<Page> condition, Consumer<Page> processor) {
        processors.add(new PredicatedProcessor(condition, processor));
        logger.info("add one processor condition: {}, method: {}", condition, processor);
    }

    public void addProcessMethod(Object instance) {
        Stream.of(instance.getClass().getDeclaredMethods())
                .filter(method -> getAnnotation(method, PageCondition.class) != null
                        && method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0] == Page.class)
                .forEach(method -> {
                    PageCondition pageCondition = getAnnotation(method, PageCondition.class);
                    Class<? extends BiPredicate<Page, Method>> predicateClass = pageCondition.value();
                    BiPredicate<Page, Method> predicate = newInstance(predicateClass);
                    Predicate<Page> condition = page -> predicate.test(page, method);
                    Consumer<Page> processor = page -> invoke(method, instance, page);
                    addProcessMethod(condition, processor);
                });
    }

    @Override
    public void process(Page page) {
        processors.stream().filter(e -> e.match(page)).findFirst().ifPresent(e -> e.process(page));
    }

    @Override
    public Site getSite() {
        return site;
    }

    private <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void invoke(Method method, Object ref, Page page) {
        try {
            method.setAccessible(true);
            method.invoke(ref, page);
        } catch (Exception e) {
            logger.warn("Error occurs on invoking {} on {} by {}", method, ref, page, e);
        }
    }

    private <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annotationClass) {
        if (element.isAnnotationPresent(annotationClass)) {
            return element.getAnnotation(annotationClass);
        }
        for (Annotation annotation : element.getAnnotations()) {
            T result;
            if ((result = getAnnotation(annotation.annotationType(), annotationClass)) != null) {
                return result;
            }
        }
        return null;
    }

    private static class PredicatedProcessor {

        private Predicate<Page> condition;

        private Consumer<Page> processor;

        PredicatedProcessor(Predicate<Page> condition, Consumer<Page> processor) {
            this.condition = condition;
            this.processor = processor;
        }

        boolean match(Page page) {
            return condition.test(page);
        }

        void process(Page page) {
            processor.accept(page);
        }
    }
}
