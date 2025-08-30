package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.FriendshipEntity;
import guru.qa.niffler.values.FriendshipStatus;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;
@ParametersAreNonnullByDefault
public interface FriendshipDao {
    @Nonnull
    void create(FriendshipEntity... friendships);
    @Nonnull
    List<FriendshipEntity> findAllByUserId(UUID userId);
    @Nonnull
    List<FriendshipEntity> findAllByFriendId(UUID friendId);
    @Nonnull
    void updateStatus(UUID id, FriendshipStatus status);
    void deleteAll();
}
