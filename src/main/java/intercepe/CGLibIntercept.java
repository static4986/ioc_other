package intercepe;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import transaction.TransactionManager;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class CGLibIntercept implements MethodInterceptor {

    private Set<String> aopMethodSet = new HashSet<>();

    public CGLibIntercept(Set<String> aopMethodSet) {
        this.aopMethodSet = aopMethodSet;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (aopMethodSet.contains(method.getName())) {
            try {
                //事务增强
                TransactionManager.beginTransaction();
                System.out.println("增强型事务");
                Object o1 = proxy.invokeSuper(obj, args);
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
        } else {
            System.out.println("非增强型事务"+method.getName());
            return proxy.invokeSuper(obj, args);
        }
    }
}
