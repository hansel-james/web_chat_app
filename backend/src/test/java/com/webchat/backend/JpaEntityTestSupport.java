package com.webchat.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.webchat.backend.conversation.entity.Conversation;
import com.webchat.backend.conversation.entity.ConversationType;
import com.webchat.backend.user.entity.AppUser;

import jakarta.persistence.EntityManager;

@SpringBootTest(properties = {
		"spring.datasource.url=${TEST_DATABASE_URL:jdbc:h2:mem:chat-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1}",
		"spring.datasource.username=${TEST_DATABASE_USERNAME:sa}",
		"spring.datasource.password=${TEST_DATABASE_PASSWORD:}",
		"spring.datasource.driver-class-name=${TEST_DATABASE_DRIVER:org.h2.Driver}",
		"spring.jpa.database-platform=${TEST_DATABASE_DIALECT:org.hibernate.dialect.H2Dialect}",
		"spring.jpa.hibernate.ddl-auto=${TEST_HIBERNATE_DDL_AUTO:create-drop}",
		"spring.flyway.enabled=${TEST_FLYWAY_ENABLED:false}",
		"aws.s3.endpoint-url=http://localhost",
		"aws.s3.access-key-id=test",
		"aws.s3.secret-access-key=test",
		"aws.s3.region=test"
})
public abstract class JpaEntityTestSupport {

	@Autowired
	protected EntityManager entityManager;

	protected AppUser persistUser(String username) {
		AppUser user = new AppUser();
		user.setUsername(username);
		user.setPasswordHash("test-password-hash");
		entityManager.persist(user);
		entityManager.flush();
		return user;
	}

	protected Conversation persistConversation(AppUser creator, ConversationType type) {
		if (type == ConversationType.DIRECT) {
			throw new IllegalArgumentException("Use the direct conversation helper for DIRECT chats");
		}
		Conversation conversation = new Conversation();
		conversation.setType(type);
		conversation.setCreatedBy(creator);
		entityManager.persist(conversation);
		entityManager.flush();
		return conversation;
	}

	protected Conversation persistDirectConversation(AppUser firstUser, AppUser secondUser) {
		long lowerUserId = Math.min(firstUser.getId(), secondUser.getId());
		long higherUserId = Math.max(firstUser.getId(), secondUser.getId());

		Conversation conversation = new Conversation();
		conversation.setType(ConversationType.DIRECT);
		conversation.setDirectPairKey(lowerUserId + ":" + higherUserId);
		conversation.setCreatedBy(firstUser);
		entityManager.persist(conversation);
		entityManager.flush();
		return conversation;
	}

	protected long countRows(String entityName) {
		return entityManager.createQuery(
				"select count(entity) from " + entityName + " entity", Long.class)
				.getSingleResult();
	}
}
