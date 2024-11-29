package com.spl.chatservice.repository;

import com.spl.chatservice.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {

  @Query(
      "select c from ConversationEntity c where c.toUser = :toUser and c.deliveryStatus in ('NOT_DELIVERED', 'DELIVERED') and c.fromUser = :fromUser")
  List<ConversationEntity> findUnseenMessages(
      @Param("toUser") UUID toUser, @Param("fromUser") UUID fromUser);

  @Query(
      value =
          "select * from conversation where to_user = :toUser and delivery_status in ('NOT_DELIVERED', 'DELIVERED')",
      nativeQuery = true)
  List<ConversationEntity> findUnseenMessagesCount(@Param("toUser") UUID toUser);
}
