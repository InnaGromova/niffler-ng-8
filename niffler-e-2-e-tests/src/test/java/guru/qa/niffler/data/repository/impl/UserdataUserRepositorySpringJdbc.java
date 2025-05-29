package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDataDao;
import guru.qa.niffler.data.dao.impl.UserDataDaoSpringJdbc;
import guru.qa.niffler.data.entity.FriendshipEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.mapper.UserDataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.values.FriendshipStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserdataUserRepositorySpringJdbc implements UserDataRepository {
    private static final Config CFG = Config.getInstance();
    private final UserDataDao userdataDao = new UserDataDaoSpringJdbc();
    @Override
    public UserEntity createUser(UserEntity user) {
        return userdataDao.createUser(user);
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        return userdataDao.update(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID uuid) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.query(
                "SELECT u.*, f.*" + "FROM \"user\" u " +
                        "LEFT JOIN friendship f ON u.id = f.requester_id OR u.id = f.addressee_id " +
                        "WHERE u.id = ?",
                this::extractUserWithFriendships,
                uuid)
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.query(
                "SELECT u.*, f.*" + "FROM \"user\" u " +
                        "LEFT JOIN friendship f ON u.id = f.requester_id OR u.id = f.addressee_id " +
                        "WHERE u.username = ?",
                this::extractUserWithFriendships,
                username)
        );
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                UserDataUserEntityRowMapper.instance
        );
    }

    @Override
    public void delete(UserEntity user) {
        userdataDao.delete(user);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)",
                Arrays.asList(
                        getFriendshipRecord(requester.getId(), addressee.getId(), FriendshipStatus.ACCEPTED),
                        getFriendshipRecord(addressee.getId(), requester.getId(), FriendshipStatus.ACCEPTED))
        );
    }
    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)",
                getFriendshipRecord(requester.getId(), addressee.getId(), FriendshipStatus.PENDING)
        );
    }
    public Object[] getFriendshipRecord(UUID requesterId, UUID addresseerId, FriendshipStatus status) {
        return new Object[]{requesterId, addresseerId, String.valueOf(status), new java.sql.Date(System.currentTimeMillis())};
    }
    private UserEntity extractUserWithFriendships(ResultSet rs) throws SQLException {
        Map<UUID, UserEntity> userMap = new ConcurrentHashMap<>();
        UUID userId = null;
        List<FriendshipEntity> friendshipAddressees = new ArrayList<>();
        List<FriendshipEntity> friendshipRequests = new ArrayList<>();
        while (rs.next()) {
            userId = rs.getObject("id", UUID.class);
            UserEntity user = userMap.computeIfAbsent(userId, key -> {
                try {
                    return UserDataUserEntityRowMapper.instance.mapRow(rs, 1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            if (rs.getString("status") != null) {
                FriendshipEntity friendship = new FriendshipEntity();
                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                friendship.setCreatedDate(new Date(rs.getTimestamp("created_date").getTime()));
                UserEntity userEntity = new UserEntity();
                UUID addresseeId = rs.getObject("addressee_id", UUID.class);
                UUID requesterId = rs.getObject("requester_id", UUID.class);
                if (addresseeId.equals(userId)) {
                    friendship.setAddressee(user);
                    userEntity.setId(requesterId);
                    friendship.setRequester(userEntity);
                    friendshipAddressees.add(friendship);
                } else {
                    userEntity.setId(addresseeId);
                    friendship.setAddressee(userEntity);
                    friendship.setRequester(user);
                    friendshipRequests.add(friendship);
                }
            }
        }
        UserEntity user = userMap.get(userId);
        if (user != null) {
            user.setFriendshipAddressees(friendshipAddressees);
            user.setFriendshipRequests(friendshipRequests);
        }
        return user;
    }
}
