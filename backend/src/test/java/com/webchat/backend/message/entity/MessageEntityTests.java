package com.webchat.backend.message.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.webchat.backend.JpaEntityTestSupport;
import com.webchat.backend.conversation.entity.Conversation;
import com.webchat.backend.conversation.entity.ConversationType;
import com.webchat.backend.user.entity.AppUser;

class MessageEntityTests extends JpaEntityTestSupport {

	@Test
	@Transactional
	void persistsMessagesRepliesAttachmentsAndReceipts() {
		AppUser sender = persistUser("sender");
		AppUser recipient = persistUser("recipient");
		Conversation conversation = persistConversation(sender, ConversationType.DIRECT);

		Message message = createMessage(conversation, sender, 1, "Hello");
		entityManager.persist(message);

		Message reply = createMessage(conversation, recipient, 2, "Reply");
		reply.setReplyTo(message);
		entityManager.persist(reply);

		MessageAttachment attachment = new MessageAttachment();
		attachment.setMessage(message);
		attachment.setObjectKey("private/chat/image.png");
		attachment.setMediaType("image/png");
		attachment.setByteSize(128);
		attachment.setChecksum("checksum");
		entityManager.persist(attachment);

		Instant deliveredAt = Instant.now();
		MessageReceipt receipt = new MessageReceipt();
		MessageReceiptId receiptId = new MessageReceiptId();
		receiptId.setMessageId(message.getId());
		receiptId.setUserId(recipient.getId());
		receipt.setId(receiptId);
		receipt.setMessage(message);
		receipt.setUser(recipient);
		receipt.setDeliveredAt(deliveredAt);
		entityManager.persist(receipt);
		entityManager.flush();

		UUID messageId = message.getId();
		UUID replyId = reply.getId();
		entityManager.clear();

		Message persistedMessage = entityManager.find(Message.class, messageId);
		Message persistedReply = entityManager.find(Message.class, replyId);
		assertEquals("Hello", persistedMessage.getBody());
		assertNotNull(persistedMessage.getCreatedAt());
		assertEquals(1, persistedMessage.getVersion());
		assertEquals(messageId, persistedReply.getReplyTo().getId());
		assertEquals(2L, countRows("Message"));
		assertEquals(1L, countRows("MessageAttachment"));
		assertEquals(1L, countRows("MessageReceipt"));

		MessageReceipt persistedReceipt = entityManager.find(MessageReceipt.class, receiptId);
		assertEquals(deliveredAt.truncatedTo(ChronoUnit.MICROS), persistedReceipt.getDeliveredAt());
	}

	private Message createMessage(
			Conversation conversation, AppUser sender, long sequence, String body) {
		Message message = new Message();
		message.setConversation(conversation);
		message.setSender(sender);
		message.setSequence(sequence);
		message.setBody(body);
		message.setClientMessageId(UUID.randomUUID());
		return message;
	}
}
