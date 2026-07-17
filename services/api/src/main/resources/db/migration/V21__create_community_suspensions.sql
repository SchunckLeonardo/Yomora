CREATE TABLE community_suspensions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reason VARCHAR(500) NOT NULL,
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id),
    reversed_at TIMESTAMPTZ,
    reversed_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT community_suspensions_period_check CHECK (ends_at > starts_at),
    CONSTRAINT community_suspensions_reversal_check CHECK (
        (reversed_at IS NULL AND reversed_by IS NULL)
        OR (reversed_at IS NOT NULL AND reversed_by IS NOT NULL)
    )
);

CREATE INDEX community_suspensions_user_active_idx
    ON community_suspensions (user_id, ends_at DESC)
    WHERE reversed_at IS NULL;
