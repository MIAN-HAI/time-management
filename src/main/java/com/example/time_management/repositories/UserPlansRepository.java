package com.example.time_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.time_management.models.UserPlan;

@Repository
public interface UserPlansRepository extends JpaRepository<UserPlan, Integer> {
    List<UserPlan> findByUserId(Integer userId);

    

    // 被迫使用title重载
    Optional<UserPlan> findByPlanId(Integer planId);
}
