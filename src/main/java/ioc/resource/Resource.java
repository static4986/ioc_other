package ioc.resource;

import ioc.resource.pojo.Path;

import java.io.InputStream;

public interface Resource {

    Path getPath();

    Path getPath(InputStream io);

    Path getPath(String... property);

}
