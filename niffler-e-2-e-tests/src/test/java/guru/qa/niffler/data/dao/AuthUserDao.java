package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.AuthUserEntity;

public interface AuthUserDao {
    AuthUserEntity create(AuthUserEntity authUser);
}
