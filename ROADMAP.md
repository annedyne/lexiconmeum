# LexiconMeum Roadmap

This roadmap outlines the next planned stages of work for the project. It is intended to show current priorities clearly without overstating project maturity.

## Phase 1 — Product Completeness and UX Polish

**Goal:** Improve feature completeness and frontend usability on top of the current backend/API foundation.

### Dictionary and grammar coverage

- [ ] Add support for remaining word-type gaps where needed
    - [x] nouns
    - [x] verbs
    - [x] adjectives
    - [x] adverbs
    - [x] prepositions
    - [x] conjunctions
    - [x] pronouns

### Verb and adjective detail completeness

- [ ] Complete remaining verb support, including passive voice and any missing tense or mood combinations
    - [x] Add participles to the detail API
    - [x] Add passive inflections to the detail API
- [x] Include positive, comparative, and superlative adjective forms in a single detail response

### Frontend polish

- [ ] Fix dropdown arrow-key navigation in the search field
- [ ] Automatically clear error messages when input changes or retry succeeds

## Phase 1.5 — Frontend Deployment

**Goal:** Make frontend deployment more predictable and easier to verify.

- [x] Automate frontend build and deployment
- [ ] Use `rsync` or another safe deletion strategy to avoid accidental file loss
- [ ] Version build output for traceability
- [ ] Make deploy logs clearly indicate success or failure
- [ ] Align Vite output with server/runtime expectations

## Phase 1.6 — Frontend Test Coverage

**Goal:** Improve confidence in UI behavior and reduce regression risk.

- [x] Set up Vitest for unit testing
- [x] Add tests for suggestion-box logic and error handling
- [ ] Add keyboard navigation tests for the dropdown
- [ ] Run frontend tests in CI

## Phase 2 — Startup Performance and Service Separation

**Goal:** Reduce boot-time work and separate data preparation from API serving.

- [ ] Extract parsing into a dedicated service or CLI workflow
- [ ] Generate and store parsed output as a reusable snapshot
- [ ] Load the API from pre-parsed data to reduce startup latency
- [ ] Support on-demand data refresh or versioned data switching

## Phase 3 — Scalability and Operations

**Goal:** Add targeted infrastructure and runtime improvements without overcomplicating the current architecture.

- [ ] Add a public-facing architecture diagram and reference it from `README.md`
- [ ] Add support for data refresh via webhook, polling, or admin endpoint
- [ ] Experiment with Redis or off-heap cache options for lexical data
- [ ] Containerize parser, API, and frontend for more consistent local and development environments
- [ ] Expand architecture documentation around major tradeoffs and patterns

## Related documents

- [README.md](README.md) — project overview and local run instructions
- [ARCHITECTURE.md](docs/ARCHITECTURE.md) — system design and runtime flow
- [docs/RELEASING.md](docs/RELEASING.md) — maintainer release and deployment workflow