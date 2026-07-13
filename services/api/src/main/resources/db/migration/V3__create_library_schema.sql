CREATE TABLE user_books (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    edition_id UUID NOT NULL REFERENCES book_editions(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    current_page INTEGER NOT NULL DEFAULT 0,
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    rating INTEGER,
    target_finish_date DATE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT user_books_unique_edition UNIQUE (user_id, edition_id),
    CONSTRAINT user_books_status_check CHECK (status IN ('WANT_TO_READ', 'READING', 'PAUSED', 'FINISHED', 'ABANDONED')),
    CONSTRAINT user_books_current_page_nonnegative CHECK (current_page >= 0),
    CONSTRAINT user_books_rating_check CHECK (rating IS NULL OR rating BETWEEN 1 AND 5)
);

CREATE INDEX user_books_user_status_idx ON user_books (user_id, status);
CREATE INDEX user_books_updated_idx ON user_books (user_id, updated_at DESC);

CREATE TABLE shelves (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(80) NOT NULL,
    public_shelf BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT shelves_user_name_unique UNIQUE (user_id, name)
);

CREATE INDEX shelves_user_idx ON shelves (user_id);

CREATE TABLE shelf_books (
    shelf_id UUID NOT NULL REFERENCES shelves(id) ON DELETE CASCADE,
    user_book_id UUID NOT NULL REFERENCES user_books(id) ON DELETE CASCADE,
    PRIMARY KEY (shelf_id, user_book_id)
);
