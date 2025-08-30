package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
@ParametersAreNonnullByDefault
public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {
    private static final Config CFG = Config.getInstance();
    @Nonnull
    @Override
    public AuthAuthorityEntity create(AuthAuthorityEntity... authorities) {
        for (AuthAuthorityEntity authority : authorities) {
            if (authority.getId() == null) {
                throw new IllegalArgumentException("User ID cannot be null for authority: " + authority.getAuthority());
            }
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority(user_id, authority) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authorities[i].getId());
                        ps.setString(2, String.valueOf(authorities[i].getAuthority()));
                    }

                    @Override
                    public int getBatchSize() {
                        return authorities.length;
                    }
                }
        );
        return authorities[0];
    }
    @Nonnull
    @Override
    public List<AuthAuthorityEntity> findByUserId(UUID userId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM authority WHERE user_id = ?",
                AuthorityEntityRowMapper.instance,
                userId
        );
    }
    @Override
    public void delete(AuthAuthorityEntity authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.update(
                "DELETE FROM authority WHERE id = ?",
                authority.getId()
        );
    }
}
