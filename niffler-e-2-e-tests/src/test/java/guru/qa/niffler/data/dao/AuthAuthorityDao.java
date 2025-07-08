package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.AuthAuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;
@ParametersAreNonnullByDefault
public interface AuthAuthorityDao {
    AuthAuthorityEntity create(AuthAuthorityEntity... authority);
    @Nonnull
    List<AuthAuthorityEntity> findByUserId(UUID userId);
    void delete(AuthAuthorityEntity authority);
}
