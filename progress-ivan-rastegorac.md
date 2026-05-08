# Individual Progress Tracker

---

## Engineer Information

| Field | Value                               |
|---|-------------------------------------|
| **Name** | Ivan Raštegorac                     |
| **Role** | IC                                  |
| **Team** | Upskilling Kraljević                |
| **Program Start Date** | 2026-04-06                          |
| **Primary Agentic Tool** | Claude Code                         |
| **Secondary Tools** | _List any additional tools you use_ |
| **Primary Stack** | Java / Spring Boot                  |
| **Manager** | Lukša Kraljević                     |

---

## Level Progression

| Field | Value      |
|---|------------|
| **Baseline Level (at program start)** | 1          |
| **Current Level** | 2          |
| **Target Level** | 3          |
| **Target Date** | 30.06.2026 |

---

## Self-Assessment History

Record results each time you complete a self-assessment or formal evaluation.

| Date       | Assessed Level | Assessment Type | Key Strengths | Areas to Improve | Notes |
|------------|---|---|---|---|---|
| 2026-04-06 | 1 | Self | _e.g., prompt specificity_ | _e.g., context management_ | Initial self-assessment |
| 2026-05-08 | 1 | Certification (AI-evaluated) | Structured prompting, Spring Boot context (92%, Quiz 95%) | Deeper comparative analysis s konkretnim primjerima | Spring Boot Prompting Basics — PASSED |

---

## Learning Path Progress

Check off modules as you complete them. Add the completion date next to each item.

### Level 1 — Foundations

- [x] Module 1: Claude Code Installation & First Session — _Date: 
- [x] Module 2: The Agentic Loop in Action — _Date:
- [x] Module 3: The Permission System — _Date:
- [x] Module 4: Your First AI-Assisted Task — 
- [x] Module 5: Understanding AI Limitations — 
- [x] Module 6: Building Good Habits — 
- [x] Foundations Exercises — _Date: 2026-04-12 (hello-agentic/ projekt: HealthController, ShoppingCart, OrderProcessor, LRUCache, RateLimiter, StringUtils + testovi)

### Level 2 — Intermediate

- [x] Module 1: Prompt Engineering — _Date: 2026-04-27_
- [x] Module 2: CLAUDE.md — _Date: 2026-04-27_
- [x] Module 3: Context Management — _Date: 2026-04-28_
- [x] Module 4: Plan Mode — _Date: 2026-04-28_
- [x] Module 5: Context Window Management — _Date: 2026-04-28_
- [x] Module 6: Skills — _Date: 2026-04-28_
- [x] Module 7: Critical Review of AI Output — _Date: 2026-04-28_
- [x] Module 8: Workflow Integration & Team Collaboration — _Date: 2026-04-28_

### Level 3 — Advanced

- [ ] Module 1: Multi-File and Multi-Step Workflows — _Date: ___
- [ ] Module 2: Architecture-Level Conversations — _Date: ___
- [ ] Module 3: Custom Tool and MCP Integration — _Date: ___
- [ ] Module 4: Advanced Context Window Management — _Date: ___
- [ ] Module 5: Security-Aware AI-Assisted Development — _Date: ___
- [ ] Module 6: Performance Optimization with AI — _Date: ___
- [ ] Module 7: Cross-Repository and System-Level Tasks — _Date: ___
- [ ] Advanced Capstone Exercise — _Date: ___

### Level 4 — Expert

- [ ] Module 1: Designing Agentic Workflows for Teams — _Date: ___
- [ ] Module 2: Building Custom MCP Servers — _Date: ___
- [ ] Module 3: Evaluating and Benchmarking AI Tools — _Date: ___
- [ ] Module 4: AI-Assisted System Design and Architecture — _Date: ___
- [ ] Module 5: Organizational Adoption and Change Management — _Date: ___
- [ ] Module 6: Frontier Techniques and Emerging Patterns — _Date: ___
- [ ] Expert Capstone Exercise — _Date: ___

---

## Exercise Completion Log

Track every exercise you complete, what you learned, and what you would do differently.

| Exercise Name | Level | Date Completed | Time Spent | Key Takeaway | What I Would Do Differently |
|---|---|---|---|---|---|
| Exercise 1: Structured Prompting | L2 | 2026-04-27 | | Plan mode otkriva thread safety i backoff reasoning sam, ali constraints su neophodni za tvrde zahtjeve (npr. signature) | |
| Exercise 2: CLAUDE.md Configuration | L2 | 2026-04-27 | | Kad postojeći kod demonstrira konvencije, Claude ih inferencira bez CLAUDE.md — dodaj entryje samo reaktivno za stvari koje Claude griješi | |
| Exercise 3: Context with @ Selectors | L2 | 2026-04-28 | | Claude čita cijeli projekt i inferencira paterne bez @ — @selector je koristan tek u velikim projektima gdje Claude ne bi sam pronašao pravi referentni fajl | |
| Exercise 4: Plan Mode | L2 | 2026-04-28 | | Plan mode otkriva arhitekturalne paterne sam (AuditLogger + constructor injection), ali revizija je bila potrebna za thread safety i izbjegavanje code duplication | |
| Exercise 5: Context Window Management | L2 | 2026-04-28 | | /clear na ~40%, fajlovi su source of truth; /compact zadržava tok rada, /clear je bolji između nepovezanih zadataka; CLAUDE.md prenosi odluke između sesija | |
| Exercise 6: Skills | L2 | 2026-04-28 | | Skill dostigao 90% (Description 100%, Content 77%); skill reproducibilno prati konvencije + CLAUDE.md pravila koje ad hoc prompt lako propusti | |
| Exercise 7: Four-Layer Code Review | L2 | 2026-04-28 | | Claude pronašao sve zasađene greške + 1 bonus (input validation); review-security skill dostigao 95%, nula false positiva na čistom fajlu | |
| Spring Boot Prompting Basics (L1 cert) | L1 | 2026-05-08 | | Structured prompt (Step 2: 9/10) — entity fields + constructor injection + anotacije | Konkretnija usporedba minimal vs structured outputa |

