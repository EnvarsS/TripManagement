package org.envycorp.userservice.services;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
