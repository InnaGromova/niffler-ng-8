package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final JdbcTemplate jdbcTemplate =
            new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
            try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled,account_non_expired," +
                            "account_non_locked,credentials_non_expired)" +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, authUser.getUsername());
                ps.setString(2, ENCODER.encode(authUser.getPassword()));
                ps.setBoolean(3, authUser.getEnabled());
                ps.setBoolean(4, authUser.getAccountNonExpired());
                ps.setBoolean(5, authUser.getAccountNonLocked());
                ps.setBoolean(6, authUser.getCredentialsNonExpired());
                ps.executeUpdate();
                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else throw new SQLException("Can't find id in ResultSet");
                }
                authUser.setId(generatedKey);
                return authUser;
            }
            catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthUserEntity update(AuthUserEntity authUserEntity) {
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET " +
                        "username = ?," +
                        "password = ?," +
                        "enabled = ?," +
                        "account_non_expired = ?," +
                        "account_non_locked = ?," +
                        "credentials_non_expired = ?" +
                        "WHERE id = ?"
        )) {
            ps.setString(1, authUserEntity.getUsername());
            ps.setString(2, authUserEntity.getPassword());
            ps.setBoolean(3, authUserEntity.getEnabled());
            ps.setBoolean(4, authUserEntity.getAccountNonExpired());
            ps.setBoolean(5, authUserEntity.getAccountNonLocked());
            ps.setBoolean(6, authUserEntity.getCredentialsNonExpired());
            ps.setObject(7, authUserEntity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authUserEntity;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "SELECT * FROM auth.auth_user WHERE id = ?")) {
            ps.setObject(1, UUID.fromString(String.valueOf(id)));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(AuthUserEntityRowMapper.instance.mapRow(rs, 1));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID", e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUserName(String username) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM auth.auth_user WHERE username = ?",
                            AuthUserEntityRowMapper.instance,
                            username
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {

        List<AuthUserEntity> authUserEntities = new ArrayList<>();

        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {

                while (rs.next()) {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setId(rs.getObject("id", UUID.class));
                    authUser.setUsername(rs.getString("username"));
                    authUser.setPassword(rs.getString("password"));
                    authUser.setEnabled(rs.getBoolean("enabled"));
                    authUser.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUser.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUser.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    authUserEntities.add(authUser);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authUserEntities;
    }
    @Override
    public void remove(AuthUserEntity authUserEntity) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, authUserEntity.getId());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

