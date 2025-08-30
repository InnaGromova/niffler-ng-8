package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.*;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.extractor.AuthUserWithAuthoritiesExtractor;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ParametersAreNonnullByDefault
public class AuthUsersDaoSpringJdbc implements AuthUserDao {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    @Nonnull
    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, authUser.getUsername());
            ps.setString(2, ENCODER.encode(authUser.getPassword()));
            ps.setBoolean(3, authUser.getEnabled());
            ps.setBoolean(4, authUser.getAccountNonExpired());
            ps.setBoolean(5, authUser.getAccountNonLocked());
            ps.setBoolean(6,  authUser.getCredentialsNonExpired());
            return ps;
        }, kh);
        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        authUser.setId(generatedKey);
        return authUser;
    }
    @Nonnull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        AuthUserEntityRowMapper.instance,
                        id
                )
        );
    }
    @Nonnull
    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.instance
        );
    }

    @Override
    public List<AuthUserEntity> findAllWithAuthorities() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT u.id as user_id, u.username, u.password, u.enabled, " +
                        "u.account_non_expired, u.account_non_locked, u.credentials_non_expired, " +
                        "a.id as authority_id, a.authority, a.user_id as authority_user_id " +
                        "FROM \"user\" u " +
                        "LEFT JOIN authority a ON u.id = a.user_id",
                new AuthUserWithAuthoritiesExtractor()
        );
    }
    @Nonnull
    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.update(
                "UPDATE \"user\" SET " +
                        "username = ?, password = ?, enabled = ?, " +
                        "account_non_expired = ?, account_non_locked = ?, " +
                        "credentials_non_expired = ? " +
                        "WHERE id = ?",
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                user.getId()
        );
        return user;
    }
}
