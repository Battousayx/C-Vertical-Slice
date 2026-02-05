Purpose
-------
This file provides targeted guidance for AI coding agents working on this repository so they become productive immediately.

Quick Architecture
------------------
- Spring Boot (entry: src/main/java/br/com/music/api/MusicApiApplication.java) using Java 21.
- Layered structure by package under `br.com.music.api`:
  - `Domain` — JPA entities (e.g., Artista, Album, ArtistaAlbum).
  - `Repository` — Spring Data JPA interfaces.
  - `Services` — business logic and transactional boundaries.
  - `Controller` — REST endpoints and request/response mapping.
- Persistence: JPA + PostgreSQL (runtime dependency), migrations with Liquibase (resources/db/changelog).
- Security: Spring Security + OAuth2 authorization-server is included in `pom.xml`.

Build & Dev Commands
--------------------
- Build: `./mvnw clean package` (project uses Maven wrapper)
- Run locally: `./mvnw spring-boot:run` or run `MusicApiApplication` main class.
- Tests: `./mvnw test` (unit/integration tests live under `src/test/java`).
- Dev services: there's a docker-compose at `src/main/resources/services/docker/docker-compose.yml` for DB and supporting services (check it before use).

Project Conventions (explicit)
-----------------------------
- Entities live in `Domain` and use Jakarta Persistence annotations. Example: `src/main/java/br/com/music/api/Domain/Artista.java`.
- Repositories follow Spring Data naming; return types and optional usage should use `Optional<T>` where applicable.
- Services contain business rules; prefer constructor injection and annotate transactional boundaries at service methods.
- Controllers map to REST endpoints; validation uses `spring-boot-starter-validation`.
- Lombok is optional (declared as optional in `pom.xml`) — don't assume Lombok-generated methods unless present on the class.

Integration points & external dependencies
----------------------------------------
- PostgreSQL is the primary DB (org.postgresql dependency). Use Liquibase changelogs under `src/main/resources/db/changelog`.
- Docker-based local infra lives in `src/main/resources/services/docker` (docker-compose). Inspect before launching to avoid port conflicts.
- `src/main/resources/services/minIO` suggests object-storage integration; check configuration if implementing file uploads.

Code patterns and examples
-------------------------
- Entity relationship example: `Artista` has a `Set<ArtistaAlbum>` mapped with `mappedBy = "artista"` (see Domain/Artista.java).
- Enums are stored as `@Enumerated(EnumType.STRING)` to keep DB values readable and stable.
- Use `@Column(nullable = false)` for required fields and explicit `length` where present.

When editing or adding files
---------------------------
- Keep packages aligned with `br.com.music.api` root; use the existing package layout.
- Add Liquibase changelogs for any schema changes and reference them in `db.master.xml`.
- Update `src/main/resources/application.properties` (or use profiles) rather than hardcoding values in code.

Debugging tips
--------------
- Run the app from the IDE via `MusicApiApplication` to attach a debugger quickly.
- For DB-related issues, bring up the docker-compose in `src/main/resources/services/docker` then point `application.properties` to the running container.

What NOT to change without confirmation
-------------------------------------
- Do not remove Liquibase files or change changelog IDs — this will break migrations.
- Avoid changing security configuration broadly; coordinate with maintainers when altering OAuth2/security.

If anything is ambiguous
------------------------
- Ask for the intended behavior and point to the smallest reproducible example (file path + test or endpoint).

Controller & DTO patterns
-------------------------
- Controllers live under `src/main/java/br/com/music/api/Controller` and follow Spring MVC conventions: `@RestController`, request mappings with `@RequestMapping`/`@GetMapping`/`@PostMapping`, and typically return `ResponseEntity<T>`.
- Input validation uses Bean Validation annotations (`@Valid`, `@NotNull`, `@Size`) on request DTOs. Place DTOs in `Controller.dto` or a `dto` package alongside the controller.
- Map between `Domain` entities and DTOs in `Services` or use a small mapper class. Avoid returning JPA entities directly from controllers.
- Example references: `src/main/java/br/com/music/api/Domain/Artista.java`, `src/main/java/br/com/music/api/Domain/Album.java`.

Testing guidance
----------------
- Tests live under `src/test/java` (see `MusicApiApplicationTests.java`) and use `spring-boot-starter-test`.
- Use `@SpringBootTest` for full-context integration tests and `@WebMvcTest` or `@DataJpaTest` for focused slices. Use `@AutoConfigureMockMvc` + `MockMvc` for controller tests.
- Security-related endpoints: include `spring-security-test` utilities to mock authenticated principals in controller tests.

PR checklist (quick)
--------------------
- Run `./mvnw test` and ensure Liquibase migrations apply locally if the change touches the DB.
- If schema changes are needed, add a Liquibase changelog under `src/main/resources/db/changelog` and reference it in `db.master.xml`.
- Add/update integration tests for behavioral changes and include a brief description in the PR about required infra (DB ports, MinIO).

How to run local infra
----------------------
- The docker-compose lives at `src/main/resources/services/docker/docker-compose.yml` — run from repo root with:

```bash
docker-compose -f src/main/resources/services/docker/docker-compose.yml up
```

- After services are up, set DB connection properties in `src/main/resources/application.properties` or an active profile.

Feedback
--------
Please review these additions and tell me if you want more concrete Controller/DTO code examples, a sample test template, or a PR template to add to `.github/PULL_REQUEST_TEMPLATE.md`.
