package com.webchat.backend.auth.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.webchat.backend.JpaEntityTestSupport;
import com.webchat.backend.user.entity.AppUser;

class RefreshTokenEntityTests extends JpaEntityTestSupport {

	@Test
	@Transactional
	void persistsHashedRefreshTokenAndRevocationState() {
		AppUser user = persistUser("token-owner");
		Instant now = Instant.now();

		RefreshToken token = new RefreshToken();
		token.setUser(user);
		token.setTokenHash("sha256-hash");
		token.setCreatedAt(now);
		token.setExpiresAt(now.plusSeconds(3600));
		entityManager.persist(token);
		entityManager.flush();

		UUID tokenId = token.getId();
		token.setRevokedAt(now.plusSeconds(60));
		entityManager.flush();
		entityManager.clear();

		RefreshToken persisted = entityManager.find(RefreshToken.class, tokenId);
		assertEquals(user.getId(), persisted.getUser().getId());
		assertEquals("sha256-hash", persisted.getTokenHash());
		assertNotNull(persisted.getRevokedAt());
		assertTrue(Duration.between(now.plusSeconds(3600), persisted.getExpiresAt())
				.abs().toNanos() <= 1_000);
	}
}
