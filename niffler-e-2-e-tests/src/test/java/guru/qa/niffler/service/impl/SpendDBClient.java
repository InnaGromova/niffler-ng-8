package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.*;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;
import java.util.UUID;

public class SpendDBClient implements SpendsClient {
    private final SpendRepository spendRepository = new SpendRepositoryHibernate();
    private static final Config CFG = Config.getInstance();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDao spendDao = new SpendDaoJdbc();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private final JdbcTransactionTemplate jdbcTrTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public SpendJson createSpend (SpendJson spend){
        return jdbcTrTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory() != null) {
                        CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            spendDao.create(spendEntity)
                    );
                }
        );
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) throws Exception {
        return xaTransactionTemplate.execute(()->{
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                spendRepository.findCategoryByUsernameAndSpendName(
                        spendEntity.getCategory().getUsername(),
                        spendEntity.getCategory().getName()
                ).ifPresentOrElse(
                        spendEntity::setCategory,
                        () -> spendEntity.setCategory(
                                spendRepository.createCategory(spendEntity.getCategory())));
            }
            return SpendJson.fromEntity(spendRepository.update(SpendEntity.fromJson(spend)));
        });
    }

    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTrTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(
                            categoryDao.create(categoryEntity));
        }
        );
    }
    public CategoryJson updateCategory(CategoryJson category) {
        return jdbcTrTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(
                            categoryDao.create(categoryEntity));
                }
        );
    }

    @Override
    public void deleteSpend(SpendJson spendJson) throws Exception {
        xaTransactionTemplate.execute(()->{
            spendRepository.findById(spendJson.id()).ifPresent(
                    spendRepository::remove);
            return null;
        });
    }

    @Override
    public SpendJson findSpendById(UUID id) throws Exception {
        return xaTransactionTemplate.execute(()->
                spendRepository.findById(id)
                        .map(SpendJson::fromEntity)
                        .orElse(null)
        );
    }
}
