
package aop.aspect;


import annotation.Autowired_other;
import annotation.Service_other;
import annotation.Transaction_other;
import intercepe.CGLibIntercept;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import transaction.TransactionManager;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiceScan {

    private static String classPath = "D:\\study\\lagou\\课件\\文档\\模块二\\code\\transfer\\src\\main\\java\\rich";

    public static Map<String, Object> singletonCache = new HashMap<>();

    static {
        //递归查找路径下的所有类
        List<Class> clazzes = loadByPath(classPath);
        //反射获取所有标有注解的类，并放入缓存中
        clazzes.forEach(c -> {
            try {
                annotationFilter(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 注解过滤器
     */
    private static void annotationFilter(Class c) throws Exception {
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
				key = c.getName().substring(c.getName().lastIndexOf(".")+1).toLowerCase();
            }
            singletonCache.put(key, singleton);
            //组装字段上的属性注入
            populate(singleton);
            instantiateMethod(key,singleton);
        }
    }


    /**
     * 实例化成员方法
     */

    private static void instantiateMethod(String key,Object singleton) {
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
        if (singleton.getClass().isInterface() || Proxy.isProxyClass(singleton.getClass())) {
            Proxy.newProxyInstance(singleton.getClass().getClassLoader(), singleton.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (aopMethodSet.contains(method.getName())) {
                        try {//事务开启
                            TransactionManager.beginTransaction();
                            Object object =  method.invoke(singleton, args);
                            //事务提交
                            TransactionManager.commit();
                            return object;
                        } catch (Exception e) {
                            e.printStackTrace();
                            //事务回滚
                            TransactionManager.rollBack();
                            System.out.println("事务回滚");
                        }
                    } else {
                        return method.invoke(singleton, args);
                    }
                    return null;
                }
            });
        } else {
            //cglib动态代理
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(singleton.getClass());
            CGLibIntercept cgLibIntercept = new CGLibIntercept(aopMethodSet,singleton);
            enhancer.setCallback(cgLibIntercept);
            Object cglibSingleton = enhancer.create();
            singletonCache.put(key.toLowerCase(),cglibSingleton);
        }
    }


    /**
     * 解析成员变量
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
				String fieldKey = fieldName.substring(fieldName.lastIndexOf(".")+1);
                Object cacheSingleton = singletonCache.get(fieldKey.toLowerCase());
                if (null == cacheSingleton) {
                    //不存在该对象，新增
                    Object newSingleton = declaredFields[i].getDeclaringClass().getConstructor().newInstance();
                    //字段赋值
                    declaredFields[i].set(singleton,newSingleton);
                    singletonCache.put(fieldKey.toLowerCase(), newSingleton);
                    //新增之后赋值为成员变量
					cacheSingleton=newSingleton;
                }
                	//如果不为空，初始化字段
					declaredFields[i].set(singleton,cacheSingleton);
            }
        }
    }


    /**
     * 递归查询路径下左右类
     */

    private static List<Class> loadByPath(String classPath) {
        //解析文件夹下所有文件的绝对路径
        String path = classPath.replace('.', '/');
        File dir = new File(path);
        List<String> filePathList = new ArrayList<>();
        findFile(dir, filePathList);
        //文件加载为类
        List<Class> classList = new ArrayList<>();
        filePathList.forEach(c -> {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(c);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            classList.add(clazz);
        });
        return classList;
    }

    private static void findFile(File file, List<String> filePath) {
        if (file.isFile()) {
            int begin = file.getAbsolutePath().indexOf("java") + 5;
            String path = file.getAbsolutePath().substring(begin, file.getAbsolutePath().length() - 5);
            filePath.add(path.replace("\\", "."));
        } else {
            if (file.listFiles().length > 0) {
                for (int i = 0; i < file.listFiles().length; i++) {
                    findFile(file.listFiles()[i], filePath);
                }
            } else {
                return;
            }
        }
    }
}

