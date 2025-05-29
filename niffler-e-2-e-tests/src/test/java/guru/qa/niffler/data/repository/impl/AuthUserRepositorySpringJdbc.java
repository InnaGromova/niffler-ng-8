package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {
    private static final Config CFG = Config.getInstance();
    private final AuthUserDao authUserDao = new AuthUsersDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final JdbcTemplate jdbcTemplate =
            new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    @Override
    public AuthUserEntity create(AuthUserEntity authUserEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, authUserEntity.getUsername());
            ps.setString(2, authUserEntity.getPassword());
            ps.setBoolean(3, authUserEntity.getEnabled());
            ps.setBoolean(4, authUserEntity.getAccountNonExpired());
            ps.setBoolean(5, authUserEntity.getAccountNonLocked());
            ps.setBoolean(6, authUserEntity.getCredentialsNonExpired());
            return ps;
        }, kh);
        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        authUserEntity.setId(generatedKey);
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authUserEntity.getAuthorities().get(i).getUser().getId());
                        ps.setString(2, authUserEntity.getAuthorities().get(i).getAuthority().name());
                    }
                    @Override
                    public int getBatchSize() {
                        return authUserEntity.getAuthorities().size();
                    }
                }
        );
        return authUserEntity;
    }
    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        AuthUserEntity updatedUser = authUserDao.update(user);
                update(user);

        List<AuthAuthorityEntity> existingAuthorities = authAuthorityDao.findByUserId(user.getId());
        existingAuthorities.forEach(authAuthorityDao::delete);

        if (!user.getAuthorities().isEmpty()) {
            user.getAuthorities().forEach(a -> a.setUser(updatedUser));
            authAuthorityDao.create(user.getAuthorities().toArray(new AuthAuthorityEntity[0]));
        }
        return updatedUser;
    }
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM \"user\" WHERE id = ?",
                            new Object[]{UUID.fromString(String.valueOf(id))},
                            new AuthUserEntityRowMapper()
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    @Override
    public Optional<AuthUserEntity> findByUserName(String username) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM \"user\" WHERE username = ?",
                            new Object[]{username},
                            new AuthUserEntityRowMapper()
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.instance
        );
    }
    @Override
    public void remove(AuthUserEntity user) {
        jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", user.getId());
    }
}
