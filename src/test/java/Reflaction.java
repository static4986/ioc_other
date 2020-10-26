import enhancer.EnhancerAdvisor;
import intercepe.CGLibIntercept;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import transaction.TransactionManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

public class Reflaction {

    public static void main(String[] args) throws Exception {


        /*Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Class.forName("Student"));
        enhancer.setCallback(new EnhancerAdvisor());
        Object o = enhancer.create();
        System.out.println(o);*/


        /*Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Class.forName("Student"));
        CGLibIntercept cgLibIntercept = new CGLibIntercept(new HashSet<>());
        enhancer.setCallback(cgLibIntercept);
        Object cglibSingleton = enhancer.create();
        Student student = (Student)cglibSingleton;
        student.sayLenovo();
        student.sayHuipu();
        System.out.println(cglibSingleton);*/


        /*Class<?> student = Class.forName("Student");
        Student studentInstance = (Student)student.getConstructor().newInstance();
        studentInstance.sayHuipu();
        studentInstance.sayLenovo();*/
        /*Object student = Class.forName("Student").getConstructor().newInstance();
        Field[] fields = Class.forName("Student").getDeclaredFields();
        for (int i = 0;i<fields.length;i++){
            fields[i].setAccessible(true);
            fields[i].set(student,"信纸");
        }*/

        Student student2 = new Student();
        student2.setName("hello");
        Enhancer enhancer = new Enhancer();
        //CGLibIntercept cgLibIntercept = new CGLibIntercept(new HashSet<>());
        enhancer.setSuperclass(student2.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    try {
                        //事务增强
                        TransactionManager.beginTransaction();
                        System.out.println("增强型事务");
                        Object o1 = proxy.invokeSuper(student2, args);
                        //事务提交
                        TransactionManager.commit();
                        return o1;
                    } catch (Exception e) {
                        //事务回滚
                        e.printStackTrace();
                        System.out.println("事务回滚");
                        TransactionManager.rollBack();
                        throw e;
                    }
                }
        });
        Object cglibSingleton = enhancer.create();
        Student student1 = (Student)cglibSingleton;
        student1.sayLenovo();
        student1.sayHuipu();
        System.out.println(student1);


    }
}
