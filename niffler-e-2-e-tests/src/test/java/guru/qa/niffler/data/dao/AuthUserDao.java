package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.AuthUserEntity;

import java.util.List;

public interface AuthUserDao {
    AuthUserEntity create(AuthUserEntity authUser);

    List<AuthUserEntity> findAll();
    List<AuthUserEntity> findAllWithAuthorities();
}
