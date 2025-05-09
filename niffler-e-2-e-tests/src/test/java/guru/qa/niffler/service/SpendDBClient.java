package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.sql.Connection;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDBClient {
    private static final Config CFG = Config.getInstance();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDao spendDao = new SpendDaoJdbc();

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
}
