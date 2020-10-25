package transaction;

import annotation.Autowired_other;
import annotation.Service_other;
import factory.ConnectionFactory;

import java.sql.SQLException;
@Service_other
public class TransactionManager {

    @Autowired_other
    private static ConnectionFactory connectionFactory;

    /**
     * 开始事务
     * */
    public static void beginTransaction() throws SQLException {
        connectionFactory.getConnection().setAutoCommit(false);
    }


    /**
     * 提交事务
     * */
    public static void commit() throws SQLException{
        connectionFactory.getConnection().commit();
    }

    /**
     * 事务回滚
     * */
    public static void rollBack() throws SQLException{
        connectionFactory.getConnection().rollback();
    }
}
