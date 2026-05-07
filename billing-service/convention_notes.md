# Convention Notes — Exercise 2: CLAUDE.md Configuration

## Part A: Baseline Without CLAUDE.md

**Prompt korišten:**
> "Add a cancel invoice feature to billing-service. It should include a service method, a controller method, and a test."

**Convention violations uočene u outputu:** nema — Claude je čitao postojeći kod i slijedio sve paterne

| Convention | Status |
|---|---|
| Constructor injection used | ✓ — slijedio pattern iz InvoiceController |
| Exception extends BillingException | ✓ — cancel() baca BillingException za non-PENDING status |
| Input validation in service | ✓ — status check prije akcije |
| No business logic in controller | ✓ — cancelInvoice() samo delegira na service |
| Test method naming pattern | ✓ — cancel_pendingInvoice_updatesToCancelled, cancel_paidInvoice_throwsBillingException |

**Zaključak:** Bez CLAUDE.md, Claude je sve konvencije ispoštovao jer ih može zaključiti iz postojećeg koda. Potvrđuje teoriju: ne dodavaj CLAUDE.md entryje za stvari koje Claude već može inferencati.

---

## Part D: Before vs. After CLAUDE.md

| Convention | Before CLAUDE.md | After CLAUDE.md |
|---|---|---|
| Constructor injection used | ✓ | ✓ |
| Exception extends BillingException | ✓ | ✓ |
| Input validation in service | ✓ | ✓ |
| No business logic in controller | ✓ | ✓ |
| Test method naming pattern | ✓ | ✓ |

**Napomena:** Part D nije dao usporedivu razliku jer je feature već bio implementiran iz Part A.
Ključni zaključak: CLAUDE.md konvencije koje su vidljive iz koda ne dodaju vrijednost — Claude ih inferencira sam.

---

## Part E: Noise Experiment

**Redundantno pravilo dodano u CLAUDE.md:**
- "Always use constructor injection"

**Prompt poslan:**
- "Add a findAll method to InvoiceService"

**Rezultat — je li pravilo uzrokovalo konfuziju, warning, ili nema efekta:**
- Nema efekta — Claude je ispoštovao pravilo (koje je ionako već pratio) bez konfuzije ili warningsa
- Bonus nalaz: Claude je primijetio da nema build systema (nema pom.xml/build.gradle) pa testovi ne mogu biti pokrenuti

---

## Reflection

- Koje su se konvencije najviše poboljšale između Part A i Part D?
- Postoje li konvencije koje Claude i dalje krši unatoč eksplicitnoj listi?
- Kakav efekt je imalo redundantno pravilo iz Part E?
- Kako bi podijelio pravila između project-level i package-level CLAUDE.md na pravom timskom projektu?
