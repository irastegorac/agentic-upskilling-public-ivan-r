# Prompt Journal — Exercise 1: Structured Prompting

## Round A: Vague Prompt

**Prompt korišten:**
> "Add retry logic to PaymentService."

**Pretpostavke koje je Claude napravio:**
- _(language, retry count, koje metode se retryaju, backoff strategija, thread safety)_

**Što nedostaje ili je pogrešno u outputu:**
-
"Here's what changed:

- charge — validates input (non-retryable), then delegates to attemptCharge in a retry loop. On RuntimeException, it waits 100ms and retries up to 3 times, then rethrows. Handles InterruptedException safely.
- attemptCharge (new private method) — contains the original charge logic; insufficient balance returns false immediately without triggering a retry (it's deterministic, not transient).
- MAX_RETRIES = 3, RETRY_DELAY_MS = 100 as constants for easy tuning.                                                                                                                                                       
  "
---

## Round B: Context and Behavioral Specification

**Prompt korišten:**
> "Add retry logic to the `charge` method in PaymentService.java. The method should retry up to 3 times on network-related errors only, using exponential backoff starting at 200ms. Do not retry on IllegalArgumentException or insufficient balance — those are not transient errors."

**Što se popravilo u odnosu na Round A:**
-
"Key design decisions:

- Network errors only — the loop catches IOException exclusively. IllegalArgumentException (invalid input) propagates immediately; insufficient balance returns false inside attemptCharge without ever throwing, so it's   
  never retried.
- Exponential backoff — 200 * 2^(attempt-1) gives delays of 200ms → 400ms → 800ms across the 3 retries.
- charge declares throws IOException — callers are forced to handle the case where all retries are exhausted, rather than having it silently swallowed in an unchecked wrapper.
- attemptCharge throws IOException — the declared contract for network failures, keeping the retry logic cleanly separated from the business logic. "
---

Tri konkretne razlike:

1. Koje greške se retryjaju — prvi prompt je retryao svaki RuntimeException. Drugi specificira samo IOException (mrežne greške). IllegalArgumentException i insufficient balance nikad ne triggiraju retry.
2. Delay strategija — prvi: fiksnih 100ms između pokušaja. Drugi: eksponencijalni backoff — 200ms → 400ms → 800ms (200 * 2^(attempt-1)).
3. Checked exception na charge — drugi prompt je zahtijevao da charge deklarira throws IOException, što forsira callere da eksplicitno handlaju scenarij kad svi retryi ne uspiju. U prvoj verziji greška je bila omotana u
   unchecked RuntimeException.


## Round C: Constraints

**Prompt korišten:**
> "Additionally: do not add any new dependencies, keep the `charge` method signature identical to how it is now, make the retry logic thread-safe, and add a log line for each retry attempt including the attempt number and delay."

**Što se popravilo u odnosu na Round B:**
-
"Three things changed:

- Thread safety — attemptCharge, refund, and getBalance are all synchronized on this. Critically, Thread.sleep stays in charge (unsynchronized) so sleeping threads don't hold the lock and block other threads from        
  completing their own charges or refunds.
- Log line — now reads "Retry attempt 1 of 3 for customer X, waiting 200ms before next attempt" — explicit attempt number, total retries, and delay.
- charge signature — unchanged (throws IOException)."

---

## Round D: Verification Instruction

**Prompt korišten:**
> "After implementing the retry logic, run any existing tests to confirm they still pass."

**Kako je Claude reagirao na verifikacijsku instrukciju:**
-
"No test files exist in the project — only PaymentService.java and prompt_journal.md. There's no build tool (no pom.xml, build.gradle, etc.) either, so there's nothing to run.

If you'd like, I can write a PaymentServiceTest.java covering the retry behaviour, thread safety, and the non-retry cases (IllegalArgumentException, insufficient balance)."
**Je li uočio regresije:**
-

---

## Bonus: Plan Mode Comparison

**High-level opis dan Claudeu u plan modeu:**
> "I need to add retry logic to PaymentService.java."

**Plan koji je Claude generirao:**
-

**Usporedba s Round C i Round D promptovima:**
- Gdje se preklapaju:
- Što plan sadrži a promptovi nisu:
- Što promptovi sadrže a plan nije:

---

## Reflection

- U kojoj rundi je output prvi put bio upotrebljiv bez značajnog ručnog editiranja?
- Koja vrsta detalja je imala najveći utjecaj: behavioral requirements, constraints ili verification instructions?
- Je li Claude-ov self-generated plan u plan modeu više ličio na Round C ili Round D prompt?
- Postoji li točka diminishing returns gdje dodavanje više detalja ne poboljšava output?


" The implementation already matches the plan exactly — no code changes needed. Now for the comparison you originally asked about:
                                                                                                                                                                                                                              
---                                                                                                                                                                                                                       
Gdje se preklapaju (plan ↔ Round C + D):
- Retry only IOException, not IllegalArgumentException or insufficient balance
- Exponential backoff starting at 200ms (200→400→800ms)
- MAX_RETRIES = 3
- synchronized on attemptCharge, refund, getBalance; sleep outside the lock
- Log warning per retry with attempt number and delay
- No new dependencies

  ---                                                                                                                                                                                                                         
Što plan sadrži a promptovi nisu:
- Eksplicitno objašnjenje zašto refund nije retryjan (idempotency risk — double-credit)
- Trade-off diskusija: thundering herd (nedostaje jitter), ReentrantLock kao alternativa
- 7 konkretnih test case-ova s verifikacijskim uputama (javac / java -ea)
- ⚠️  Plan preporučuje promjenu signature-a (throws IOException) — Round C je to eksplicitno zabranio

  ---                                                                                                                                                                                                                         
Što promptovi sadrže a plan nije:
- Round C: "keep the charge method signature identical" — plan to ne pretpostavlja, dapače preporučuje suprotno
- Round D: "run any existing tests" — plan pretpostavlja da testovi ne postoje i da ih treba napisati; nije pomislio provjeriti prvo

  ---                                                                                                                                                                                                                         
Ključni zaključak za journal: Plan mode je sam otkrio thread safety, backoff reasoning i verifikaciju — ali je napravio krivu pretpostavku o signature-u. Round C je morao eksplicitno ispraviti tu pretpostavku            
constraintom. Round D je otkrio da plan nije uzeo u obzir provjeru postojećih testova. "