package org.envycorp.userservice.services;

import lombok.RequiredArgsConstructor;
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
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    @Transactional
    public ResponseEntity<String> login(UserLoginRequestDto userLogin) {
        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(() -> new IncorrectEmailException("Invalid email or password"));

        if (!bCryptPasswordEncoder.matches(userLogin.getPassword(), user.getHashedPassword())) {
            throw new IncorrectPasswordException("Invalid password");
        }

        return ResponseEntity.ok(jwtService.generateToken(user));
    }

    @Transactional
    public ResponseEntity<String> createUser(UserCreateRequestDto userCreate) {
        if(userRepository.existsByEmail(userCreate.getEmail())) {
            throw new EmailIsAlreadyTakenException("Email Already Exists");
        }

        User user = modelMapper.map(userCreate, User.class);
        user.setHashedPassword(bCryptPasswordEncoder.encode(userCreate.getPassword()));
        Role role = roleRepository.findByName("USER");
        user.setRole(role);
        User savedUser = userRepository.save(user);

        String jwt = jwtService.generateToken(savedUser);

        return ResponseEntity.ok(jwt);
    }
}
