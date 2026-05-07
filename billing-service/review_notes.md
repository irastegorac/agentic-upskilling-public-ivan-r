# Review Notes — Exercise 7: Four-Layer Code Review

## Human Review

### Correctness
-

### Security
-

### Performance
-

### Maintainability
-

---

## Claude Code Review

### Correctness
- [CRITICAL] Token expiry off by 1000× — `nowMillis + TOKEN_EXPIRY` umjesto `nowMillis + TOKEN_EXPIRY * 1000L` → token istječe za ~86 sekundi, ne 24h
- [HIGH] NPE u validateToken — `claims.getSubject()` može biti null; `.isEmpty()` baca NPE umjesto IllegalArgumentException

### Security
- [HIGH] `secretKey` nije final — može biti mutiran nakon konstrukcije
- [MEDIUM] Nema input validation u `issueToken` — null/blank userId ili role producira token koji validateToken odbija tek nakon što je potpisan

### Performance
- [MEDIUM] `SecretKeySpec` se kreira nanovo pri svakom pozivu u `validateToken` i `getKey()` — treba biti cachean kao `private final Key signingKey` u konstruktoru

### Maintainability
- [LOW] `TOKEN_EXPIRY` ne govori jedinicu — treba biti `TOKEN_EXPIRY_SECONDS`
- [LOW] `getKey()` naziv sugerira jeftin field read, a zapravo alocira — treba biti `buildSigningKey()` ili nestati nakon performance fixa

---

## Comparison

### Issues koje sam našao a Claude nije:
- (Part A preskočen)

### Issues koje je Claude našao a ja nisam:
- Sve zasađene greške pronađene (5/5)
- Bonus: input validation u `issueToken` — nije bila zasađena greška, Claude ju je sam identificirao

### Neslaganja u severityju:
- Nema — Claude je ispravno rangirao expiry bug kao CRITICAL, ostalo HIGH/MEDIUM/LOW
