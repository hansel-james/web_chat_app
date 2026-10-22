package com.webchat.backend.message.entity;

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
public class MessageReceiptId implements Serializable {

    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "user_id")
    private Long userId;
}
