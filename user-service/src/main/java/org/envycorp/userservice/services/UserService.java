package org.envycorp.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserUpdateRequestDto;
import org.envycorp.commonmodule.dto.response.users.UserResponseDto;
import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.UserNotFoundException;
import org.envycorp.userservice.models.entity.Role;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.repositories.RoleRepository;
import org.envycorp.userservice.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Data integrity issue: authenticated userId={} not found in database", userId);
                    return new UserNotFoundException("User not found");
                });

        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }

    public ResponseEntity<UserResponseDto> getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Data integrity issue: authenticated userId={} not found in database", userId);
                    return new UserNotFoundException("User not found");
                });

        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }

    @Transactional
    public ResponseEntity<UserResponseDto> createUser(UserCreateRequestDto userCreateRequestDto) {
        if (userRepository.existsByEmail(userCreateRequestDto.getEmail())) {
            log.warn("Registration failed: email already taken");
            throw new EmailIsAlreadyTakenException("Email Already Exists");
        }

        User user = modelMapper.map(userCreateRequestDto, User.class);
        Role role = roleRepository.findByName("USER");
        user.setRole(role);
        user.setHashedPassword(bCryptPasswordEncoder.encode(userCreateRequestDto.getPassword()));
        User savedUser = userRepository.save(user);

        log.info("User registered successfully: userId={}", savedUser.getId());
        return new ResponseEntity<>(modelMapper.map(savedUser, UserResponseDto.class), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<UserResponseDto> updateUser(Long providedUserId, UserUpdateRequestDto dto) {

        User user = userRepository.findById(providedUserId)
                .orElseThrow(() -> {
                    log.error("Data integrity issue: authenticated userId={} not found in database", providedUserId);
                    return new UserNotFoundException("User not found");
                });

        if (!dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                log.warn("Profile update failed: email already taken, userId={}", providedUserId);
                throw new EmailIsAlreadyTakenException("Email is already taken");
            }
            user.setEmail(dto.getEmail());
        }

        user.setName(dto.getName());

        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getHashedPassword())) {
            user.setHashedPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }

        User saved = userRepository.save(user);
        log.info("User profile updated: userId={}", saved.getId());
        return ResponseEntity.ok(modelMapper.map(saved, UserResponseDto.class));
    }

    @Transactional
    public void deleteUser(Long providedUserId) {

        User user = userRepository.findById(providedUserId)
                .orElseThrow(() -> {
                    log.error("Data integrity issue: authenticated userId={} not found in database", providedUserId);
                    return new UserNotFoundException("User not found");
                });

        userRepository.delete(user);
        log.info("User account deleted: userId={}", providedUserId);
    }
}
