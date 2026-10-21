package com.webchat.backend.message.entity;

import java.time.Instant;

import com.webchat.backend.user.entity.AppUser;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "message_receipts", indexes = {
        @Index(name = "ix_receipts_user_message", columnList = "user_id, message_id")
})
@Getter
@Setter
@NoArgsConstructor
public class MessageReceipt {

    @EmbeddedId
    private MessageReceiptId id;

    @MapsId("messageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @jakarta.persistence.Column(name = "delivered_at")
    private Instant deliveredAt;

    @jakarta.persistence.Column(name = "read_at")
    private Instant readAt;
}
