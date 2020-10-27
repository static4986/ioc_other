package ioc.interceptor;

import annotation.Service_other;
import ioc.support.BeanDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**注解过滤器*/
public class AnnotationInterceptor implements Interceptor {

    /**解析带有需要过滤的注解*/
    public BeanDefinition findAnnotationSupportBean(Class c) throws Exception {

        BeanDefinition bd = new BeanDefinition();
        if (c.isAnnotationPresent(Service_other.class)) {
            //service注解类，注入容器
            //类实例化
            Object singleton = c.getDeclaredConstructor().newInstance();
            //获取类上的service_other注解
            Service_other serviceOther = (Service_other) c.getAnnotation(Service_other.class);
            Class<? extends Annotation> annotationClazz = serviceOther.getClass();
            Method value = annotationClazz.getMethod("value");
            //获取service_other的value方法返回值
            String valueStr = (String) value.invoke(serviceOther);
            String key;
            if (null != valueStr && !valueStr.trim().equals("")) {
                //注解value不为空，放入别名作为ke'y
                key = valueStr.toLowerCase();
            } else {
                //注解value为空，默认类名小写为key
                key = c.getName().substring(c.getName().lastIndexOf(".") + 1).toLowerCase();
            }
            bd.setAliasName(key);
            bd.setBeanName(c.getName());
            bd.setObject(singleton);
            return bd;
        }else {
            return null;
        }
    }
}
