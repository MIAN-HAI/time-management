package com.example.time_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.time_management.dto.UserPlanResponse;
import com.example.time_management.exceptions.InfoNotMatch;
import com.example.time_management.exceptions.UpdateObjectNotExists;
import com.example.time_management.models.User;
import com.example.time_management.models.UserPlan;
import com.example.time_management.repositories.UserPlansRepository;
import com.example.time_management.repositories.UserProcessRepository;

@Service
public class UserPlansService {
    private final UserPlansRepository userPlansRepository;
    private final UserProcessRepository userProcessRepository;

    @Autowired
    public UserPlansService(UserPlansRepository userPlansRepository,UserProcessRepository userProcessRepository) {
        this.userPlansRepository = userPlansRepository;
        this.userProcessRepository = userProcessRepository;
    }

    public List<UserPlanResponse> getUserPlans(Integer userId) {
        List<UserPlan> userPlans = userPlansRepository.findByUserId(userId);
        return userPlans.stream().map(UserPlanResponse::new).toList();
    }

    public UserPlan addPlan(UserPlan userPlan) {
        return userPlansRepository.save(userPlan);
    }

    public UserPlanResponse updatePlan(UserPlan userPlan) {
        Optional<UserPlan> object = userPlansRepository.findByPlanId(userPlan.getPlanId());
        if (object.isEmpty()) {
            throw new UpdateObjectNotExists("待更新的对象不存在");
        }
        if(userPlan.getUserId()!=object.get().getUserId()){
            throw new InfoNotMatch("用户信息不匹配");
        }
        userPlan.setUserId(object.get().getUserId());
        return new UserPlanResponse(userPlansRepository.save(userPlan));
    }

    public UserPlanResponse deletePlan(Integer planId,Integer userId) {
        Optional<UserPlan> object = userPlansRepository.findByPlanId(planId);
        if (object.isEmpty()) {
            throw new UpdateObjectNotExists("待删除的对象不存在");
        }
        if(userId!=object.get().getUserId()){
            throw new InfoNotMatch("用户信息不匹配");
        }
        userProcessRepository.deleteAllByPlanId(planId);
        userPlansRepository.deleteById(planId);
        return new UserPlanResponse(object.get());
    }
}
