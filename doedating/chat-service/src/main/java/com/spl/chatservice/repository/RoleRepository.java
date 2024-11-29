package com.spl.chatservice.repository;

import com.spl.chatservice.entity.RoleEntity;
import com.spl.chatservice.model.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
  Optional<RoleEntity> findByName(ERole name);
}
