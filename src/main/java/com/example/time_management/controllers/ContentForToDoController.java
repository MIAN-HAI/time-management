package com.example.time_management.controllers;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.time_management.Util.JwtTokenUtil;
import com.example.time_management.Util.TokenUtil;
import com.example.time_management.dto.AddRequestForToDo;
import com.example.time_management.dto.ApiResponse;
import com.example.time_management.dto.UpdateRequestForToDo;
import com.example.time_management.dto.UserToDoResponse;
import com.example.time_management.exceptions.InfoNotMatch;
import com.example.time_management.exceptions.TokenInvalid;
import com.example.time_management.exceptions.UpdateObjectNotExists;
import com.example.time_management.models.UserToDo;
import com.example.time_management.services.UserToDoService;


@RestController
@RequestMapping("/todos")
public class ContentForToDoController {
    UserToDoService userToDoService;

    @Autowired
    public ContentForToDoController(UserToDoService userToDoService) {
        this.userToDoService = userToDoService;
    }

    @GetMapping
    public ResponseEntity<?> getToDos(@RequestHeader("Authorization") String authHeader) {
        try{
            Integer userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            return ResponseEntity.ok(new ApiResponse<List<UserToDoResponse>>(200,"加载成功",userToDoService.getUserToDos(userId)));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }

    @PostMapping
    public ResponseEntity<?> addToDo(@RequestHeader("Authorization") String authHeader,@RequestBody AddRequestForToDo request) {
        try{
            Integer userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            return ResponseEntity.ok(new ApiResponse<UserToDoResponse>(201,"待办事项创建成功",
            userToDoService.addToDo(new UserToDo(null,userId,request.getPriority(),request.getTitle(),request.getDeadline(),request.getReminderTime(),request.isCompleted(),LocalDateTime.now(),LocalDateTime.now(),request.getDescription()))));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<?> updateToDo(@PathVariable Integer todoId,@RequestHeader("Authorization") String authHeader,@RequestBody UpdateRequestForToDo request) {
        try{
            Integer userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            return ResponseEntity.ok(new ApiResponse<UserToDoResponse>(201,"待办事项更新成功",
            userToDoService.updateToDo(new UserToDo(todoId,userId,request.getPriority(),request.getTitle(),request.getDeadline(),request.getReminderTime(),request.isCompleted(),request.getUpdatedAt(),LocalDateTime.now(),request.getDescription()))));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400,e.getMessage(),null));
        }catch(UpdateObjectNotExists e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }catch(InfoNotMatch e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }

    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<?> deleteToDo(@PathVariable Integer todoId,@RequestHeader("Authorization") String authHeader) {
        try{
            Integer id=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            return ResponseEntity.ok(new ApiResponse<UserToDoResponse>(201,"待办事项删除成功",userToDoService.deleteToDo(todoId,id)));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400,e.getMessage(),null));
        }catch(UpdateObjectNotExists e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }catch(InfoNotMatch e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }

}
