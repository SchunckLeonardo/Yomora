ALTER TABLE users
    ADD COLUMN email_verified_at TIMESTAMPTZ,
    ADD COLUMN profile_completed_at TIMESTAMPTZ;

UPDATE users
SET profile_completed_at = created_at
WHERE profile_completed_at IS NULL;

ALTER TABLE users
    ALTER COLUMN password_hash DROP NOT NULL;

CREATE TABLE email_verification_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX email_verification_tokens_user_idx ON email_verification_tokens (user_id);
CREATE INDEX email_verification_tokens_active_idx
    ON email_verification_tokens (token_hash)
    WHERE used_at IS NULL;

CREATE TABLE external_identities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(30) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    email VARCHAR(254) NOT NULL,
    encrypted_refresh_token TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT external_identities_provider_subject_unique UNIQUE (provider, subject),
    CONSTRAINT external_identities_user_provider_unique UNIQUE (user_id, provider)
);

CREATE INDEX external_identities_user_idx ON external_identities (user_id);
