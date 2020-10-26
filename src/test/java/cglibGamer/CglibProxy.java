package cglibGamer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {
    private Object obj;

    public Object getInstance(Object obj){
        this.obj = obj;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(obj.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object invoke = null;
        if(method.getName().equals("login")){
            System.out.println("代练玩家操作~");
            invoke = method.invoke(obj,objects);
            System.out.println("代练玩家结束操作");
        }
        return invoke;
    }
}
