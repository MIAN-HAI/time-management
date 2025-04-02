package com.example.time_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.time_management.models.UserToDo;

@Repository
public interface UserToDoRepository extends JpaRepository<UserToDo, Integer> {
    Optional<UserToDo> findByTodoId(Integer todoId);
    List<UserToDo> findByUserId(Integer userId);
}
