package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserDataDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.spend.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.NoSuchElementException;
import java.util.UUID;

public class UserDBClient {
    private final UserDataDao userDao = new UserdataUserDaoJdbc();

    public UserJson createUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        return UserJson.fromEntity(
                userDao.createUser(userEntity)
        );
    }
    public void delete(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        userDao.delete(userEntity);
    }
    public UserJson findByUsername(String username) {
        return userDao.findByUsername(username)
                .map(UserJson::fromEntity).orElseThrow(
                        () -> new NoSuchElementException("Can`t find user.")
                );
    }
    public UserJson findById(UUID id) {
        return userDao.findById(id)
                .map(UserJson::fromEntity).orElseThrow(
                        () -> new NoSuchElementException("Can`t find user.")
                );
    }
}
