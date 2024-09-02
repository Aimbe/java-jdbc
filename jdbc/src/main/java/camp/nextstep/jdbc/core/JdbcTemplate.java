package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.dao.EmptyResultDataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class JdbcTemplate {

  private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

  private final DataSource dataSource;

  private Connection transactionConnection;

  public JdbcTemplate(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public int update(String sql, Object... params) {
    try (Connection conn = getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      setParameters(preparedStatement, Arrays.asList(params));
      return preparedStatement.executeUpdate();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DataAccessException("update 쿼리 실행 중 오류가 발생했습니다.", e);
    }
  }

  public <T> T queryForObject(String sql, ResultSetHandler<T> resultSetHandler, Object... params) {
    try (Connection conn = getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      setParameters(preparedStatement, Arrays.asList(params));
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return resultSetHandler.handle(resultSet);
        } else {
          throw new EmptyResultDataAccessException("쿼리 결과가 없습니다.");
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("단일 객체 조회 쿼리 실행 중 오류가 발생했습니다.", e);
    }
  }

  public <T> List<T> queryForList(String sql,
      ResultSetHandler<T> resultSetHandler,Object... params) {
    try (Connection conn = getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      setParameters(preparedStatement, Arrays.asList(params));

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        List<T> results = new ArrayList<>();
        while (resultSet.next()) {
          results.add(resultSetHandler.handle(resultSet));
        }
        return results;
      }
    } catch (SQLException e) {
      throw new DataAccessException("리스트 조회 쿼리 실행 중 오류가 발생했습니다.", e);
    }
  }

  private void setParameters(PreparedStatement preparedStatement, List<?> params)
      throws SQLException {
    for (int i = 0; i < params.size(); i++) {
      preparedStatement.setObject(i + 1, params.get(i));
    }
  }


  private Connection getConnection() throws SQLException {

    if(transactionConnection != null) {
      return transactionConnection;
    }
    return dataSource.getConnection();
  }


  public void startTransaction() throws SQLException {
    if (transactionConnection == null) {
      transactionConnection = dataSource.getConnection();
      transactionConnection.setAutoCommit(false);
    }
  }

  public void commit() throws SQLException {
    if (transactionConnection != null) {
      try {
        transactionConnection.commit();
      } finally {
        closeTransactionConnection();
      }
    }
  }

  public void rollback() throws SQLException {
    if (transactionConnection != null) {
      try {
        transactionConnection.rollback();
      } finally {
        closeTransactionConnection();
      }
    }
  }

  private void closeTransactionConnection() throws SQLException {
    if (transactionConnection != null) {
      transactionConnection.setAutoCommit(true);
      transactionConnection.close();
      transactionConnection = null;
    }
  }

}

