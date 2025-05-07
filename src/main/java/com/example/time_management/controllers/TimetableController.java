package com.example.time_management.controllers;

import com.example.time_management.Util.JwtTokenUtil;
import com.example.time_management.Util.TokenUtil;
import com.example.time_management.dto.ApiResponse;
import com.example.time_management.dto.TimetableResponse;
import com.example.time_management.exceptions.TokenInvalid;
import com.example.time_management.services.TimetableService;

import ch.qos.logback.core.subst.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/Ctable")
public class TimetableController {
    private final TimetableService service;

    @Autowired
    public TimetableController(TimetableService service) {
        this.service = service;
    }

    /**
     * 上传解析 PDF，返回解析后的 JSON
     */
    @PostMapping
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("termStart") String termStart
    ){
        try{
            long userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            TimetableResponse resp = service.parseAndSave(file, userId,termStart);
            return ResponseEntity.ok(resp);
        }catch(IOException e){
            System.out.println(111111);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }

    /**
     * 获取指定用户的所有课程表
     */
    @GetMapping
    public ResponseEntity<?> getByUser(@RequestHeader("Authorization") String authHeader){
        try{
            long userId=JwtTokenUtil.getIdFromToken(TokenUtil.extractToken(authHeader));
            TimetableResponse resp = service.getByUserId(userId);
            return ResponseEntity.ok(resp);
        }catch(IOException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }catch(TokenInvalid e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400,e.getMessage(),null));
        }
    }
}
