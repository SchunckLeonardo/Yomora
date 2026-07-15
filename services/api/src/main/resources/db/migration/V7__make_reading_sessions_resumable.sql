ALTER TABLE reading_sessions
    ADD COLUMN current_page INTEGER,
    ADD COLUMN paused_at TIMESTAMPTZ,
    ADD COLUMN paused_seconds BIGINT NOT NULL DEFAULT 0;

UPDATE reading_sessions
SET current_page = COALESCE(end_page, start_page);

ALTER TABLE reading_sessions
    ALTER COLUMN current_page SET NOT NULL;

ALTER TABLE reading_sessions
    ADD CONSTRAINT reading_sessions_current_page_valid CHECK (current_page >= start_page),
    ADD CONSTRAINT reading_sessions_paused_seconds_nonnegative CHECK (paused_seconds >= 0);

WITH ordered_active_sessions AS (
    SELECT id,
           LEAD(started_at) OVER (PARTITION BY user_id ORDER BY started_at, id) AS next_started_at
    FROM reading_sessions
    WHERE finished_at IS NULL
)
UPDATE reading_sessions session
SET finished_at = ordered.next_started_at,
    duration_seconds = GREATEST(
        1,
        EXTRACT(EPOCH FROM (ordered.next_started_at - session.started_at))::BIGINT
    ),
    end_page = session.current_page,
    pages_read = session.current_page - session.start_page,
    paused_at = NULL
FROM ordered_active_sessions ordered
WHERE session.id = ordered.id
  AND ordered.next_started_at IS NOT NULL;

DROP INDEX reading_sessions_one_active_per_book_idx;

CREATE UNIQUE INDEX reading_sessions_one_active_per_user_idx
    ON reading_sessions (user_id)
    WHERE finished_at IS NULL;
