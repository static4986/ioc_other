package ioc.proxy;

import ioc.proxy.AbstractProxy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import transaction.TransactionManager;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class CGLibProxy extends AbstractProxy implements MethodInterceptor {

    private Set<String> aopMethodSet;

    private Object obj;

    public CGLibProxy(Set<String> aopMethodSet, Object obj) {
        this.aopMethodSet = aopMethodSet;
        this.obj = obj;
    }

    public Object proxy(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(obj.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (aopMethodSet.contains(method.getName())) {
            try {
                //事务增强
                TransactionManager.beginTransaction();
                System.out.println("增强型事务");
                Object invoke = method.invoke(obj, args);
                //事务提交
                TransactionManager.commit();
                return invoke;
            } catch (Exception e) {
                //事务回滚
                e.printStackTrace();
                System.out.println("事务回滚");
                TransactionManager.rollBack();
                throw e;
            }
        } else {
            System.out.println("非增强型事务" + method.getName());
            return method.invoke(obj, args);
        }
    }
}
