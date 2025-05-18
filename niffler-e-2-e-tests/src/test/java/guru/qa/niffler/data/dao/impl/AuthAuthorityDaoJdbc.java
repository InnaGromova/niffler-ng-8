package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;

import static guru.qa.niffler.data.tpl.Connections.holder;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
    private static final Config CFG = Config.getInstance();
    @Override
    public AuthAuthorityEntity create(AuthAuthorityEntity[] authorities) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                        "VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthAuthorityEntity authority : authorities) {
                ps.setObject(1, authority.getId());
                ps.setString(2, String.valueOf(authority.getAuthority()));
                ps.addBatch();
            }

            ps.executeBatch();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            authorities[0].setId(generatedKey);
            return authorities[0];
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }

    @Override
    public List<AuthAuthorityEntity> findByUserId(UUID userId) {
        return null;
    }

    @Override
    public void delete(AuthAuthorityEntity authority) {

    }

}
