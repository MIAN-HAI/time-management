package com.example.time_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.time_management.models.UserProcess;

public interface UserProcessRepository extends JpaRepository<UserProcess, Integer> {
    Optional<UserProcess> findByProcessId(Integer processId);

    List<UserProcess> findByPlanId(Integer planId);

    List<UserProcess> findByPlanIdIn(List<Integer> planIds);

}
