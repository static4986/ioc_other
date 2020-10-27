package ioc.resource;

import ioc.aspect.ApplicationContext;
import ioc.resource.pojo.Path;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class WindowResource implements Resource {

    private final static String file = "application.properties";

    @Override
    public Path getPath() {
       return null;
    }

    @Override
    public Path getPath(InputStream io) {
        return null;
    }

    @Override
    public Path getPath(String... field) {
        Properties properties = new Properties();
        InputStream io = ApplicationContext.class.getClassLoader().getResourceAsStream(file);
        Path path = new Path();
        try {
            properties.load(new InputStreamReader(io,"UTF-8"));
            if(field.length==1) {
                String scanPath = properties.getProperty(field[0]);
                path.setPathName(scanPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
