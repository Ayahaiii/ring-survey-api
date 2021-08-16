package com.monetware.ringsurvey.system.util.spring;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: Cookie
 * @Date: 2019/1/18 16:01
 * @Description: 获取javaBean实例
 */
@Component
public class SpringBeanUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringBeanUtil.applicationContext == null) {
            SpringBeanUtil.applicationContext = applicationContext;
        }
        System.out.println("-----------------------------------------------");
        System.out.println("-----------------------------------------------");
        System.out.println("========= RING SURVEY API BEAN START ==========");
        System.out.println("-----------------------------------------------");
        System.out.println("-----------------------------------------------");
    }
    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }
    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }
    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
}
