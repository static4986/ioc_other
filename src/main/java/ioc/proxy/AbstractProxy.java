package ioc.proxy;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractProxy {

    protected Set<String> aopMethodSet = new HashSet<>();

    protected Object singleton;

    public void setAopMethodSet(Set<String> aopMethodSet) {
        this.aopMethodSet = aopMethodSet;
    }

    public void setObject(Object object) {
        this.singleton = object;
    }

    abstract Object proxy();
}
