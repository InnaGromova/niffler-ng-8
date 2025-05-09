package guru.qa.niffler.data.dao;


import guru.qa.niffler.data.entity.AuthAuthorityEntity;

public interface AuthAuthorityDao {
    AuthAuthorityEntity create(AuthAuthorityEntity... authority);
}
