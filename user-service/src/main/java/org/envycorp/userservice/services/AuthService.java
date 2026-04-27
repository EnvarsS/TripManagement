package org.envycorp.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.IncorrectEmailException;
import org.envycorp.userservice.exceptions.IncorrectPasswordException;
import org.envycorp.userservice.models.dto.request.UserCreateRequestDto;
import org.envycorp.userservice.models.dto.request.UserLoginRequestDto;
import org.envycorp.userservice.models.entity.Role;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.repositories.RoleRepository;
import org.envycorp.userservice.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    @Transactional
    public ResponseEntity<String> login(UserLoginRequestDto userLogin) {
        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: account not found");
                    return new IncorrectEmailException("Invalid email or password");
                });

        if (!bCryptPasswordEncoder.matches(userLogin.getPassword(), user.getHashedPassword())) {
            log.warn("Login failed: invalid credentials for userId={}", user.getId());
            throw new IncorrectPasswordException("Invalid password");
        }

        log.info("User authenticated successfully: userId={}", user.getId());
        return ResponseEntity.ok(jwtService.generateToken(user));
    }

    @Transactional
    public ResponseEntity<String> createUser(UserCreateRequestDto userCreate) {
        if (userRepository.existsByEmail(userCreate.getEmail())) {
            log.warn("Registration failed: email already taken");
            throw new EmailIsAlreadyTakenException("Email Already Exists");
        }

        User user = modelMapper.map(userCreate, User.class);
        user.setHashedPassword(bCryptPasswordEncoder.encode(userCreate.getPassword()));
        Role role = roleRepository.findByName("USER");
        user.setRole(role);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully: userId={}", savedUser.getId());
        return ResponseEntity.ok(jwtService.generateToken(savedUser));
    }
}
