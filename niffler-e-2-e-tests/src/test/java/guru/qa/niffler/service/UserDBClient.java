package guru.qa.niffler.service;

import org.springframework.transaction.support.TransactionTemplate;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDataDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.dao.impl.UserDataDaoSpringJdbc;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.values.AuthorityType;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

import java.util.Arrays;

public class UserDBClient {
    private static final Config CFG = Config.getInstance();
    private final AuthUserDao authUserDaoSpringJdbc = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpringJdbc = new AuthAuthorityDaoSpringJdbc();
    private final UserDataDao userDataDaoSpringJdbc = new UserDataDaoSpringJdbc();
    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            dataSource(CFG.authJdbcUrl())
                    ),
                    new JdbcTransactionManager(
                            dataSource(CFG.userdataJdbcUrl())
                    )
            )
    );
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(), CFG.userdataJdbcUrl()
    );



    public UserJson createUserTxJdbc(UserJson user) throws Exception {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(null);
                    authUser.setPassword("test-user");
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                   AuthUserEntity createdAuthUser = authUserDaoSpringJdbc.create(authUser);

                   AuthAuthorityEntity[] authorityEntities = Arrays.stream(AuthorityType.values()).map(
                            e -> {
                                AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
                                authAuthority.setId(createdAuthUser.getId());
                                authAuthority.setAuthority(AuthorityType.valueOf(String.valueOf(e)));
                                return authAuthority;
                            }
                    ).toArray(AuthAuthorityEntity[]::new);

                    authAuthorityDaoSpringJdbc.create(authorityEntities);
                    return UserJson.fromEntity(
                            userDataDaoSpringJdbc.createUser(UserEntity.fromJson(user))
                    );
                }
        );
    }

    //ChainedTransactionManager
    public UserJson chainedTxCreateUserSpringJdbc(UserJson user) {
        return txTemplate.execute(status -> {
            AuthUserEntity authUserEntity = new AuthUserEntity();
            authUserEntity.setId(user.id());
            authUserEntity.setUsername(user.username());
            authUserEntity.setPassword(user.password());
            authUserEntity.setEnabled(true);
            authUserEntity.setAccountNonExpired(true);
            authUserEntity.setAccountNonLocked(true);
            authUserEntity.setCredentialsNonExpired(true);
            AuthUserEntity createdAuthUser = authUserDaoSpringJdbc.create(authUserEntity);
            AuthAuthorityEntity[] userAuthorities = Arrays.stream(AuthorityType.values()).map(
                    e -> {
                        AuthAuthorityEntity ae = new AuthAuthorityEntity();
                        ae.setId(createdAuthUser.getId());
                        ae.setAuthority(AuthorityType.valueOf(String.valueOf(e)));
                        return ae;
                    }).toArray(AuthAuthorityEntity[]::new);
            authAuthorityDaoSpringJdbc.create(userAuthorities);
            return UserJson.fromEntity(
                    userDataDaoSpringJdbc.createUser(UserEntity.fromJson(user))
            );
        }
        );
    }
    public UserJson createUserJdbc(UserJson user) {

        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(user.username());
        authUserEntity.setPassword(user.password());
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);
        AuthUserEntity createdAuthUser = authUserDaoSpringJdbc.create(authUserEntity);
        AuthAuthorityEntity[] userAuthorities = Arrays.stream(AuthorityType.values()).map(
                e -> {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setId(createdAuthUser.getId());
                    ae.setAuthority(AuthorityType.valueOf(String.valueOf(e)));
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);
        authAuthorityDaoSpringJdbc.create(userAuthorities);
        return UserJson.fromEntity(
                userDataDaoSpringJdbc.createUser(UserEntity.fromJson(user)));
    }

//    public void createUser(UserJson user) {
//        xaTransaction(
//                Connection.TRANSACTION_READ_COMMITTED,
//
//                new Databases.XaConsumer(
//                        connection -> {
//                            new UserdataUserDaoJdbc(connection)
//                                    .createUser(UserEntity.fromJson(user));
//                        },
//                        CFG.userdataJdbcUrl()
//                ),
//
//                new Databases.XaConsumer(
//                        connection -> {
//                            AuthUserDaoJdbc authDao = new AuthUserDaoJdbc(connection);
//                            AuthAuthorityDaoJdbc authorityDao = new AuthAuthorityDaoJdbc(connection);
//
//                            UUID userId = authDao.create(AuthUserEntity.fromJson(user)).getId();
//
//                            for (AuthorityType authority : AuthorityType.values()) {
//                                AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
//                                authAuthority.setUserId(userId);
//                                authAuthority.setAuthority(authority.getValue());
//                                authorityDao.create(new AuthorityEntity[]{authAuthority});
//                            }
//                        },
//                        CFG.authJdbcUrl()
//                )
//        );
//    }
}
