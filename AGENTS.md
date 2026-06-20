# AGENTS.md

## Overview
- Multi-module Gradle project for a PaperMC plugin.
- Modules:
  - `api`: shared domain interfaces and model types.
  - `core`: implementation code shared by runtime and tests.
  - `papermc`: Paper plugin bootstrap, commands, and packaging.

## Working Rules
- Keep changes scoped to the relevant module.
- Put cross-module contracts in `api`.
- Put persistence and service implementations in `core`.
- Put Paper-specific wiring, commands, bootstrap, and resource packaging in `papermc`.
- Follow existing Kotlin style and keep package naming under `kr.doka.template`.

## Build And Validation
- Format check/fix: `./gradlew spotlessCheck` / `./gradlew spotlessApply`
- Full build: `./gradlew build`
- Tests: `./gradlew test`
- Core tests only: `./gradlew :core:test`

## Project Notes
- Kotlin JVM toolchain is set to Java 25 across modules.
- `papermc` depends on `api` and `core` and produces the plugin artifact.
- `core` uses Exposed and keeps JDBC drivers mostly out of runtime API surface.
- `papermc/src/main/resources/libraries.properties` is expanded from Gradle version properties during `processResources`.

## Change Guidance
- Prefer adding tests in `core/src/test` when changing repository or database behavior.
- When changing plugin bootstrap or command behavior, verify the entrypoints stay aligned with `paperPluginYaml`.
- Avoid introducing Paper-specific dependencies into `api` or `core` unless there is a clear architectural reason.
