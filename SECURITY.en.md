**[PT-BR 🇧🇷 ](SECURITY.md)** |   **[ENG 🇺🇸 ]**

# 🔒 Security Policy — Task Manager (Java Swing)

This document describes the security model of the **Task Manager**, how to
report vulnerabilities, and recommended best practices for anyone running
or extending the project.

---

## 📦 Supported Versions

The project is distributed as Java source code, with no official
binary/installer currently published. Always use the latest version of the
`main` branch.

| Component | Reference version | Supported |
| --------- | -------------------- | :-------: |
| Java UI   | JDK 17+               | ✅        |
| Java UI   | JDK < 17              | ❌        |

---

## 🧭 Threat Model

The **Task Manager** is a **fully local, offline desktop application**,
with no network component or server. This significantly reduces the attack
surface compared to client-server applications. Relevant points of the
current security model:

* **No network** — the application does not open any ports, make HTTP
  requests, or communicate with any external service. All processing
  happens on the user's machine.
* **Plain-text persistence (`tasks.txt`)** — tasks are saved without
  encryption, in the program's execution directory. **Do not** store
  sensitive information (passwords, confidential personal data, etc.) in
  task descriptions, since anyone with access to the file can read it in
  plain text.
* **No access control** — since this is a local, single-user application,
  there is no authentication or permission separation. Protection of the
  `tasks.txt` file depends entirely on the user's operating system
  permissions.
* **Defensive data file parsing** — corrupted or unexpectedly formatted
  lines in `tasks.txt` are ignored on load instead of crashing the
  application, reducing the risk that a malformed (or maliciously edited)
  file causes unexpected behavior.
* **No external runtime dependencies** — the application runs on the JDK
  alone; Maven is used only at build/test time, reducing the surface of
  third-party dependencies that reach the end user.

---

## 🚨 Known Risks and Mitigations

| Risk                                                                | Current / recommended mitigation                                                                                     |
| ---------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| `tasks.txt` read by another user/process on the same system          | Adjust file/directory permissions at the OS level; avoid saving sensitive data in descriptions.                       |
| Manual corruption of the `tasks.txt` file                              | The parser silently ignores invalid/corrupted lines on load, without interrupting the application.                    |
| Delimiter character (`\|`) present in a task description colliding with the file format | The `\|` character in descriptions is replaced with `/` before saving, preventing parser breakage.                    |
| Outdated test dependencies (JUnit 5 via Maven)                         | Periodically run `mvn versions:display-dependency-updates` and keep `pom.xml` up to date.                              |
| Data loss due to a save failure (disk full, permission denied)         | The `FileTaskRepository`/`TaskService` layer should handle I/O exceptions and alert the user via `JOptionPane` instead of failing silently. |

---

## 🛡️ Best Practices for Extending the Project

1. **Do not** store sensitive data (passwords, tokens, confidential
   personal data) as task text — the `tasks.txt` file is not encrypted.
2. If you add database persistence (e.g., SQLite) or a JSON format, keep
   the layer separation (`repository` isolated behind an interface), as is
   already done today, to make auditing and testing easier.
3. When accepting user input (task description), sanitize/handle special
   characters before persisting, especially if migrating to a more
   structured format (CSV, JSON) — the `|` character is already handled
   today, but new delimiters will require the same care.
4. Run `mvn dependency-check:check` (OWASP Dependency-Check) or an
   equivalent tool before adding new dependencies to `pom.xml`, especially
   if the project grows to include third-party runtime libraries.
5. If the project evolves to support multiple users or cloud sync,
   completely reassess this document — the current threat model assumes
   local, single-user usage.

---

## 📣 How to Report a Vulnerability

If you find a security vulnerability in this project, **do not open a
public issue**. Instead:

1. Send an e-mail to **shadow.voidh@gmail.com** with:
   - a description of the vulnerability;
   - steps to reproduce it (if possible);
   - potential impact;
   - a suggested fix (optional).
2. You can expect an acknowledgment within **5 business days**.
3. We kindly ask for a reasonable amount of time to fix the issue before
   any public disclosure (*responsible disclosure*).

Good-faith reports are very welcome and appreciated.

---

## 📬 Contact

* *GitHub:* [@shadowvoidh](https://github.com/shadowvoidh)
* *E-mail:* shadow.voidh@gmail.com
* *LinkedIn:* [Pedro Carnio](https://linkedin.com/in/pedrocarnio)
* *Discord:* shadow_voidh
