# 🧪 JUnitGen CLI – AI-Powered Test Generation

Easily generate high-quality, context-aware JUnit 5 tests for Java projects using AI models like OpenAI and Gemini.

---

## 🚀 Features

- 📦 Project-wide smart scanning
- 🔁 Dependency-based test regeneration
- 🧠 AI prompt enrichment with code context
- 🔐 Safe overwrite strategies (`skip`, `warn`, `overwrite`)
- 📂 Manual file/folder trigger support (`--file=`, `--folder=`)
- 📊 CLI summary reporting
- 🖥️ Supports OpenAI GPT and Google Gemini models
- 🧪 Designed for Spring Boot and Java 17+

---

## 🛠️ Installation

### 1. Download Artifacts
- `junitgen.jar`
- `junitgen.sh` (Linux/macOS) or `junitgen.bat` (Windows)

### 2. Make Executable (Linux/macOS)
```bash
chmod +x junitgen.sh
sudo mv junitgen.sh /usr/local/bin/junitgen
```

**Windows:**
- Move `junitgen.bat` and `junitgen.jar` to a folder in your PATH
- Or you can add the folder path in which `junitgen.bat` and `junitgen.jar` is stored to the Environment Variables PATH

---

## ⚙️ Basic Usage

From your Spring Boot (or Java) project root:

### Generate tests for staged files (Git mode)

```bash
junitgen --project-path=. --model=gemini
```

### Manually generate test for a specific file

```bash
junitgen --project-path=. --file=src/main/java/com/example/service/OrderService.java
```

### Generate tests for an entire folder

```bash
junitgen --project-path=. --folder=src/main/java/com/example/service
```

---

## 🛡️ Overwrite Strategy Control

Choose what to do if test files already exist:

| Mode | Description |
|------|-------------|
| `skip` | Do not overwrite existing tests |
| `warn` (default) | Warn before overwriting |
| `overwrite` | Silently replace |

Example:
```bash
junitgen --project-path=. --test-overwrite=warn
```

---

## 📄 Available CLI Flags

| Flag | Description |
|------|-------------|
| `--project-path=` | Root path of the project |
| `--file=` | Single `.java` file to generate test for |
| `--folder=` | Folder containing `.java` files |
| `--model=` | AI model to use (`gemini`, `openai`) |
| `--dry-run` | Print generated tests without saving |
| `--debug-prompt` | Print AI prompt for inspection |
| `--test-overwrite=` | Control behavior if test already exists |

---

## ⚡ Requirements

- Java 17+
- Maven installed (for building locally)
- Internet access (for AI model calls)
- API Key for OpenAI or Gemini models
- Set API Keys as `OPENAI_API_KEY`,`GEMINI_API_KEY` in Environment Variables.

---

## 👨‍💻 Contributing

Coming soon!

---

## 🛠️ Roadmap

- Smart test merging (basic)
- Prompt few-shot example enhancement
- Full `.junitgenrc` config support
- Possible Gradle plugin version