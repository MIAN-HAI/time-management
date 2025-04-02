package com.example.time_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.time_management.dto.UserPlanResponse;
import com.example.time_management.exceptions.UpdateObjectNotExists;
import com.example.time_management.models.UserPlan;
import com.example.time_management.repositories.UserPlansRepository;

@Service
public class UserPlansService {
    private final UserPlansRepository userPlansRepository;

    @Autowired
    public UserPlansService(UserPlansRepository userPlansRepository) {
        this.userPlansRepository = userPlansRepository;
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
        userPlan.setUserId(object.get().getUserId());
        return new UserPlanResponse(userPlansRepository.save(userPlan));
    }
}
