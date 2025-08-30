package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AuthUserRepository {
    AuthUserEntity create(AuthUserEntity authUserEntity);
    AuthUserEntity update(AuthUserEntity authUserEntity);
    Optional<AuthUserEntity> findById (UUID id);
    Optional<AuthUserEntity> findByUserName (String username);
    List<AuthUserEntity> findAll();
    void remove (AuthUserEntity user);
    void clear();
}
