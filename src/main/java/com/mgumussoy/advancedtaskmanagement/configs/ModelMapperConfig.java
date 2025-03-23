package com.mgumussoy.advancedtaskmanagement.configs;

import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.entities.TaskEntity;
import com.mgumussoy.advancedtaskmanagement.entities.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(UserEntity.class, UserDTO.class).addMappings(mapper -> {
            mapper.map(UserEntity::getPassword, UserDTO::setPassword);
            mapper.map(src -> src.getDepartment().getId(), UserDTO::setDepartmentId);
            mapper.map(src -> {
                if (src.getAuthorities() != null) {
                    return src.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                } else {
                    return new ArrayList<>();
                }
            }, UserDTO::setAuthorities);
        });
        modelMapper.typeMap(TaskEntity.class, TaskDTO.class).addMappings(mapper -> {
            mapper.map(TaskEntity::getId, TaskDTO::setId);
            mapper.map(TaskEntity::getUserStory, TaskDTO::setUserStory);
            mapper.map(TaskEntity::getAcceptanceCriteria, TaskDTO::setAcceptanceCriteria);
            mapper.map(TaskEntity::getState, TaskDTO::setState);
            mapper.map(TaskEntity::getPriority, TaskDTO::setPriority);
            mapper.map(src -> src.getAssignee().getId(), TaskDTO::setAssigneeId);
            mapper.map(src -> src.getProject().getId(), TaskDTO::setProjectId);
        });
        return modelMapper;
    }
}

