# Yomora contributor guide

Yomora is a modular monolith plus a native iOS client. Keep backend code inside its domain module and iOS code inside a feature. Do not introduce infrastructure dependencies into backend `domain` packages.

For Spring changes, use test-driven development: add the smallest failing focused test, run it, implement the production change, rerun the focused test, then run `./gradlew test`. Flyway migrations are append-only after release.

Useful commands: `make setup`, `make backend`, `make test`, `make docker-up`, `make seed`, `make ios-test`. Configuration belongs in environment variables; never commit real secrets.

