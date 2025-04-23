## 🗓️ April 8, 2025

### ✅ What was done:

- Project initialized using Maven with Java 17 and necessary dependencies (`jgit`, `picocli`, `lombok`)
- `Main.java` and `JUnitGenCLI.java` created to support CLI execution
- Implemented `GitDiffService` to detect Git **staged** changes using JGit
- Fixed multiple issues:
    - Maven `classpath` error on Windows (resolved by fixing env variables)
    - JAR signature exception (resolved by excluding `META-INF/*.SF`, `.DSA`, `.RSA`)
    - JGit `IncorrectObjectTypeException` during manual tree diff parsing
- CLI now successfully filters and displays only staged `.java` files

### 🐞 Problems faced:

- Maven command line not working due to environment misconfiguration
- JAR not executing due to invalid signature digest
- JGit tree mismatch bug when parsing diffs manually

### 🛠️ How they were resolved:

- Environment variables (`JAVA_HOME`, `PATH`) adjusted to point to correct Java/Maven
- Added Maven Shade plugin filters to exclude conflicting signature files
- Rewrote diff logic using `.setCached(true)` to correctly capture staged file changes

### 📈 Outcome:

- Functional CLI tool that detects and logs `.java` file changes in staging area
- All foundational scaffolding complete and stable

### 🔜 Next Step:

- Parse staged `.java` files to extract class + method info
- Begin generating structured JUnit test stubs

### -----------------------------------------------------------------------------------------------------------------------------

## 🗓️ April 9, 2025

### ✅ What was done:

- Created and implemented `JavaSourceParser` to extract:
    - Package name
    - Class name
    - Public method names
- Successfully parsed a sample Java file and validated output
- Clarified purpose of parsing:
    - Parser provides metadata
    - Full `.java` file is still passed to AI for context
- Implemented `MockAIService` to simulate AI-generated test class creation
- Created `TestFileWriter` to:
    - Write test class to appropriate `src/test/java/<package>` path
    - Automatically create folders if missing
- Fully wired the flow in CLI:
    - Detect `.java` file
    - Parse → Mock AI → Write test
- Ran a complete end-to-end test:
    - Staged a `.java` file
    - CLI detected it
    - Parser extracted info
    - Mock AI generated a JUnit test class
    - Test file saved correctly in the project

### 🐞 Problems faced:

- None — smooth sailing 🚤

### 🧠 Learnings:

- Full `.java` file must be sent to AI to generate meaningful tests, even if only a portion was modified
- Parser is only for tool-side logic (naming, location, etc.), not for test generation logic

### 📈 Outcome:

- MVP of the AI test generator working end-to-end 🎉
- CLI tool now generates and saves a basic test class from a staged `.java` file

### 🔜 Next Step:

- Generate one `@Test` method for each public method
- Improve CLI summaries and UX
- Explore AI integration or prompt refinement

### -----------------------------------------------------------------------------------------------------------------------------

## 🗓️ April 10, 2025

### ✅ What was done:

- Enhanced `MockAIService` to:
    - Generate one `@Test` stub per public method
    - Ignore `main()` method (not unit-testable)
    - Use more expressive test method names (`method_shouldBehaveAsExpected`)
- Introduced `AIInput` model:
    - Encapsulates package name, class name, source code, and public methods
    - Prepares the system for integration with real AI test generation
- Refactored `MockAIService` into a clean interface-driven architecture:
    - Created `AITestGenerator` interface
    - Implemented `MockTestGenerator` as a first concrete class
    - CLI now uses the interface — making the AI engine pluggable

### 🐞 Problems faced:

- None — feature flow was smooth

### 📈 Outcome:

- Core AI generation flow now has proper interfaces and separation of concerns
- Ready to plug in OpenAI, Claude, or local LLMs without touching the core logic

### 🔜 Next Step:

- Simulate or wire up a real AI-based test generation module
- Add test project config (`.junitgenrc`, etc.) to customize AI prompt behavior

### -----------------------------------------------------------------------------------------------------------------------------

## 🗓️ April 16, 2025

### ✅ What was done:

