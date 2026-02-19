## Phase 4: Future-Proofing for GUI and Mobile

### 1) Threading and Async Strategy

In a console application, blocking the main thread while waiting for an API response is usually acceptable because the “UI” is just sequential input/output. In JavaFX and Android, blocking the UI thread freezes rendering and event handling, creating an unresponsive experience and potentially triggering platform warnings or application-not-responding behavior.

To remain future-proof, all remote operations should return `CompletableFuture<T>` so callers can attach callbacks (`thenApply`, `thenAccept`, `exceptionally`, `handle`) and decide how and where to update UI state. Scheduling concerns belong in the **UI/controller layer** (for example, dispatching results back to the JavaFX Application Thread or Android main thread), while the **service layer** focuses solely on completing the future with a domain result or completing exceptionally with a service-level error.

- **CompletableFuture<T> vs RxJava3**
    - **CompletableFuture<T>** is an excellent default for request–response workflows where results are consumed once and reflected in the UI.
    - **RxJava3** becomes useful when modeling continuous streams, complex composition, or advanced retry/backoff policies, but may be unnecessary overhead for simpler interaction flows.

---

### 2) Decoupling and “Zero-Change” Components

If `TerminalUI` is replaced with a JavaFX view, the components that should require **zero changes** are the **domain layer** (`Game`, `Guess`, `Score`), the **service boundary** (`CodebreakerService`), and the service implementation contract (`RestCodebreakerService`). These components remain unchanged because they are intentionally UI-agnostic and depend only on stable interfaces and domain concepts.

The components that may change live at the edges of the system. The terminal-based UI is replaced with JavaFX views and event handlers, and some presentation logic shifts from a linear loop to an event-driven model. The `GameController` may require minor adjustments to ensure UI updates occur on the correct thread, but its orchestration responsibilities remain the same. This separation ensures platform changes affect only the outermost layer.

- **Zero-change:** Domain models, service interface, DTO/mapping concepts, service-level error taxonomy
- **Likely changes:** UI rendering, input handling, event wiring
- **Minor adjustments:** UI-thread dispatch logic in controller callbacks

---

### 3) Android Constraints Checklist

Android environments introduce stricter networking, security, and lifecycle constraints than desktop platforms. Designing with these constraints in mind ensures the `RestCodebreakerService` can transition to mobile without architectural rework.

Key considerations include:

- **HTTPS/TLS:** Assume HTTPS-only communication; avoid reliance on cleartext traffic.
- **Certificate trust:** Be cautious with custom trust managers or certificate pinning.
- **Network permissions and variability:** Expect intermittent connectivity; design for retries, timeouts, and graceful failure.
- **Main thread restrictions:** All networking must occur off the main thread; futures should complete asynchronously.
- **Lifecycle awareness:** Avoid leaking Activities or Views via async callbacks; detach listeners cleanly.
- **Timeouts and retries:** Use conservative retry strategies to preserve battery and bandwidth.
- **Secure storage:** Treat persisted identifiers or logs as potentially sensitive; avoid logging secrets.

---

### 4) Platform Evolution Diagram

```mermaid
flowchart TB
  UI1[TerminalUI] --> CTRL[GameController];
  UI2[JavaFXView] --> CTRL;

  CTRL --> SVC[CodebreakerService];
  SVC --> IMPL[RestCodebreakerService];

  CTRL --> DOM[Domain Models<br/>Game / Guess / Score];

  subgraph Unchanged Across Platforms
    SVC;
    IMPL;
    DOM;
  end
