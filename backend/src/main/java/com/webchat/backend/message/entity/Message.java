package com.webchat.backend.message.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.webchat.backend.conversation.entity.Conversation;
import com.webchat.backend.user.entity.AppUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "messages",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_messages_conversation_sequence",
                        columnNames = { "conversation_id", "sequence" }),
                @UniqueConstraint(name = "ux_messages_sender_client_id",
                        columnNames = { "sender_id", "client_message_id" })
        },
        indexes = {
                @Index(name = "ix_messages_sender_created",
                        columnList = "sender_id, created_at"),
                @Index(name = "ix_messages_reply_to", columnList = "reply_to_message_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(nullable = false)
    private long sequence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private AppUser sender;

    @Column(columnDefinition = "text")
    private String body;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_message_id")
    private Message replyTo;

    @Column(name = "client_message_id", nullable = false)
    private UUID clientMessageId;

    @Version
    @Column(nullable = false)
    private int version = 1;
}