- Integrated real AI model support into the test generation flow
- Implemented `OpenAITestGenerator` with Chat Completions API
- Handled OpenAI quota error gracefully and logged it in CLI
- Switched to **Google Gemini (AI Studio)** for API-based generation:
    - Created `GeminiTestGenerator` implementing `AITestGenerator`
    - Reused `OpenAIPromptBuilder` to maintain consistent prompts
    - Parsed Gemini response using robust JSON checks
- Successfully ran the CLI → staged `.java` → Gemini generated a full test class:
    - Multiple test cases
    - `@BeforeEach` setup
    - Smart assertions and meaningful structure

### 🐞 Problems faced:

- OpenAI quota exhausted → Switched to Gemini (free + fast)
- Initial Gemini response parsing missed JSON structure → Fixed with raw response debug

### 📈 Outcome:

- MVP now has **real AI test generation working** from staged code
- Test output is robust, readable, and actually useful
- Gemini fully plugged into system and functional

### 🔜 Next Step:

- Add CLI support to switch models (Gemini/OpenAI)
- Add dry-run preview option
- Enhance prompt structure or naming logic

### -----------------------------------------------------------------------------------------------------------------------------

## 🗓️ April 17, 2025

### ✅ What was done:

#### ✔️ CLI Framework Enhancements

- Added `--model` flag to toggle between OpenAI & Gemini
- Implemented `--dry-run` flag for preview-only mode
- Created `.junitgenrc` project config support with fallback defaults
- Improved debug logging for config loading and CLI behavior

#### ✔️ AI Prompt & Output Enhancements

- Rewrote prompt with clear guidelines for quality test generation
- Added Mockito support in AI prompting
- Improved consistency, structure, and test realism

#### ✔️ Review & Milestone

- Reflected on original PRD and validated all major goals are met
- Backlogged future enhancements (prompt tuning, test merging, etc.)
- Created and pushed first Git tag: `v0.1`

### 📌 Backlogged Enhancements

- Better test method naming (E)
- Output auto-formatting (F)
- `--debug-prompt` CLI flag (G)
- Prompt few-shot examples
- Test merging (append vs overwrite)
- Method-level Git diff detection
- CLI usage stats (files processed, etc.)
- Auto-generated README/help screen

### 🏁 Milestone:

**v0.1 – MVP complete. Project is usable, extensible, and AI-powered.**

### -----------------------------------------------------------------------------------------------------------------------------

## 🗓️ April 22, 2025

### ✅ What was done:

#### ✔️ Tool Enhancements
- Implemented **smarter test method naming** in `MockTestGenerator`
- Added **`TestOutputFormatter`** to clean up AI output:
  - Removed markdown, smart quotes, blank lines
- Added `--debug-prompt` CLI flag to preview AI prompts before sending
- Improved AI prompt quality with:
  - Naming conventions (`shouldX_whenY`)
  - Mockito usage
  - Cleaner, structured prompt for better AI output

#### ✔️ Testing & Analysis
- Successfully tested:
  - Multiple staged file processing
  - Partial file changes
  - Staged files with interdependencies
- Confirmed current system:
  - ✅ Handles multiple files individually
  - ⚠️ Does not yet support cross-file dependency injection
- Validated that AI hallucination happens when it lacks context (e.g., incorrect constructors)

#### 💡 Key Insight:
> The AI will only generate correct tests when it's aware of dependencies.
> Therefore, a full project-wide scan + context caching mechanism is required to enable intelligent, accurate generation.

---

### 📌 Strategic Feature Planned: **Code Sync-Up**

#### ✅ Core Capabilities:
- Parses all `.java` files in the project
- Builds a cached index of:
  - Classes, methods, fields
  - File dependencies (who uses what)
- Injects referenced classes automatically into AI prompts
- Supports manual trigger (`ai-junit-gen scan`)
- Will power smart re-generation of tests for files indirectly impacted by changes

---

### 📌 Additional Insight:
> “When one file changes, test generation should extend to all files that depend on it — not just the file itself.”

This is now a core requirement for the tool moving forward (v0.2+)

### -----------------------------------------------------------------------------------------------------------------------------