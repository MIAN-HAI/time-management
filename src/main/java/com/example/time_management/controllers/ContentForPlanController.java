package com.example.time_management.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.time_management.Util.JwtTokenUtil;
import com.example.time_management.Util.TokenUtil;
import com.example.time_management.dto.AddRequestForPlan;
import com.example.time_management.dto.ApiResponse;
import com.example.time_management.dto.UserPlanResponse;
import com.example.time_management.exceptions.TokenInvalid;
import com.example.time_management.exceptions.UpdateObjectNotExists;
import com.example.time_management.models.UserPlan;
import com.example.time_management.services.UserPlansService;

@RestController
@RequestMapping("/plans")
public class ContentForPlanController {
    UserPlansService userPlansService;

    @Autowired
    public ContentForPlanController(UserPlansService userPlansService) {
        this.userPlansService = userPlansService;
    }

    @GetMapping
    public ResponseEntity<?> getPlans(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = TokenUtil.extractToken(authHeader);
            Integer id = JwtTokenUtil.getIdFromToken(token);
            return ResponseEntity
                    .ok(new ApiResponse<List<UserPlanResponse>>(200, "获取成功", userPlansService.getUserPlans(id)));
        } catch (TokenInvalid e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<?> addPlan(@RequestHeader("Authorization") String authHeader,
            @RequestBody AddRequestForPlan request) {
        try {
            String token = TokenUtil.extractToken(authHeader);
            Integer id = JwtTokenUtil.getIdFromToken(token);
            UserPlan userPlans = userPlansService.addPlan(new UserPlan(null, id, request.getTitle(),
                    request.getStartTime(),
                    request.getEndTime(), request.isCompleted(), LocalDateTime.now(), request.getDescription()));
            return ResponseEntity.ok(new ApiResponse<UserPlanResponse>(200, "添加成功", new UserPlanResponse(userPlans)));
        } catch (TokenInvalid e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/{planId}")
    public ResponseEntity<?> updatePlan(@RequestHeader("Authorization") String authHeader,
            @RequestBody AddRequestForPlan updateRequest) {
        try {
            String token = TokenUtil.extractToken(authHeader);
            Integer id = JwtTokenUtil.getIdFromToken(token);
            return ResponseEntity.ok(new ApiResponse<UserPlanResponse>(200, "更新成功",
                    userPlansService
                            .updatePlan(new UserPlan(id, null, updateRequest.getTitle(), updateRequest.getStartTime(),
                                    updateRequest.getEndTime(), updateRequest.isCompleted(), LocalDateTime.now(),
                                    updateRequest.getDescription()))));
        } catch (TokenInvalid e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(400, e.getMessage(), null));
        } catch (UpdateObjectNotExists e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
