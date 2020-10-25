import enhancer.EnhancerAdvisor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import transaction.TransactionManager;

import java.lang.reflect.Method;

public class Reflaction {

    public static void main(String[] args) throws Exception {


        /*Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Class.forName("Student"));
        enhancer.setCallback(new EnhancerAdvisor());
        Object o = enhancer.create();
        System.out.println(o);*/


        Enhancer enhancer = new Enhancer();
        Student student = new Student();
        enhancer.setSuperclass(student.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    try {
                        //事务增强
                        Object o1 = methodProxy.invokeSuper(o, objects);
                        //事务提交
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
        System.out.println(cglibSingleton);


        Student su = new Student();
        Student su1 = su.getClass().getConstructor().newInstance();
        su1.sayHuipu();
        su1.sayLenovo();


        /*Class<?> student = Class.forName("Student");
        Student studentInstance = (Student)student.getConstructor().newInstance();
        studentInstance.sayHuipu();
        studentInstance.sayLenovo();*/




    }
}
