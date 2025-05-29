package guru.qa.niffler.service.impl;


import static guru.qa.niffler.utils.RandomDataUtils.randomUserName;

import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.service.UsersClient;
import jakarta.persistence.EntityManager;
import jaxb.userdata.FriendshipStatus;
import org.springframework.transaction.support.TransactionTemplate;
import guru.qa.niffler.config.Config;
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
import java.util.Optional;

public class UserDBClient implements UsersClient {
    private static final Config CFG = Config.getInstance();
    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserDataRepository userDataRepository = new UserdataUserRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(), CFG.userdataJdbcUrl()
    );

    @Override
    public UserJson createUser(String username, String password) {
        try {
            return xaTransactionTemplate.execute(() -> {
                        AuthUserEntity authUser = authUserEntity(String.valueOf(username), password);
                        authUserRepository.create(authUser);
                        return UserJson.fromEntity(
                                userDataRepository.createUser(userEntity(username)),
                                null);
        });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createFriends(UserJson user, int count) throws Exception {
        if (count > 0) {
            UserEntity targetEntity = userDataRepository.findById(user.id())
                    .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String friendUsername = randomUserName();
                    AuthUserEntity authUser = authUserEntity(friendUsername, "test-user");
                    authUserRepository.create(authUser);
                    UserEntity friendEntity = userDataRepository.createUser(userEntity(friendUsername));
                    userDataRepository.addFriend(targetEntity, friendEntity);
                    userDataRepository.addFriend(friendEntity, targetEntity);
                    user.testData().friends().add(UserJson.fromEntity(friendEntity, FriendshipStatus.FRIEND));
                    return null;
                });
            }
        }
    }
    @Override
    public void createIncomeInvitations(UserJson requester, int count) throws Exception {
        if (count > 0) {
            UserEntity targetEntity = userDataRepository.findById(
                    requester.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = randomUserName();
                    AuthUserEntity authUser = authUserEntity(username, "test-user5");
                    authUserRepository.create(authUser);
                            UserEntity addressee = userDataRepository.createUser(userEntity(username));
                            userDataRepository.sendInvitation(targetEntity, addressee);
                            requester.testData().friendshipRequests().add(UserJson.fromEntity(addressee, FriendshipStatus.INVITE_RECEIVED));
                            return null;
                        }
                );
            }
        }
    }
    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) throws Exception {
        if (count > 0) {
            UserEntity targetEntity = userDataRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = randomUserName();
                    AuthUserEntity authUser = authUserEntity(username, "test-user5");
                    authUserRepository.create(authUser);
                    UserEntity addressee = userDataRepository.createUser(userEntity(username));
                    userDataRepository.sendInvitation(addressee, targetEntity);
                    targetUser.testData().friendshipAddressees().add(UserJson.fromEntity(addressee, FriendshipStatus.INVITE_SENT));
                            return null;
                        }
                );
            }
        }
    }
    @Override
    public Optional<UserJson> findUserByUsername(String username) throws Exception {
        return xaTransactionTemplate.execute(() -> {
            Optional<AuthUserEntity> authUserOpt = authUserRepository.findByUserName(username);
            Optional<UserEntity> userdataUserOpt = userDataRepository.findByUsername(username);

            if (authUserOpt.isPresent() && userdataUserOpt.isPresent()) {
                UserEntity userdataUser = userdataUserOpt.get();
                return Optional.of(UserJson.fromEntity(userdataUser, null));
            }

            return Optional.empty();
        });
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(password);
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(AuthorityType.values()).map(
                        e -> {
                            AuthAuthorityEntity ae = new AuthAuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
    private UserEntity userEntity(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCurrency(CurrencyValues.RUB);
        return userEntity;
    }
}



//    public UserJson createUserSpringJdbc(UserJson user) {
//        AuthUserEntity authUser = new AuthUserEntity();
//        authUser.setUsername(user.username());
//        authUser.setPassword("test-user");
//        authUser.setEnabled(true);
//        authUser.setAccountNonExpired(true);
//        authUser.setAccountNonLocked(true);
//        authUser.setCredentialsNonExpired(true);
//        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc()
//                .create(authUser);
//        AuthAuthorityEntity[] authorityEntities = Arrays.stream(AuthorityType.values()).map(
//                e -> {
//                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
//                    ae.setId(createdAuthUser.getId());
//                    ae.setAuthority(e);
//                    return ae;
//                }
//        ).toArray(AuthAuthorityEntity[]::new);
//        new AuthAuthorityDaoSpringJdbc()
//                .create(authorityEntities);
//        return UserJson.fromEntity(
//                new UserDataDaoSpringJdbc()
//                        .createUser(
//                                UserEntity.fromJson(String.valueOf(user))
//                        ),
//                null
//        );
//    }

        //ChainedTransactionManager
//    public UserJson chainedTxCreateUserSpringJdbc(UserJson user) {
//        return txTemplate.execute(status -> {
//            AuthUserEntity authUserEntity = new AuthUserEntity();
//            authUserEntity.setId(user.id());
//            authUserEntity.setUsername(user.username());
//            authUserEntity.setPassword(user.password());
//            authUserEntity.setEnabled(true);
//            authUserEntity.setAccountNonExpired(true);
//            authUserEntity.setAccountNonLocked(true);
//            authUserEntity.setCredentialsNonExpired(true);
//            UserEntity createdAuthUser = authUserRepository.create(authUserEntity);
//            AuthAuthorityEntity[] userAuthorities = Arrays.stream(AuthorityType.values()).map(
//                    e -> {
//                        AuthAuthorityEntity ae = new AuthAuthorityEntity();
//                        ae.setId(createdAuthUser.getId());
//                        ae.setAuthority(AuthorityType.valueOf(String.valueOf(e)));
//                        return ae;
//                    }).toArray(AuthAuthorityEntity[]::new);
//                    authUserRepository.create(userAuthorities);
//            return UserJson.fromEntity(
//                    userdataUserRepository.createUser(UserEntity.fromJson(String.valueOf(user))),
//                    null);
//        }
//        );
//    }
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

