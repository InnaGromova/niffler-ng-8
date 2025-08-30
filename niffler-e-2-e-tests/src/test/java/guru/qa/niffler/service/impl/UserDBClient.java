package guru.qa.niffler.service.impl;


import static guru.qa.niffler.utils.RandomData.randomUserName;

import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.model.Currency;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomData;
import jaxb.userdata.FriendshipStatus;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

public class UserDBClient implements UsersClient {
    private static final Config CFG = Config.getInstance();
    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserDataRepository userDataRepository = new UserdataUserRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(), CFG.userdataJdbcUrl()
    );
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public UserJson createUser(String username, String password) {
        try {
            return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                        AuthUserEntity authUser = authUserEntity(username, password);
                        authUserRepository.create(authUser);
                        return UserJson.fromEntity(
                                userDataRepository.createUser(userEntity(username)),
                                null
                        );
                    }
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<UserJson> createFriends(UserJson user, int count) throws Exception {

        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userDataRepository.findById(user.id())
                    .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomData.randomUserName();
                    UserEntity friend = UserEntity.fromJson(createUser(username, "12345"));
                    result.add(UserJson.fromEntity(friend, FriendshipStatus.FRIEND));
                    userDataRepository.addFriend(targetEntity, friend);
                    return null;
                });
            }
        }
        return result;
    }
    @Override
    public List<UserJson> createIncomeInvitations(UserJson requester, int count) throws Exception {
        if (count > 0) {
            UserEntity targetEntity = userDataRepository.findById(requester.id())
                    .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomData.randomUserName();
                    AuthUserEntity authUser = authUserEntity(username, "test-user5");
                    authUserRepository.create(authUser);
                    UserEntity income = userDataRepository.createUser(userEntity(username));
                    userDataRepository.addIncomeInvitation(targetEntity, income);
                    requester.testData().incomeInvitations().add(UserJson.fromEntity(income, FriendshipStatus.INVITE_RECEIVED));
                    return null;
                });
            }
        }
        return null;
    }
    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) throws Exception {
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
                    userDataRepository.addOutcomeInvitation(addressee, targetEntity);
                    targetUser.testData().outcomeInvitations().add(UserJson.fromEntity(addressee, FriendshipStatus.INVITE_SENT));
                            return null;
                        }
                );
            }
        }
        return null;
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

    @Override
    public List<UserJson> allUsers(String username, String searchQuery) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UserJson> getFriends(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UserJson> getIncomeInvitations(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UserJson> getOutcomeInvitations(String username, String searchQuery) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(passwordEncoder.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
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
        userEntity.setCurrency(Currency.RUB);
        return userEntity;
    }

}
