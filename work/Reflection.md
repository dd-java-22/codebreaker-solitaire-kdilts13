## Prompt

I'm planning on building an application (primarily in Java, but possibly with some components written in Java) that acts as a client to the web service documented at https://ddc-java.services/codebreaker-solitaire/ , and lets the user play the game described there. I'm thinking of building a console-mode version, as well as a JavaFX version, and an Android version. What questions should I ask you, to start fleshing out the technical components and architecture of such an application?

## Response (formatted into markdown)

# Codebreaker Solitaire Client – Architecture Discovery Questions

This document lists **questions to ask (and answer)** in order to flesh out the technical components and architecture of a multi-client application (Console, JavaFX, Android) that acts as a client for the Codebreaker Solitaire web service.

The goal is to design *once*, reuse logic everywhere, and keep UI layers thin.

---

## 1) Product & Game-Flow Questions

Clarify what you’re actually building before committing to structure.

- What are the **core user flows**?
    - New game → guess loop → solved / error
- What **screens/views** are required per platform?
    - Console prompt loop
    - JavaFX screens
    - Android activities/fragments
- What **settings** should users choose?
    - Pool
    - Code length
    - Defaults
- How should the UI react to:
    - Invalid guess
    - Game already solved (409)
    - Game not found (404)
- Do we want a **guess history view**, and how should it appear on each platform?

---

## 2) API Contract Questions

Turn the API docs into concrete types and edge-case handling.

- What are the API endpoints and how do they map to **Java records**?
    - `Game`
    - `Guess`
    - Error responses
- What fields are **domain-relevant** vs **transport-only**?
- What validation should happen **client-side** vs deferred to the server?
    - Guess length
    - Characters in pool
- How do we map HTTP responses into **domain errors**?
    - InvalidInput
    - GameNotFound
    - GameAlreadySolved
    - NetworkFailure
    - UnexpectedServerError
- How does server-side cleanup (inactive/deleted games) affect UX?
    - Resume game
    - Error messaging
    - Auto-start new game

---

## 3) Architecture Shape Questions

Avoid rewriting logic three times.

- What is the ideal **module layout**?
    - `core` – domain + use cases
    - `api` – HTTP client + DTOs
    - `ui-console`
    - `ui-javafx`
    - `ui-android`
- Can we use **hexagonal / clean architecture**?
    - Ports:
        - `GameService`
        - `GameRepository`
        - `Clock`
    - Adapters:
        - REST client
        - File/DB persistence
        - UI layers
- Where is the boundary between:
    - API DTOs
    - Domain models

---

## 4) State Management Questions

This app is effectively a client-side state machine.

- What are the possible **game states**?
    - Idle
    - CreatingGame
    - Playing
    - SubmittingGuess
    - Solved
    - Error
- Is the server the **source of truth**, or do we update state optimistically?
- What does “Resume Game” mean?
    - Store `gameId` only?
    - Store full game snapshot?
- How does state propagate to different UIs consistently?

---

## 5) Networking & Concurrency Questions

Critical for JavaFX and Android responsiveness.

- What HTTP client works best across platforms?
    - `java.net.http.HttpClient`
    - OkHttp
    - Retrofit
- What async model should we standardize on?
    - `CompletableFuture`
    - ExecutorService
    - Kotlin coroutines (if allowed)
- How do we guarantee:
    - No UI thread blocking
    - Safe thread handoff to JavaFX / Android main thread

---

## 6) UI Architecture Questions

Keep UI thin, logic centralized.

### Console
- How do we structure input/output without a giant `while(true)` loop?
- How do we cleanly render state changes?

### JavaFX
- MVVM or MVP?
- What does the ViewModel API look like?
    - `startGame()`
    - `submitGuess()`
- How does state bind to controls?

### Android
- MVVM with Jetpack ViewModel?
- LiveData vs StateFlow?
- How do we reuse core use cases cleanly?

---

## 7) Persistence & Offline Considerations

Improve UX and resilience.

- What should be persisted?
    - Last game ID
    - Guess history
    - Settings
    - Stats
- What persistence mechanisms per platform?
    - Desktop: JSON / file
    - Android: Room
- How do we hide persistence behind an interface?
- What happens if a stored game no longer exists on the server?

---

## 8) Testing Strategy Questions

Prevent “works on my machine” failures.

- How do we unit-test core logic without the network?
    - Fake `GameService`
- What integration tests should hit the real API?
- How do we generate stable JSON fixtures?
- How do we test error handling and edge cases?

---

## 9) Build & Packaging Questions

Multi-target builds require early decisions.

- What Gradle multi-module layout works best?
- Should Android UI be Kotlin-only while core remains Java?
- How do we manage:
    - Base URLs
    - Environments
    - Build flavors

---

## Recommended Kickoff Questions (In Order)

Start with these to converge quickly:

1. Summarize the API into Java types and a `GameService` interface.
2. Propose a clean/hex architecture with a Gradle multi-module layout.
3. Define the gameplay state machine and how UI observes it.
4. Recommend an HTTP + async approach that works across all targets.
5. Define error mapping from HTTP → domain → user messages.
6. Outline a test strategy (unit + integration).