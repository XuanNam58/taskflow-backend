# TaskFlow Backend

Spring Boot 3.5 / Java 21 / PostgreSQL / Flyway. A learning project driven by
`fullstack_learning_roadmap.md`. The goal is production-quality code, not tutorial code.

## Build & run

```bash
sh mvnw test                      # requires Docker (Testcontainers)
sh mvnw spring-boot:run
```

Use `sh mvnw`, not `./mvnw` — the `mvnw` file has no execute bit in the git index.

Running the app requires `APP_JWT_SECRET` (no default) and PostgreSQL at
`localhost:5432/taskflow`. Tests need neither — Testcontainers starts its own database.

## Conventions

- **Package by feature**: `auth/`, `user/`, `common/`. Inside each: `controller/`, `service/`,
  `service/impl/`, `repository/`, `entity/`, `dto/request/`, `dto/response/`, `mapper/`.
- **Every business error** throws `BusinessException(ErrorCode.X)`. Never throw
  `ResponseStatusException`, never build an error response by hand in a controller. New error cases go
  into the `ErrorCode` enum.
- **DTOs are `record`s** validated with Bean Validation. Never return a JPA entity from a controller.
- **Entity to DTO through MapStruct** (`UserMapper`). No hand-written mapping.
- **Flyway**: `V{n}__{snake_case_description}.sql`. Never edit a committed migration — add a new file.
  `ddl-auto` is `validate`, so the schema only ever changes through Flyway.
- **Thin controllers**: accept the request, call the service, return the response. Logic lives in the
  service.
- `@Transactional` belongs on the service layer, never on a controller or repository.

## Git

- Commits: `type(scope): message`, e.g. `feat(auth): implement login`, `fix(common): handle null path`.
- One branch per vertical slice: `feat/workspace-backend`, `fix/jwt-filter-exception-handling`.
- Never commit straight to `main`. Merge through a pull request, even when working alone.
- `git pull --rebase` so history does not collect merge commits.

## How to work with me

Answer in Vietnamese unless I ask otherwise. Code, identifiers, and this file stay in English.

**Correct my English.** When I write a prompt in English, finish the answer and then add a
`**Prompt feedback**` block of at most three lines: the corrected sentence plus the main mistakes.
Short, no lecturing. If the prompt was already fine, just say "OK".

**Teaching protocol.** This is a project I learn from, so when a topic is new to me:

1. Give the mental model first — what problem existed before this, and when *not* to use it. Keep it
   under ten lines.
2. Point me at one to three specific primary sources (a named doc section, not a search suggestion).
3. Define the smallest exercise that proves I understood it.
4. Do not write the implementation for me. I write it, then you review it.
5. After I finish, ask me two or three interview-style questions to check that I actually understood
   rather than copied.

When I am stuck, give escalating hints — name the area first, then the concept, and only give the
answer if I ask a third time. When I already have production experience with something (Spring Batch,
Redis, QueryDSL), skip step 1 and go straight to the hard parts.

**When you find a bug**, explain why it is wrong and how I could have found it myself, before proposing
a fix. Do not fix it unless I ask.
