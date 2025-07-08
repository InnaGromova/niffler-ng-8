package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDataRepository {
    UserEntity createUser(UserEntity user);
    UserEntity updateUser(UserEntity user);
    Optional<UserEntity> findById(UUID uuid);
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAll();
    void delete(UserEntity user);
    void addFriend(UserEntity requester, UserEntity addressee);

    void addIncomeInvitation(UserEntity requester, UserEntity addressee);

    void addOutcomeInvitation(UserEntity requester, UserEntity addressee);
}
