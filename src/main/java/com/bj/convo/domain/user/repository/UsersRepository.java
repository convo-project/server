package com.bj.convo.domain.user.repository;

import com.bj.convo.domain.user.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    
    boolean existsByEmail(String email);
}
