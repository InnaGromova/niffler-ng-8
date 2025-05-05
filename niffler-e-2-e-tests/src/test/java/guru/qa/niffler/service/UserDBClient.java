package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.values.AuthorityType;

import java.sql.Connection;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class UserDBClient {
    private static final Config CFG = Config.getInstance();

    public void createUser(UserJson user) {
        xaTransaction(
                Connection.TRANSACTION_READ_COMMITTED,

                new Databases.XaConsumer(
                        connection -> {
                            new UserdataUserDaoJdbc(connection)
                                    .createUser(UserEntity.fromJson(user));
                        },
                        CFG.userdataJdbcUrl()
                ),

                new Databases.XaConsumer(
                        connection -> {
                            AuthUserDaoJdbc authDao = new AuthUserDaoJdbc(connection);
                            AuthAuthorityDaoJdbc authorityDao = new AuthAuthorityDaoJdbc(connection);

                            UUID userId = authDao.create(AuthUserEntity.fromJson(user)).getId();

                            for (AuthorityType authority : AuthorityType.values()) {
                                AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
                                authAuthority.setUserId(userId);
                                authAuthority.setAuthority(authority.getValue());
                                authorityDao.create(authAuthority);
                            }
                        },
                        CFG.authJdbcUrl()
                )
        );
    }
}
