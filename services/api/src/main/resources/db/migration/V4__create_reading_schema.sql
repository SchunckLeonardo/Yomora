CREATE TABLE reading_goals (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    daily_minutes INTEGER NOT NULL,
    weekly_days INTEGER NOT NULL,
    daily_pages INTEGER,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT reading_goals_minutes_positive CHECK (daily_minutes > 0),
    CONSTRAINT reading_goals_weekly_days_check CHECK (weekly_days BETWEEN 1 AND 7),
    CONSTRAINT reading_goals_pages_positive CHECK (daily_pages IS NULL OR daily_pages > 0)
);

CREATE TABLE reading_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_book_id UUID NOT NULL REFERENCES user_books(id) ON DELETE CASCADE,
    start_page INTEGER NOT NULL,
    end_page INTEGER,
    goal_pages INTEGER,
    started_at TIMESTAMPTZ NOT NULL,
    finished_at TIMESTAMPTZ,
    duration_seconds BIGINT,
    pages_read INTEGER,
    note VARCHAR(2000),
    CONSTRAINT reading_sessions_start_page_nonnegative CHECK (start_page >= 0),
    CONSTRAINT reading_sessions_end_page_valid CHECK (end_page IS NULL OR end_page >= start_page),
    CONSTRAINT reading_sessions_duration_positive CHECK (duration_seconds IS NULL OR duration_seconds > 0),
    CONSTRAINT reading_sessions_pages_nonnegative CHECK (pages_read IS NULL OR pages_read >= 0)
);

CREATE INDEX reading_sessions_user_started_idx ON reading_sessions (user_id, started_at DESC);
CREATE INDEX reading_sessions_user_finished_idx ON reading_sessions (user_id, finished_at DESC) WHERE finished_at IS NOT NULL;
CREATE UNIQUE INDEX reading_sessions_one_active_per_book_idx
    ON reading_sessions (user_book_id)
    WHERE finished_at IS NULL;
