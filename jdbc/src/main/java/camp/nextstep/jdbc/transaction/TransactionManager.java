package camp.nextstep.jdbc.transaction;

import javax.sql.DataSource;

public interface TransactionManager {
    void getTransaction(DataSource dataSource);
    void commit(DataSource dataSource);
    void rollback(DataSource dataSource);
}
