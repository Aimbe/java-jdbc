package camp.nextstep.jdbc.transaction;

public interface TransactionManager {
    void getTransaction();
    void commit();
    void rollback();
}
