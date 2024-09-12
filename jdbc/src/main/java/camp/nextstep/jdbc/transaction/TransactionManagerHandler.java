package camp.nextstep.jdbc.transaction;

import camp.nextstep.jdbc.datasource.DataSourceUtils;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class TransactionManagerHandler implements TransactionManager {


  @Override
  public void getTransaction(DataSource dataSource) {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    try {
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void commit(DataSource dataSource) {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    try {
      connection.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      TransactionSynchronizationManager.unbindResource(dataSource);
      DataSourceUtils.releaseConnection(connection, dataSource);
    }

  }

  @Override
  public void rollback(DataSource dataSource) {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    try {
      connection.rollback();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      TransactionSynchronizationManager.unbindResource(dataSource);
      DataSourceUtils.releaseConnection(connection, dataSource);
    }
  }
}
