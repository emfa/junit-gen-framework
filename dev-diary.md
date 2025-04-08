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
