package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.AuthAuthorityEntity;

import java.util.List;
import java.util.UUID;

public interface AuthAuthorityDao {
    AuthAuthorityEntity create(AuthAuthorityEntity... authority);
    List<AuthAuthorityEntity> findByUserId(UUID userId);
    void delete(AuthAuthorityEntity authority);
}
