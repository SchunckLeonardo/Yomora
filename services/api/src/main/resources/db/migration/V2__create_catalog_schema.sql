CREATE TABLE book_works (
    id UUID PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX book_works_title_idx ON book_works (LOWER(title));

CREATE TABLE book_authors (
    work_id UUID NOT NULL REFERENCES book_works(id) ON DELETE CASCADE,
    author_name VARCHAR(200) NOT NULL,
    PRIMARY KEY (work_id, author_name)
);

CREATE INDEX book_authors_name_idx ON book_authors (LOWER(author_name));

CREATE TABLE book_categories (
    work_id UUID NOT NULL REFERENCES book_works(id) ON DELETE CASCADE,
    category VARCHAR(120) NOT NULL,
    PRIMARY KEY (work_id, category)
);

CREATE TABLE book_editions (
    id UUID PRIMARY KEY,
    work_id UUID NOT NULL REFERENCES book_works(id) ON DELETE CASCADE,
    isbn10 VARCHAR(10),
    isbn13 VARCHAR(13),
    publisher VARCHAR(200),
    publication_date DATE,
    language VARCHAR(10) NOT NULL,
    page_count INTEGER,
    cover_url VARCHAR(1000),
    external_provider VARCHAR(40),
    external_id VARCHAR(200),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT book_editions_isbn13_unique UNIQUE (isbn13),
    CONSTRAINT book_editions_provider_reference_unique UNIQUE (external_provider, external_id),
    CONSTRAINT book_editions_page_count_positive CHECK (page_count IS NULL OR page_count > 0)
);

CREATE INDEX book_editions_work_idx ON book_editions (work_id);
CREATE INDEX book_editions_language_idx ON book_editions (language);
CREATE INDEX book_editions_isbn10_idx ON book_editions (isbn10);

CREATE TABLE external_book_references (
    id UUID PRIMARY KEY,
    edition_id UUID NOT NULL REFERENCES book_editions(id) ON DELETE CASCADE,
    provider VARCHAR(40) NOT NULL,
    external_id VARCHAR(200) NOT NULL,
    raw_reference JSONB,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT external_book_reference_unique UNIQUE (provider, external_id)
);
