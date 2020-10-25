package factory;

import annotation.Service_other;
import com.alibaba.druid.pool.DruidDataSource;
import utils.DruidUtils;

import java.sql.Connection;
import java.sql.SQLException;
@Service_other
public class ConnectionFactory {

    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public static Connection getConnection() throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if(connection == null){
            DruidDataSource dataSource = DruidUtils.getInstance();
            connection = dataSource.getConnection();
            //未关联connection的线程，关联
            connectionThreadLocal.set(connection);
        }
        return connection;
    }
}
