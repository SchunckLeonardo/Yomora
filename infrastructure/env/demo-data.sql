BEGIN;

INSERT INTO users (id, name, username, email, password_hash, bio, public_profile, created_at, updated_at)
VALUES
  ('10000000-0000-0000-0000-000000000001', 'Marina Leitora', 'marina', 'marina@yomora.local', '$2a$10$3RKDaimwN7o.Gzafio9MgOEd6d4mWQORg/DqiCcIqkYXW05ipN3Uu', 'Lendo um pouco todos os dias.', TRUE, now(), now()),
  ('10000000-0000-0000-0000-000000000002', 'Caio Livros', 'caio', 'caio@yomora.local', '$2a$10$3RKDaimwN7o.Gzafio9MgOEd6d4mWQORg/DqiCcIqkYXW05ipN3Uu', 'Ficção, história e café.', TRUE, now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO book_works (id, title, description, created_at, updated_at)
VALUES ('20000000-0000-0000-0000-000000000001', 'A Biblioteca da Meia-Noite', 'Uma história sobre escolhas, arrependimentos e possibilidades.', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO book_authors (work_id, author_name)
VALUES ('20000000-0000-0000-0000-000000000001', 'Matt Haig')
ON CONFLICT DO NOTHING;

INSERT INTO book_categories (work_id, category)
VALUES ('20000000-0000-0000-0000-000000000001', 'Ficção')
ON CONFLICT DO NOTHING;

INSERT INTO book_editions (id, work_id, isbn13, publisher, publication_date, language, page_count, external_provider, external_id, created_at, updated_at)
VALUES ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', '9786555602079', 'Bertrand Brasil', '2021-01-01', 'pt', 308, 'MANUAL', 'demo-midnight-library', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO user_books (id, user_id, edition_id, status, current_page, started_at, created_at, updated_at)
VALUES ('40000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'READING', 96, now() - interval '7 days', now() - interval '7 days', now())
ON CONFLICT DO NOTHING;

INSERT INTO reading_goals (user_id, daily_minutes, weekly_days, daily_pages, created_at, updated_at)
VALUES ('10000000-0000-0000-0000-000000000001', 20, 5, 10, now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO reading_sessions (id, user_id, user_book_id, started_at, finished_at, start_page, end_page, duration_seconds, pages_read, note)
VALUES ('60000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', now() - interval '1 day 25 minutes', now() - interval '1 day', 84, 96, 1500, 12, 'Leitura tranquila.')
ON CONFLICT DO NOTHING;

INSERT INTO follows (id, follower_id, followed_id, status, created_at)
VALUES ('65000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', 'ACCEPTED', now())
ON CONFLICT DO NOTHING;

INSERT INTO posts (id, author_id, edition_id, type, text, spoiler, visibility, created_at, updated_at)
VALUES ('70000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', 'RECOMMENDATION', 'Uma leitura acolhedora sobre recomeços. Recomendo!', FALSE, 'PUBLIC', now() - interval '2 hours', now() - interval '2 hours')
ON CONFLICT DO NOTHING;

COMMIT;
