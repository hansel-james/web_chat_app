package com.webchat.backend.conversation.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.webchat.backend.JpaEntityTestSupport;
import com.webchat.backend.user.entity.AppUser;

class ConversationEntityTests extends JpaEntityTestSupport {

	@Test
	@Transactional
	void persistsConversationAndCompositeMemberships() {
		AppUser owner = persistUser("owner");
		AppUser memberUser = persistUser("member");
		Conversation conversation = persistConversation(owner, ConversationType.GROUP);
		Instant readAt = Instant.now();

		ConversationMember ownerMember = createMember(
				conversation, owner, ConversationMemberRole.OWNER);
		ConversationMember member = createMember(
				conversation, memberUser, ConversationMemberRole.MEMBER);
		member.setLastReadSequence(12L);
		member.setLastReadAt(readAt);

		entityManager.persist(ownerMember);
		entityManager.persist(member);
		entityManager.flush();

		assertNotNull(conversation.getCreatedAt());
		assertNotNull(conversation.getUpdatedAt());
		assertNotNull(ownerMember.getJoinedAt());
		assertEquals(2L, countRows("ConversationMember"));

		ConversationMemberId ownerId = ownerMember.getId();
		entityManager.clear();
		ConversationMember persistedMember = entityManager.find(
				ConversationMember.class, member.getId());
		assertEquals(ConversationMemberRole.MEMBER, persistedMember.getRole());
		assertEquals(12L, persistedMember.getLastReadSequence());
		assertEquals(readAt, persistedMember.getLastReadAt());
		assertEquals(conversation.getId(), ownerId.getConversationId());
	}

	private ConversationMember createMember(
			Conversation conversation, AppUser user, ConversationMemberRole role) {
		ConversationMemberId id = new ConversationMemberId();
		id.setConversationId(conversation.getId());
		id.setUserId(user.getId());

		ConversationMember member = new ConversationMember();
		member.setId(id);
		member.setConversation(conversation);
		member.setUser(user);
		member.setRole(role);
		return member;
	}
}
