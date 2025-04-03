package com.example.time_management.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.time_management.dto.UserProcessResponse;
import com.example.time_management.exceptions.InfoNotMatch;
import com.example.time_management.exceptions.UpdateObjectNotExists;
import com.example.time_management.models.UserPlan;
import com.example.time_management.models.UserProcess;
import com.example.time_management.repositories.UserPlansRepository;
import com.example.time_management.repositories.UserProcessRepository;

@Service
public class UserProcessService {
    private final UserProcessRepository userProcessRepository;
    private final UserPlansRepository userPlansRepository;
    @Autowired
    public UserProcessService(UserProcessRepository userProcessRepository, UserPlansRepository userPlansRepository) {
        this.userProcessRepository = userProcessRepository;
        this.userPlansRepository = userPlansRepository;
    }


    public List<UserProcessResponse> getProcesses(Integer userId){
        List<Integer> userPlans = userPlansRepository.findByUserId(userId).stream().map(UserPlan::getPlanId).toList();
        return userProcessRepository.findByPlanIdIn(userPlans).stream().map(UserProcessResponse::new).toList();
    }

    public UserProcessResponse addProcess(UserProcess userProcess,Integer userId){
        return new UserProcessResponse(userProcessRepository.save(userProcess));
    }

    public UserProcessResponse updateProcess(UserProcess userProcess,Integer userId){
        Optional<UserProcess> optionalUserProcess = userProcessRepository.findByProcessId(userProcess.getProcessId());
        Optional<UserPlan> optionalUserPlan = userPlansRepository.findByPlanId(userProcess.getPlanId());
        if(optionalUserProcess.isEmpty()||optionalUserPlan.isEmpty()){
            throw new UpdateObjectNotExists("待更新的对象不存在");
        }
        if(optionalUserPlan.get().getUserId()!=userId){
            throw new InfoNotMatch("用户信息不匹配");
        }
        userProcess.setCreateAt(optionalUserProcess.get().getCreateAt());
        return new UserProcessResponse(userProcessRepository.save(userProcess));
    }

    public UserProcessResponse deleteProcess(Integer processId,Integer userId){
        Optional<UserProcess> optionalUserProcess = userProcessRepository.findByProcessId(processId);
        Optional<UserPlan> optionalUserPlan = userPlansRepository.findByPlanId(optionalUserProcess.get().getPlanId());
        if(optionalUserProcess.isEmpty()||optionalUserPlan.isEmpty()){
            throw new UpdateObjectNotExists("待删除的对象不存在");
       }
        if(optionalUserPlan.get().getUserId()!=userId){
            throw new InfoNotMatch("用户信息不匹配");
        }
        userProcessRepository.deleteById(processId);
        return new UserProcessResponse(optionalUserProcess.get());
    }

}