---

## Skills Snapshot

Rate yourself on each skill dimension. Update quarterly or after significant learning milestones.

**Rating Scale:** 1 = Awareness, 2 = Working Knowledge, 3 = Proficient, 4 = Expert

| Skill Dimension | Rating (1-4) | Evidence / Notes | Last Updated |
|---|---|---|--|
| **Prompt Engineering** | 2 | 4-komponentni framework (opis, constraints, primjeri, verifikacija); plan mode za kompleksne zadatke | 2026-04-28 |
| **Context Management** | 2 | /clear na ~40%, fajlovi su source of truth, CLAUDE.md prenosi odluke između sesija, @ selector | 2026-04-28 |
| **Output Evaluation** | 2 | 4-layer review (correctness, security, performance, maintainability); review-security skill (95%) | 2026-04-28 |
| **Tool Proficiency** | 2 | Plan mode, /clear, /compact, skills, CLAUDE.md, Tessl CLI lint i review | 2026-04-28 |
| **Workflow Integration** | 2 | Claude Code u daily workflow; plan-review-execute ciklus; session planning | 2026-04-28 |
| **Security Awareness** | 2 | Identificirao security issues u TokenService (non-final key, NPE, crypto); review-security skill | 2026-04-28 |
| **Testing with AI** | 2 | 70+ testova generiranih i verificiranih; test naming konvencije; verifikacijska instrukcija u promptovima | 2026-04-28 |
| **Architecture & Design** | 1 | Plan mode za arhitekturalne odluke (AuditLogger interface, constructor injection) | 2026-04-28 |
| **Collaboration & Sharing** | 2 | 2 skilla u .claude/ (new-service 90%, review-security 95%) commitana u git za tim | 2026-04-28 |
| **Debugging with AI** | 1 | Osnovno — nije eksplicitno pokriveno u L2 vježbama | 2026-04-28 |

---

## Quarterly Goals

### Current Quarter: Q2 2026

| # | Goal | Measurable Target | Status | Notes |
|---|---|---|---|---|
| 1 | Završiti Level 1 Foundations (uključujući vježbe) | Svi moduli i vježbe označeni kao završeni | Completed | |
| 2 | Završiti Level 2 Intermediate learning path | Svih 8 modula završeno | Completed | Završeno 2026-04-28 |
| 3 | Završiti Level 3 Advanced learning path | Svih 8 modula završeno | Not Started | |
| 4 | Dostići L3 razinu kompetencije | Samoprocjena potvrđuje L3 | Not Started | |

### Previous Quarter Goals (for reference)

| # | Goal | Measurable Target | Outcome |
|---|---|---|---|
| | | | |

---

## Reflection Journal

Write brief entries after meaningful learning experiences, difficult problems solved with AI, or insights about your workflow. This is for your own benefit — be honest and specific.

### 2026-04-28 — Završen Level 2 Intermediate

_What happened:_ Prošao sam svih 8 modula L2 i odradio 7 vježbi u Java projektima (payment-service, billing-service, order-service). Kreirao sam dva skilla evaluirana s Tessl CLI-em (new-service 90%, review-security 95%) i postavio sve na GitHub.

_What I learned:_ Najvažniji insight je da Claude konzistentno čita postojeći kod i inferencira konvencije bez eksplicitnih uputa — CLAUDE.md i @ selector dodaju vrijednost samo kad kod ne može sam pokazati pravilo. Plan mode sam predlaže dobre arhitekturalne paterne, ali explicit constraints su neophodni za tvrde zahtjeve. Fajlovi su source of truth, ne conversation historija — /clear na ~40% sprječava degradaciju kvalitete.

_What I will do differently:_ Ranije koristiti plan mode za zadatke koji diraju više fajlova, i reaktivnije ažurirati CLAUDE.md — dodavati entryje odmah kad Claude napravi grešku, ne retroaktivno.

---

### 2026-04-12 — Završena Level 1 teorija

_What happened:_ Prošao sam kompletnu Foundations teoriju (svih 6 modula) i upoznao se s pristupom korištenja Claude Codea kao tutora, peera i assessora.

_What I learned:_ Agentic loop ciklus (primi zadatak → čitaj kontekst → planiraj → izvrši → promatraj → odluči → ponovi), sustav dozvola, ograničenja LLM-ova (halucinacije, kontekst, sigurnost, zastarjelo znanje), i važnost odvajanja radne sesije od sesije za učenje.

_What I will do differently:_ Primijeniti two-session pristup za sve buduće vježbe — odvojena radna sesija i sesija za učenje/procjenu.

---

_(Add new entries at the top so the most recent is always first.)_

---

## Manager Notes

> This section is for your manager to add notes during 1:1s, quarterly reviews, or after observing your work. Engineers: leave this section for your manager.

| Date | Note | Follow-Up Needed? |
|---|---|---|
| | | |

---

_Last updated: 2026-04-16_
