# Yomora contributor guide

Yomora is a modular monolith plus a native iOS client. Keep backend code inside its domain module and iOS code inside a feature. Do not introduce infrastructure dependencies into backend `domain` packages.

For Spring changes, use test-driven development: add the smallest failing focused test, run it, implement the production change, rerun the focused test, then run `./gradlew test`. Flyway migrations are append-only after release.

Catalog search is federated across Google Books and Open Library. Keep stored language codes normalized to ISO 639-3 (for example, `por`) and convert them to each provider's required format at the infrastructure boundary.

The iOS client stores downloaded book covers in an app-owned memory and disk cache. Library status or reading-session changes must publish `libraryDidChange` so the Hoje, Ler, and Biblioteca views refresh their active-reading state. Notes entered during focus sessions are persisted by the reading-session finish endpoint and shown in the session summary and the book's library details.

Useful commands: `make setup`, `make backend`, `make test`, `make docker-up`, `make seed`, `make ios-test`. Configuration belongs in environment variables; never commit real secrets.
