.PHONY: setup backend test docker-up docker-down seed openapi ios-test

setup:
	cp -n .env.example .env || true
	cd services/api && ./gradlew classes

backend:
	set -a; [ ! -f .env ] || . ./.env; set +a; cd services/api && ./gradlew bootRun

test:
	cd services/api && ./gradlew clean test

docker-up:
	docker compose --env-file .env -f infrastructure/docker-compose.yml up --build -d

docker-down:
	docker compose --env-file .env -f infrastructure/docker-compose.yml down

seed:
	docker compose --env-file .env -f infrastructure/docker-compose.yml exec -T postgres \
		psql -U $${POSTGRES_USER:-yomora} -d $${POSTGRES_DB:-yomora} -f /demo/demo-data.sql

openapi:
	./scripts/validate-openapi.sh

ios-test:
	cd apps/ios && xcodebuild test -project Yomora.xcodeproj -scheme Yomora \
		-destination 'platform=iOS Simulator,name=iPhone 17 Pro'
