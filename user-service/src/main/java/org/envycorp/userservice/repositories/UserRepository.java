package org.envycorp.userservice.repositories;

import org.envycorp.userservice.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface UserRepository extends JpaRepository<User,Integer> {
}
