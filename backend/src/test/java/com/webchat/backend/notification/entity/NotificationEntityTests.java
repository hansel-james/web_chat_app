package com.webchat.backend.notification.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.webchat.backend.JpaEntityTestSupport;
import com.webchat.backend.conversation.entity.Conversation;
import com.webchat.backend.conversation.entity.ConversationType;
import com.webchat.backend.message.entity.Message;
import com.webchat.backend.user.entity.AppUser;

class NotificationEntityTests extends JpaEntityTestSupport {

	@Test
	@Transactional
	void persistsUnreadNotificationWithConversationAndMessageReferences() {
		AppUser sender = persistUser("sender");
		AppUser recipient = persistUser("recipient");
		Conversation conversation = persistConversation(sender, ConversationType.DIRECT);

		Message message = new Message();
		message.setConversation(conversation);
		message.setSender(sender);
		message.setSequence(1);
		message.setBody("Hello");
		message.setClientMessageId(UUID.randomUUID());
		entityManager.persist(message);

		Notification notification = new Notification();
		notification.setUser(recipient);
		notification.setConversation(conversation);
		notification.setMessage(message);
		notification.setType("NEW_MESSAGE");
		entityManager.persist(notification);
		entityManager.flush();

		UUID notificationId = notification.getId();
		entityManager.clear();

		Notification persisted = entityManager.find(Notification.class, notificationId);
		assertEquals(recipient.getId(), persisted.getUser().getId());
		assertEquals(conversation.getId(), persisted.getConversation().getId());
		assertEquals(message.getId(), persisted.getMessage().getId());
		assertEquals("NEW_MESSAGE", persisted.getType());
		assertNotNull(persisted.getCreatedAt());
		assertNull(persisted.getReadAt());
	}
}
