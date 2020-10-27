package ioc.resource.pojo;

public class Path {

    private String pathName;

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    @Override
    public String toString() {
        return "Path{" +
                "pathName='" + pathName + '\'' +
                '}';
    }
}
