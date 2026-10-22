# Phase 0 decisions

## Confirmed

- Deploy as one Spring Boot modular monolith; PostgreSQL is the durable source of truth.
- User identifiers remain `BIGINT`; conversation and message identifiers are UUIDs.
- Authentication will use short-lived JWT access tokens and rotating opaque refresh tokens. Store only refresh-token hashes, deliver refresh tokens in an HttpOnly, Secure cookie, and revoke them on logout.
- Direct conversations are unique per user pair; store the canonical pair key.
- Message edits are allowed for 15 minutes; deletions leave a tombstone; message retention is indefinite for v1.
- Read-receipt pause is reciprocal for that user's conversation view: while paused, they neither send nor see read receipts. The private unread cursor still advances; no paused-period receipts are backfilled on resume.
- The movable unread divider is a local navigation aid and does not itself emit or retract server receipts.
- When read receipts are enabled, a message becomes read after it is visible in the foreground for a short dwell time; the exact dwell duration is a Phase 1 product setting.
- Group owners and admins can add/remove ordinary members; only the owner can appoint/remove admins or transfer ownership; members can leave. Membership and role changes should be auditable.
- Notifications are in-app for v1; push/email are deferred.
- TEST and PROD use separate databases and credentials.
- Flyway owns schema changes; Hibernate validates mappings and does not alter schemas.

## Deliberately deferred

- JWT/access-token lifetimes and signing-key rotation parameters.
- Exact message body, attachment and notification retention/deletion mechanics beyond the v1 indefinite message-retention decision.
- Media size/type limits and external object-storage provider.
- Production hosting/provider selection.
