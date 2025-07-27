package guru.qa.niffler.data.tpl;

import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class XaTransactionTemplate {

    private final JdbcConnectionHolders holders;
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);
    private static final UserTransactionManager transactionManager;

    static {
        transactionManager = new UserTransactionManager();
        transactionManager.setForceShutdown(false);
        try {
            transactionManager.init();
            transactionManager.setTransactionTimeout(300);
        } catch (SystemException e) {
            throw new RuntimeException("Failed to initialize transaction manager", e);
        }
    }


    public XaTransactionTemplate(String... jdbcUrl) {
        this.holders = Connections.holders(jdbcUrl);
    }

    public XaTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }
    @Nullable
    public <T> T execute(Supplier<T> action) throws Exception {
        int status = transactionManager.getStatus();
        if (status != Status.STATUS_NO_TRANSACTION &&
                status != Status.STATUS_UNKNOWN) {
            return action.get();
        }

        try {
            transactionManager.begin();
            T result = action.get();
            if (transactionManager.getStatus() == Status.STATUS_ACTIVE) {
                transactionManager.commit();
            }
            return result;
        } catch (Exception e) {
            try {
                int currentStatus = transactionManager.getStatus();
                if (currentStatus == Status.STATUS_ACTIVE ||
                        currentStatus == Status.STATUS_MARKED_ROLLBACK) {
                    transactionManager.rollback();
                }
            } catch (SystemException ex) {
                e.addSuppressed(ex);
            }
            throw e;
        }
    }
}