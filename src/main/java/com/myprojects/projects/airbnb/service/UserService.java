package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.dto.ProfileUpdateRequestDto;
import com.myprojects.projects.airbnb.dto.UserDto;
import com.myprojects.projects.airbnb.entity.User;

public interface UserService {


    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
