package com.webchat.backend.event.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.webchat.backend.JpaEntityTestSupport;

class OutboxEventEntityTests extends JpaEntityTestSupport {

	@Test
	@Transactional
	void persistsJsonPayloadAndPublicationState() {
		UUID aggregateId = UUID.randomUUID();
		OutboxEvent event = new OutboxEvent();
		event.setAggregateType("Message");
		event.setAggregateId(aggregateId);
		event.setEventType("MessageCreated");
		event.setPayload(Map.of("messageId", aggregateId.toString()));
		event.setAttempts(2);

		entityManager.persist(event);
		entityManager.flush();
		UUID eventId = event.getId();
		entityManager.clear();

		OutboxEvent persistedEvent = entityManager.find(OutboxEvent.class, eventId);
		assertEquals("MessageCreated", persistedEvent.getEventType());
		assertEquals(aggregateId.toString(), persistedEvent.getPayload().get("messageId"));
		assertEquals(2, persistedEvent.getAttempts());
		assertNotNull(persistedEvent.getCreatedAt());
	}
}
