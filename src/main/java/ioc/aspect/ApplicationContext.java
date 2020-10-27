
package ioc.aspect;


import annotation.Autowired_other;
import annotation.Transaction_other;
import ioc.interceptor.AnnotationInterceptor;
import ioc.proxy.AbstractProxy;
import ioc.proxy.CGLibProxy;
import ioc.proxy.JDKProxy;
import ioc.resource.ClassLoader;
import ioc.resource.Resource;
import ioc.resource.WindowResource;
import ioc.resource.pojo.Path;
import ioc.support.BeanDefinition;
import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ApplicationContext {

    /**
     * 包扫描路径
     */
    private static String classPath;

    /**
     * 配置文件解析字段
     */
    private static final String property = "scanPath";

    /**
     * 实例化变量缓存池
     */
    public static Map<String, Object> singletonCache = new HashMap<>();

    static {
        //获取包扫描路径
        setClassPath();
        //递归查找路径下的所有类,转换为class
        ClassLoader classLoader = new ClassLoader(classPath);
        List<Class> clazzes = classLoader.loadByPath();
        //实例化所有标有注解的类，并放入缓存中
        initClass(clazzes);
    }

    /**
     * 设置包扫描路径
     */
    public static void setClassPath() {
        Resource resource = new WindowResource();
        Path path = resource.getPath(property);
        if (null != path)
            ApplicationContext.classPath = path.getPathName();
    }

    /**
     * 实例化需要加载的类
     */
    private static void initClass(List<Class> clazzes) {
        clazzes.forEach(c -> {
            try {
                AnnotationInterceptor interceptor = new AnnotationInterceptor();
                BeanDefinition annotationSupportBean = interceptor.findAnnotationSupportBean(c);
                if(null != annotationSupportBean){
                    String key = annotationSupportBean.getAliasName();
                    Object singleton = annotationSupportBean.getObject();
                    singletonCache.put(key, singleton);
                    //组装字段上的属性注入
                    populate(singleton);
                    //实例化成员方法
                    instantiateMethod(annotationSupportBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 设置对象成员方法
     */
    private static void instantiateMethod(BeanDefinition beanDefinition) {
        String key = beanDefinition.getAliasName();
        Object singleton = beanDefinition.getObject();
        Method[] declaredMethods = singleton.getClass().getDeclaredMethods();
        //需要增强的方法
        Set<String> aopMethodSet = new HashSet<>();
        for (int i = 0; i < declaredMethods.length; i++) {
            declaredMethods[i].setAccessible(true);
            if (declaredMethods[i].isAnnotationPresent(Transaction_other.class)) {
                //事务注解，要通过aop获取代理对象
                aopMethodSet.add(declaredMethods[i].getName());
            }
        }

        //选择jdk动态代理
        Object cglibSingleton = null;
        if (singleton.getClass().isInterface() || Proxy.isProxyClass(singleton.getClass())) {
            AbstractProxy proxy = new JDKProxy();
            proxy.setAopMethodSet(aopMethodSet);
            proxy.setObject(singleton);
            cglibSingleton = ((JDKProxy) proxy).proxy();
        } else {
            //cglib动态代理
            CGLibProxy proxy = new CGLibProxy(aopMethodSet,singleton);
            cglibSingleton = proxy.proxy();
        }
        singletonCache.put(key.toLowerCase(), cglibSingleton);
    }


    /**
     * 解析对象成员变量
     */
    private static void populate(Object singleton) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> singletonClass = singleton.getClass();
        //获取单例的所有字段
        Field[] declaredFields = singletonClass.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            declaredFields[i].setAccessible(true);
            //字段是被Autowired_other修饰的，放入缓冲池
            if (declaredFields[i].isAnnotationPresent(Autowired_other.class)) {

                String fieldName = declaredFields[i].getType().getName();
                String fieldKey = fieldName.substring(fieldName.lastIndexOf(".") + 1);
                Object cacheSingleton = singletonCache.get(fieldKey.toLowerCase());
                if (null == cacheSingleton) {
                    //不存在该对象，新增
                    Object newSingleton = declaredFields[i].getDeclaringClass().getConstructor().newInstance();
                    //字段赋值
                    declaredFields[i].set(singleton, newSingleton);
                    singletonCache.put(fieldKey.toLowerCase(), newSingleton);
                    //新增之后赋值为成员变量
                    cacheSingleton = newSingleton;
                }
                //如果不为空，初始化字段
                declaredFields[i].set(singleton, cacheSingleton);
            }
        }
    }
}

