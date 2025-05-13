package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.AuthUserEntity;
import java.util.List;


public interface AuthUserRepository {
    AuthUserEntity create(AuthUserEntity authUserEntity);
    List<AuthUserEntity> findAll();
}
