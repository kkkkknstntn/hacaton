package com.spl.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
  private UUID id;

  private String content;
  private MessageType messageType;

  private UUID senderId;
  private String senderUsername;

  private UUID receiverId;
  private String receiverUsername;

  private UserConnection userConnection;

  private MessageDeliveryStatusEnum messageDeliveryStatusEnum;

  private List<MessageDeliveryStatusUpdate> messageDeliveryStatusUpdates;
}
