package com.example.time_management.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.time_management.dto.UserToDoResponse;
import com.example.time_management.exceptions.UpdateObjectNotExists;
import com.example.time_management.models.UserToDo;
import com.example.time_management.repositories.UserToDoRepository;

@Service
public class UserToDoService {
    private final UserToDoRepository userToDoRepository;

    @Autowired
    public UserToDoService(UserToDoRepository userToDoRepository) {
        this.userToDoRepository = userToDoRepository;
    }

    public UserToDoResponse addToDo(UserToDo userToDo) {
        return new UserToDoResponse(userToDoRepository.save(userToDo));
    }

    public List<UserToDoResponse> getUserToDos(Integer userId) {
        return userToDoRepository.findByUserId(userId).stream().map(UserToDoResponse::new).toList();
    }

    public UserToDoResponse updateToDo(UserToDo userToDo) {
        java.util.Optional<UserToDo> object = userToDoRepository.findById(userToDo.getTodoId());
        if (object.isEmpty()) {
            throw new UpdateObjectNotExists("待更新的对象不存在");
        }
        return new UserToDoResponse(userToDoRepository.save(userToDo));
    }
}
