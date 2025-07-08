package guru.qa.niffler.data.dao;
import guru.qa.niffler.data.entity.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ParametersAreNonnullByDefault
public interface UserDataDao {
    @Nonnull
    UserEntity createUser(UserEntity user);
    @Nonnull
    UserEntity update(UserEntity user);
    @Nonnull
    Optional<UserEntity> findById(UUID id);
    @Nonnull
    List<UserEntity> findAll();
    @Nonnull
    Optional<UserEntity> findByUsername(String username);


    void delete(UserEntity user);
}
