CREATE TABLE community_activities (
    id UUID PRIMARY KEY,
    recipient_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    actor_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(30) NOT NULL,
    post_id UUID REFERENCES posts(id) ON DELETE CASCADE,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT community_activities_type_check CHECK (
        type IN ('FOLLOW_REQUEST', 'FOLLOW_ACCEPTED', 'USER_FOLLOWED', 'POST_LIKED', 'POST_COMMENTED')
    )
);

CREATE INDEX community_activities_recipient_created_idx
    ON community_activities (recipient_id, created_at DESC);
