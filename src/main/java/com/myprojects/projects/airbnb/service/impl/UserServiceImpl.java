package com.myprojects.projects.airbnb.service.impl;

import com.myprojects.projects.airbnb.dto.ProfileUpdateRequestDto;
import com.myprojects.projects.airbnb.dto.UserDto;
import com.myprojects.projects.airbnb.entity.User;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.repository.UserRepository;
import com.myprojects.projects.airbnb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.myprojects.projects.airbnb.util.AppUtils.getCurrentUser;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private  final ModelMapper modelMapper;
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        User user = getCurrentUser();
        Optional.ofNullable(profileUpdateRequestDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(profileUpdateRequestDto.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(profileUpdateRequestDto.getDateOfBirth()).ifPresent(user::setDateOfBirth);

        userRepository.save(user);
    }

    @Override
    public UserDto getMyProfile() {
        User currentUser = getCurrentUser();
        log.info("Fetching current user profile with ID: {}", currentUser.getId());
        return modelMapper.map(currentUser, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}
