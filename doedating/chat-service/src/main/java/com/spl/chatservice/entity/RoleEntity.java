package com.spl.chatservice.entity;

import com.spl.chatservice.model.ERole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
  @Id
  @Column(name = "id", nullable = false, columnDefinition = "uuid")
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ERole name;
}
