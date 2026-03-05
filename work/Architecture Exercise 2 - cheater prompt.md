## Initial prompt:

We are working on an architecture exercise in bootcamp. The idea is to keep everything high level. Small code snippets are ok, but please don't generate large amounts of code. I'm going to give you the entire design document including the rubric. Please give an answer that addresses the main design question, and specifically address each of the three design pillars. Please try give answers that meet 'excellent' criteria listed in the rubric. Please try to make each answer short or concise while still fully addressing the question.

## Additional input:

I also provided 2 screenshots of the document posted in Slack that displayed the whole design document.

## Output:

Here’s a high-level architecture that keeps **symbols + layout configurable** via **`.properties` + FXML swapping**, while Java code stays generic (no hard-coded “A–F”, no hard-coded button count/layout).

---

## Core idea

- **Configuration (no recompile):** A `ResourceBundle` (properties) defines:
   - which **symbol set** is active (numbers/colors/emojis)
   - how to **render** each symbol (visual text/emoji, optional style class)
   - which **FXML layout** to load (row vs grid vs palette style)
- **UI (dynamic):** FXML provides *containers* (e.g., `FlowPane`, `TilePane`, `HBox`) and placeholders; Java fills them at runtime.
- **Logic (stable):** Game logic uses **logical IDs** (e.g., `"R"`, `"A"`, `"3"`) independent of visuals.

This hits “excellent” separation: **ResourceBundle for symbols, FXML for layout**, and Java only “wires” them together.

---

## Pillar A — Data → UI Mapping (dynamic palette generation)

**Goal:** If properties says `symbols=A,B,C,D`, Java loops and creates buttons at runtime.

**Design:**
- FXML contains a dynamic container with a `fx:id`:
   - **Preferred:** `FlowPane` (wraps nicely as symbol count changes)
   - Alternative: `TilePane` (consistent grid-like palette)
- Properties contains a CSV list of **logical symbol IDs**.

**Runtime flow (small snippet only):**

```java
List<String> ids = Arrays.asList(bundle.getString("symbols").split("\\s*,\\s*"));
for (String id : ids) {
  Button b = new Button(resolveDisplayText(bundle, id));
  b.setUserData(id);
  paletteContainer.getChildren().add(b);
}
```

**Why this is “excellent”:**
- **Scales** to any number of symbols without UI breaking (container adapts).
- Java never “knows” the actual set; it only iterates the config list.

---

## Pillar B — Visual vs Logical Values (store “truth” in the Button)

**Goal:** A button might display 🔴 but the backend needs `"R"`.

**Design:**
- Treat each symbol as:
   - **Logical ID**: stable value used by the game (e.g., `"R"`)
   - **Display**: what user sees (e.g., `"🔴"` or `"Red"`)
- Store the logical ID on the `Button` using `setUserData()` (or a small `Symbol` record if you want slightly more structure).

**Event handling approach:**
- All symbol buttons can share one handler.
- Handler reads `button.getUserData()` to get the logical ID and passes that to the ViewModel/Service.

**Tiny snippet:**

```java
String logical = (String) ((Button)e.getSource()).getUserData();
viewModel.addGuessSymbol(logical);
```

**Why this is “excellent”:**
- Visuals can change freely (emoji, text, CSS) without touching game logic.
- Prevents “UI string parsing” hacks (robust + maintainable).

---

## Pillar C — Layout Swapping (choose row vs grid via config)

**Goal:** Some symbol sets want a 2x3 grid; others want a long row—controlled by config, not code changes.

**Design:**
- Properties contains an FXML name/path:
   - `layout.fxml=palette_grid.fxml` **or** `palette_row.fxml`
- App loads the chosen FXML via `FXMLLoader`, using the same `ResourceBundle`.

**High-level loading flow:**

```java
String fxml = bundle.getString("layout.fxml");
FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml), bundle);
Parent root = loader.load();
```

**Why this is “excellent”:**
- Layout is a pure asset swap: designers can add new layouts without code changes.
- Java remains stable: it always looks for the same *conceptual* placeholders (`paletteContainer`, etc.).

---

## Configuration shape (properties) that supports all 3 pillars

Example structure (illustrative, not exhaustive):

- `symbols=A,B,C,D`
- `symbol.A.display=🔴`
- `symbol.A.style=red-token` *(optional CSS class)*
- `symbol.B.display=🟠`
- `layout.fxml=palette_grid.fxml`

This keeps symbol sets “data-driven” and layout “asset-driven”.

---

## Robustness (rubric “excellent”)

Make config failure graceful, not crashy:

- Wrap bundle + FXML loading in `try/catch`.
- Use defaults with `Optional` / fallback keys:
   - If `layout.fxml` missing → load `palette_default.fxml`
   - If `symbols` missing → load a small safe default list (or show an error UI state)
   - If a symbol’s display missing → display the logical ID itself

Conceptually:
- “Missing config” should become:
   - **warning + fallback**, or
   - **visible error message** in UI (instead of a stacktrace crash)

---

## Why this meets the rubric’s “Excellent” bar

- **Separation of concerns:** symbols in `ResourceBundle`; layout in FXML; Java only assembles.
- **Scalability:** dynamic containers handle arbitrary symbol counts cleanly.
- **Flexibility:** swapping layouts is a config change, not a code change.
- **Robustness:** safe defaults + graceful failure paths.

If you want, I can also sketch a *one-paragraph* “component diagram” (ConfigLoader → FXMLLoader/Controller → ViewModel → Service) without adding more code.
