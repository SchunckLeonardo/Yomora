CREATE TABLE posts (
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    text VARCHAR(5000) NOT NULL,
    edition_id UUID REFERENCES book_editions(id) ON DELETE SET NULL,
    type VARCHAR(20) NOT NULL,
    spoiler BOOLEAN NOT NULL DEFAULT FALSE,
    spoiler_page INTEGER,
    visibility VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT posts_type_check CHECK (type IN ('NOTE', 'REVIEW', 'RECOMMENDATION', 'PROGRESS', 'QUOTE')),
    CONSTRAINT posts_visibility_check CHECK (visibility IN ('PUBLIC', 'FOLLOWERS', 'PRIVATE')),
    CONSTRAINT posts_spoiler_page_positive CHECK (spoiler_page IS NULL OR spoiler_page >= 0)
);

CREATE INDEX posts_author_created_idx ON posts (author_id, created_at DESC);
CREATE INDEX posts_visibility_created_idx ON posts (visibility, created_at DESC);

CREATE TABLE post_likes (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT post_likes_unique UNIQUE (post_id, user_id)
);

CREATE INDEX post_likes_post_idx ON post_likes (post_id);

CREATE TABLE comments (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    text VARCHAR(2000) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX comments_post_created_idx ON comments (post_id, created_at);

CREATE TABLE follows (
    id UUID PRIMARY KEY,
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    followed_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACCEPTED',
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT follows_unique UNIQUE (follower_id, followed_id),
    CONSTRAINT follows_not_self CHECK (follower_id <> followed_id),
    CONSTRAINT follows_status_check CHECK (status IN ('PENDING', 'ACCEPTED'))
);

CREATE INDEX follows_follower_idx ON follows (follower_id, status);
CREATE INDEX follows_followed_idx ON follows (followed_id, status);

CREATE TABLE notes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    edition_id UUID NOT NULL REFERENCES book_editions(id) ON DELETE CASCADE,
    content VARCHAR(5000) NOT NULL,
    page INTEGER,
    chapter VARCHAR(200),
    private_note BOOLEAN NOT NULL DEFAULT TRUE,
    spoiler BOOLEAN NOT NULL DEFAULT FALSE,
    post_id UUID REFERENCES posts(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT notes_page_nonnegative CHECK (page IS NULL OR page >= 0)
);

CREATE INDEX notes_user_edition_idx ON notes (user_id, edition_id, created_at DESC);

CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    work_id UUID NOT NULL REFERENCES book_works(id) ON DELETE CASCADE,
    edition_id UUID REFERENCES book_editions(id) ON DELETE SET NULL,
    rating INTEGER NOT NULL,
    title VARCHAR(200),
    text VARCHAR(5000) NOT NULL,
    spoiler BOOLEAN NOT NULL DEFAULT FALSE,
    post_id UUID REFERENCES posts(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT reviews_user_work_unique UNIQUE (user_id, work_id),
    CONSTRAINT reviews_rating_check CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX reviews_work_created_idx ON reviews (work_id, created_at DESC);

CREATE TABLE blocked_users (
    id UUID PRIMARY KEY,
    blocker_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT blocked_users_unique UNIQUE (blocker_id, blocked_id),
    CONSTRAINT blocked_users_not_self CHECK (blocker_id <> blocked_id)
);

CREATE TABLE reports (
    id UUID PRIMARY KEY,
    reporter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reported_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    post_id UUID REFERENCES posts(id) ON DELETE SET NULL,
    reason VARCHAR(100) NOT NULL,
    details VARCHAR(2000),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT reports_target_check CHECK (reported_user_id IS NOT NULL OR post_id IS NOT NULL),
    CONSTRAINT reports_status_check CHECK (status IN ('OPEN', 'REVIEWED', 'DISMISSED', 'ACTIONED'))
);

CREATE INDEX reports_status_created_idx ON reports (status, created_at);
