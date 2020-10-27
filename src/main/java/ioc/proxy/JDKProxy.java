package ioc.proxy;


import transaction.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxy extends AbstractProxy {

    @Override
    public Object proxy() {
        Proxy.newProxyInstance(singleton.getClass().getClassLoader(), singleton.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (aopMethodSet.contains(method.getName())) {
                    try {//事务开启
                        TransactionManager.beginTransaction();
                        Object object = method.invoke(singleton, args);
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
        return null;
    }
}
