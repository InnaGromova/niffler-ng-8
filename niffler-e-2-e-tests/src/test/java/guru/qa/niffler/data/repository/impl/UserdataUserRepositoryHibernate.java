package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.FriendshipEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.values.FriendshipStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.*;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UserdataUserRepositoryHibernate implements UserDataRepository {
    private final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());
    @Override
    public UserEntity createUser(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }
    @Override
    public UserEntity updateUser(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.merge(user);
        return user;
    }
    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(UserEntity.class, id)
        );
    }
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("select u from UserEntity u where u.username =: username", UserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    @Override
    public List<UserEntity> findAll() {
        String hql = "FROM UserEntity";
        return entityManager.createQuery(hql, UserEntity.class)
                .getResultList();
    }
    @Override
    public void delete(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.remove(user);
    }
    @Override
    public void addFriend(UserEntity user, UserEntity friend) {
        entityManager.joinTransaction();
        user.addFriends(FriendshipStatus.ACCEPTED, friend);
        friend.addFriends(FriendshipStatus.ACCEPTED, user);
    }
    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        addressee.addFriends(FriendshipStatus.PENDING, requester);
        updateUser(requester);
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.PENDING, addressee);
        updateUser(requester);
    }

    @Override
    public void clear() {
        entityManager.clear();
    }
}
