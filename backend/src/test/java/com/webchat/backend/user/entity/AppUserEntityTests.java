package com.webchat.backend.user.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.webchat.backend.JpaEntityTestSupport;

import jakarta.persistence.PersistenceException;

class AppUserEntityTests extends JpaEntityTestSupport {

	@Test
	@Transactional
	void trimsAndNormalizesUsernameOnPersist() {
		AppUser user = persistUser("  Alice_1  ");

		assertEquals("Alice_1", user.getUsername());
		assertEquals("alice_1", user.getUsernameNormalized());
		assertNotNull(user.getCreatedAt());
	}

	@Test
	@Transactional
	void rejectsDuplicateUsernamesIgnoringCaseAndWhitespace() {
		assertThrows(PersistenceException.class, () -> {
			persistUser("Alice");
			persistUser(" alice ");
		});
	}

	@Test
	@Transactional
	void rejectsUsernamesOutsideAllowedFormat() {
		AppUser user = new AppUser();
		user.setUsername("bad username");
		user.setPasswordHash("test-password-hash");

		assertThrows(IllegalArgumentException.class, () -> entityManager.persist(user));
	}
}
