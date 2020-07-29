package jp.kobe_u.cs27.app.meetingroomreservation.domain.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    
}