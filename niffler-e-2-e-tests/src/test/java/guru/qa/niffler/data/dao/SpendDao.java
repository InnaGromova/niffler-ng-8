package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    SpendEntity create(SpendEntity spend);
    List<SpendEntity> findAllByUsername(String username);
    Optional<SpendEntity> findSpendById(UUID id);
    void deleteSpend(SpendEntity spend);
}
