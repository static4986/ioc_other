package utils;

import annotation.Service_other;
import com.alibaba.druid.pool.DruidDataSource;
@Service_other
public class DruidUtils {

    private static DruidDataSource druidDataSource = new DruidDataSource();

    static {
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/bank");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
    }

    public static DruidDataSource getInstance(){
        return druidDataSource;
    }

}
