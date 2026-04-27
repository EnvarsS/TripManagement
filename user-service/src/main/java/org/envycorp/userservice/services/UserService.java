package org.envycorp.userservice.services;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.IncorrectEmailException;
import org.envycorp.userservice.exceptions.IncorrectPasswordException;
import org.envycorp.userservice.exceptions.UserNotFoundException;
import org.envycorp.userservice.models.dto.request.UserCreateRequestDto;
import org.envycorp.userservice.models.dto.request.UserLoginRequestDto;
import org.envycorp.userservice.models.dto.request.UserUpdateRequestDto;
import org.envycorp.userservice.models.dto.response.UserResponseDto;
import org.envycorp.userservice.models.entity.Role;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.repositories.RoleRepository;
import org.envycorp.userservice.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    public UserResponseDto getUser() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return modelMapper.map(user, UserResponseDto.class);
    }

    @Transactional
    public UserResponseDto updateUser(UserUpdateRequestDto dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new EmailIsAlreadyTakenException("Email is already taken");
            }
            user.setEmail(dto.getEmail());
        }

        user.setName(dto.getName());

        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getHashedPassword())) {
            user.setHashedPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }

        return modelMapper.map(userRepository.save(user), UserResponseDto.class);
    }

    @Transactional
    public void deleteUser() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }
}
