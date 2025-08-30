package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.model.CurrencyValues;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import static guru.qa.niffler.data.tpl.Connections.holder;
@ParametersAreNonnullByDefault
public class SpendDaoJdbc implements SpendDao {
    private static final Config CFG = Config.getInstance();
    @Nonnull
    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)" +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, spend.getSpendDate());
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Nonnull
    @Override
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "UPDATE spend SET username = ?, spend_date = ?, currency = ?, " +
                        "amount = ?, description = ?, category_id = ? " +
                        "WHERE id = ? "
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.setObject(7, spend.getId());
            ps.executeUpdate();
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Nonnull
    @Override
    public Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ? AND description = ?")) {
            ps.setObject(1, username);
            ps.setString(2, description);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return rs.next() ? Optional.of(mapResultSetToSpendEntity(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Nonnull
    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setSpendDate(rs.getObject("spend_date", Date.class));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(null);
                    return Optional.of(se);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Nonnull
    @Override
    public List<SpendEntity> findAllByUsername(String username) {
            try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                    "SELECT * FROM spend WHERE username = ?"
            )) {
                ps.setObject(1, username);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    List<SpendEntity> spendList = new ArrayList<>();
                    while (rs.next()) {
                        SpendEntity se = new SpendEntity();
                        se.setId(rs.getObject("id", UUID.class));
                        se.setUsername(rs.getString("username"));
                        se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        se.setSpendDate(rs.getObject("spend_date", Date.class));
                        se.setAmount(rs.getDouble("amount"));
                        se.setDescription(rs.getString("description"));
                        se.setCategory(null);
                        spendList.add(se);
                    }
                    return spendList;
                }
            }
            catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Nonnull
    @Override
    public List<SpendEntity> findAll() {
        List<SpendEntity> spendEntities = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement("SELECT * FROM spend");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpendEntity spendEntity = SpendEntityRowMapper.instance.mapRow(rs, rs.getRow());
                spendEntities.add(spendEntity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all spend entities", e);
        }
        return spendEntities;
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
            try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                    "DELETE FROM spend WHERE id = ?"
            )) {
                ps.setObject(1, spend.getId());
                ps.executeUpdate();
            }
            catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private SpendEntity mapResultSetToSpendEntity(ResultSet rs) {
        try {
            SpendEntity spendEntity = new SpendEntity();
            CategoryEntity categoryEntity = new CategoryEntity();
            spendEntity.setId(rs.getObject("id", UUID.class));
            spendEntity.setUsername(rs.getString("username"));
            spendEntity.setSpendDate(rs.getDate("spend_date"));
            spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            spendEntity.setAmount(rs.getDouble("amount"));
            spendEntity.setDescription(rs.getString("description"));
            categoryEntity.setId(rs.getObject("category_id", UUID.class));
            spendEntity.setCategory(categoryEntity);
            return spendEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
