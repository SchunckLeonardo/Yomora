# Yomora contributor guide

Yomora is a modular monolith plus a native iOS client. Keep backend code inside its domain module and iOS code inside a feature. Do not introduce infrastructure dependencies into backend `domain` packages.

For Spring changes, use test-driven development: add the smallest failing focused test, run it, implement the production change, rerun the focused test, then run `./gradlew test`. Flyway migrations are append-only after release.

Catalog search is federated across Google Books and Open Library. Keep stored language codes normalized to ISO 639-3 (for example, `por`) and convert them to each provider's required format at the infrastructure boundary.

The reading API is authoritative for an in-progress session: at most one active session exists per user, and pause, resume, elapsed time and current-page updates must remain resumable across devices. The iOS client restores that state on launch and foreground, while downloaded book covers stay in an app-owned memory and disk cache. Library status or reading-session changes must publish `libraryDidChange` so the Hoje, Ler, and Biblioteca views refresh their active-reading state. Notes entered during focus sessions are persisted by the reading-session finish endpoint and shown in the session summary and the book's library details.

Generate `apps/ios/Yomora.xcodeproj` from `apps/ios/project.yml` with XcodeGen after adding targets or source files. Keep device-only values in ignored `Config/Local.xcconfig`; the committed plist must contain build-setting references only. Reading sessions update the Live Activity, and opt-in reading reminders are local notifications rebuilt when goals or sessions change. UI tests inject no-op managers so system permission dialogs never make CI nondeterministic.

Useful commands: `make setup`, `make backend`, `make test`, `make docker-up`, `make seed`, `make ios-test`. Backend `./gradlew check` includes SpotBugs and FindSecBugs. GitHub security CI uses Semgrep CE, Trivy and zizmor; Dependabot maintains Gradle and GitHub Actions dependencies. Configuration belongs in environment variables; never commit real secrets.
