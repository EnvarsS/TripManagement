package org.envycorp.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.UserNotFoundException;
import org.envycorp.userservice.models.dto.request.UserUpdateRequestDto;
import org.envycorp.userservice.models.dto.response.UserResponseDto;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.repositories.UserRepository;
import org.modelmapper.ModelMapper;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    public ResponseEntity<UserResponseDto> getUser() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Data integrity issue: authenticated userId={} not found in database", userId);
                    return new UserNotFoundException("User not found");
                });

        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }

    @Transactional
    public ResponseEntity<UserResponseDto> updateUser(UserUpdateRequestDto dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Data integrity issue: authenticated userId={} not found in database", userId);
                    return new UserNotFoundException("User not found");
                });

        if (!dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                log.warn("Profile update failed: email already taken, userId={}", userId);
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
    public void deleteUser() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Data integrity issue: authenticated userId={} not found in database", userId);
                    return new UserNotFoundException("User not found");
                });

        userRepository.delete(user);
        log.info("User account deleted: userId={}", userId);
    }
}
