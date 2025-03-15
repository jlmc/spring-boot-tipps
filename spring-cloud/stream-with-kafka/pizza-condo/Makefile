up:
	DOCKER_DEFAULT_PLATFORM=linux/amd64 docker compose up -d

down:
	docker compose down

restart:
	make down
	make up

logs:
	docker compose logs -f

ps:
	docker compose ps