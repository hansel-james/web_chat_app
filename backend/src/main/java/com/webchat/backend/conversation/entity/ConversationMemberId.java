package com.webchat.backend.conversation.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ConversationMemberId implements Serializable {

    @Column(name = "conversation_id")
    private UUID conversationId;

    @Column(name = "user_id")
    private Long userId;
}
