package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import static guru.qa.niffler.data.tpl.Connections.holder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

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
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<AuthUserEntity> findAll() {

        List<AuthUserEntity> authUserEntities = new ArrayList<>();

        try(PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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
    public List<AuthUserEntity> findAllWithAuthorities() {
        return null;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        return null;
    }
}
