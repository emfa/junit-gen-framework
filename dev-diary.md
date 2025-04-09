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
