package jp.kobe_u.es4.app.meetingroomreservation.domain.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.User;

/**
 * ユーザリポジトリ
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {
    
}