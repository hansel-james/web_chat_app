package com.webchat.backend.conversation.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.webchat.backend.user.entity.AppUser;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conversation_members", indexes = {
        @Index(name = "ix_cm_user_conversation", columnList = "user_id, conversation_id")
})
@Getter
@Setter
@NoArgsConstructor
public class ConversationMember {

    @EmbeddedId
    private ConversationMemberId id;

    @MapsId("conversationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ConversationMemberRole role;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    @Column(name = "last_read_sequence")
    private Long lastReadSequence;

    @Column(name = "last_read_message_id")
    private UUID lastReadMessageId;

    @Column(name = "last_read_at")
    private Instant lastReadAt;

    @Column(name = "read_receipts_enabled", nullable = false)
    private boolean readReceiptsEnabled = true;
}
