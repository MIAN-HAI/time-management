package com.example.time_management.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.sql.Update;
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
import com.example.time_management.dto.AddRequestForPlan;
import com.example.time_management.dto.AddRequestForProcess;
import com.example.time_management.dto.ApiResponse;
import com.example.time_management.dto.UserPlanResponse;
import com.example.time_management.dto.UserProcessResponse;
import com.example.time_management.exceptions.InfoNotMatch;
import com.example.time_management.exceptions.TokenInvalid;
import com.example.time_management.exceptions.UpdateObjectNotExists;
import com.example.time_management.models.User;
import com.example.time_management.models.UserPlan;
import com.example.time_management.models.UserProcess;
import com.example.time_management.services.UserPlansService;
import com.example.time_management.services.UserProcessService;

@RestController
@RequestMapping("/plans")
public class ContentForPlanController {
    private final UserPlansService userPlansService;
    private final UserProcessService userProcessService;

    @Autowired
    public ContentForPlanController(UserPlansService userPlansService,UserProcessService userProcessService) {
        this.userPlansService = userPlansService;
        this.userProcessService = userProcessService;
    }

    @GetMapping
    public ResponseEntity<?> getPlans(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = TokenUtil.extractToken(authHeader);
            Integer userId = JwtTokenUtil.getIdFromToken(token);
            return ResponseEntity
                    .ok(new ApiResponse<List<UserPlanResponse>>(200, "请求成功", userPlansService.getUserPlans(userId)));
        } catch (TokenInvalid e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<?> addPlan(@RequestHeader("Authorization") String authHeader,
            @RequestBody AddRequestForPlan request) {
        try {
            String token = TokenUtil.extractToken(authHeader);
            Integer userId = JwtTokenUtil.getIdFromToken(token);
            UserPlan userPlans = userPlansService.addPlan(new UserPlan(null, userId, request.getTitle(),
                    request.getStartTime(),
                    request.getEndTime(), request.isCompleted(), LocalDateTime.now(), request.getDescription()));
            return ResponseEntity.ok(new ApiResponse<UserPlanResponse>(201, "学习计划创建成功", new UserPlanResponse(userPlans)));
        } catch (TokenInvalid e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/{planId}")
    public ResponseEntity<?> updatePlan(@PathVariable Integer planId,@RequestHeader("Authorization") String authHeader,
            @RequestBody AddRequestForPlan updateRequest) {
        try {
            String token = TokenUtil.extractToken(authHeader);
            Integer userId = JwtTokenUtil.getIdFromToken(token);
            return ResponseEntity.ok(new ApiResponse<UserPlanResponse>(201, "学习计划更新成功",
                    userPlansService
                            .updatePlan(new UserPlan(planId, userId, updateRequest.getTitle(), updateRequest.getStartTime(),
                                    updateRequest.getEndTime(), updateRequest.isCompleted(), LocalDateTime.now(),
                                    updateRequest.getDescription()))));
        } catch (TokenInvalid e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400, e.getMessage(), null));
        } catch (UpdateObjectNotExists e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }catch(InfoNotMatch e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deletePlan(@PathVariable Integer planId,@RequestHeader("Authorization") String authHeader) {
        try {
            String token = TokenUtil.extractToken(authHeader);
            Integer userId = JwtTokenUtil.getIdFromToken(token);
            return ResponseEntity.ok(new ApiResponse<UserPlanResponse>(201, "学习计划删除成功",
                    userPlansService.deletePlan(planId, userId)));
        } catch (TokenInvalid e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400, e.getMessage(), null));
        }catch (UpdateObjectNotExists e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/processes")
    public ResponseEntity<?> getProcesses(@RequestHeader("Authorization") String authHeader) {
        try{
            Integer userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            return ResponseEntity.ok(new ApiResponse<List<UserProcessResponse>>(200,"学习过程数据生成完成",userProcessService.getProcesses(userId)));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }

    @PostMapping("/{planId}/processes")
    public ResponseEntity<?> addProcess(@PathVariable Integer planId,@RequestHeader("Authorization") String authHeader,@RequestBody AddRequestForProcess request) {
        try{
            Integer userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            return ResponseEntity.ok(new ApiResponse<UserProcessResponse>(201,"学习过程创建成功",userProcessService.addProcess(new UserProcess(null,planId,request.getContent(),request.getCompletedTime(),LocalDateTime.now()),userId)));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }

    @PutMapping("/{planId}/processes/{processId}")
    public ResponseEntity<?> updateProcess(@PathVariable Integer processId,@PathVariable Integer planId,@RequestHeader("Authorization") String authHeader,
            @RequestBody AddRequestForProcess updateRequest) {
                try{
                    Integer userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
                    return ResponseEntity.ok(new ApiResponse<UserProcessResponse>(201,"学习过程更新成功",userProcessService.updateProcess(new UserProcess(processId,planId,updateRequest.getContent(),updateRequest.getCompletedTime(),null),userId)));
                }catch(TokenInvalid e){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400,e.getMessage(),null));
                }catch(UpdateObjectNotExists e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
                }catch(InfoNotMatch e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
                }
    }

    @DeleteMapping("/{planId}/processes/{processId}")
    public ResponseEntity<?> deleteProcess(@PathVariable Integer processId,@PathVariable Integer planId,@RequestHeader("Authorization") String authHeader) {
        try{
            Integer userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            return ResponseEntity.ok(new ApiResponse<UserProcessResponse>(201,"学习过程删除成功",userProcessService.deleteProcess(processId,userId)));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400,e.getMessage(),null));
        }catch(UpdateObjectNotExists e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }catch(InfoNotMatch e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }

}
