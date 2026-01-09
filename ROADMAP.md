# 📘 LexiconMeum Roadmap

A staged development plan to balance MVP delivery, engineering quality, and learning goals.

---

## ✅ Phase 1 – MVP Completion (User-Facing Polish)

**Goal:** Deliver a usable, professional-feeling interface that serves basic learner needs.

- [ ] Add support for all word types (e.g. adjectives, adverbs, etc.)
    - [x] nouns
    - [x] verbs
    - [x] adjectives
    - [x] adverbs
    - [x] prepositions 
    - [x] conjunctions
    - [x] pronouns 
- [ ] Complete verb support (add passive voice and any missing tenses/moods)
    - [x] Add Participles to Detail API
    - [x] Add Passive Inflections to Detail API 
- [ ] Include positive, comparative, and superlative adjective forms in a single detail response 
- [ ] Fix dropdown arrow navigation in search field
- [ ] Automatically clear error messages when input changes or retry succeeds

---

## 🔁 Phase 1.5 – Front-End Deployment Automation

**Goal:** Match the backend's seamless deploy process to reduce friction and risk.

- [x] Automate front-end build and deployment (GitHub Actions or local script)
- [ ] Use `rsync` or safe delete strategy to prevent accidental file loss
- [ ] Version build output for traceability (e.g., `lexiconmeum-frontend-20250725`)
- [ ] Ensure deploy logs clearly show success/failure
- [ ] Match Vite output to server expectations

---

## 🧪 Phase 1.6 – Front-End Testing

- [x] Set up Vitest for unit testing
- [x] Write tests for suggestion box logic, error handling, etc.
- [ ] Test keyboard navigation behavior in dropdown
- [ ] Run tests in CI (GitHub Actions or locally)

---

## ⚙️ Phase 2 – Performance & Service Structure

**Goal:** Improve responsiveness and prepare for future reliability and redundancy.

- [ ] Extract parser into a dedicated service (or CLI) to isolate boot-time logic
- [ ] Generate and store parsed output as a snapshot JSON file for faster boot time
- [ ] Let main API load from pre-parsed file to reduce startup latency
- [ ] Enable on-demand data refresh or versioned data switching

---

## ⚙️ Phase 3 – Scalability, Redundancy & System Enhancements

**Goal:** Extend the platform with targeted enhancements that improve startup time, enable redundancy, and support future scalability.
- 
- [ ] Create a visual architecture diagram and include it in `README.md`
- [ ] Add support for data refresh via webhook, polling, or admin endpoint
- [ ] Experiment with Redis or off-heap cache for inflection storage
- [ ] Containerize parser + API + frontend for consistent dev environment
- [ ] Document tradeoffs and patterns used in `ARCHITECTURE.md` or blog post

---

## 🔗 Related

- [ ] [README.md](./README.md) – Project overview and deployment notes
- [ ] [GitHub Issues](../../issues) – Task tracking
- [ ] [GitHub Actions](../../actions) – Deployment status
