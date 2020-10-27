package ioc.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassLoader {

    private final String classPath;

    public ClassLoader(String classPath) {
        this.classPath = classPath;
    }

    /**
     * 加载路径下的所有类
     */
    public List<Class> loadByPath() {
        //解析文件夹下所有文件的绝对路径
        String path = classPath.replace('.', '/');
        File dir = new File(path);
        List<String> filePathList = new ArrayList<>();
        findFile(dir, filePathList);
        //文件加载为类
        List<Class> classList = new ArrayList<>();
        filePathList.forEach(c -> {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(c);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            classList.add(clazz);
        });
        return classList;
    }

    /**
     * 递归查询路径下所有的类
     * */
    private void findFile(File file, List<String> filePath) {
        if (file.isFile()) {
            int begin = file.getAbsolutePath().indexOf("java") + 5;
            String path = file.getAbsolutePath().substring(begin, file.getAbsolutePath().length() - 5);
            filePath.add(path.replace("\\", "."));
        } else {
            if (file.listFiles().length > 0) {
                for (int i = 0; i < file.listFiles().length; i++) {
                    findFile(file.listFiles()[i], filePath);
                }
            } else {
                return;
            }
        }
    }
}
