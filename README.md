# 📋 Gerenciador de Tarefas — Java Swing

Aplicação desktop de gerenciamento de tarefas (**Task Manager**), desenvolvida em **Java puro + Swing**, com persistência local em arquivo texto (`tasks.txt`) e arquitetura em camadas (Model / Repository / Service / View).

Projeto pensado para portfólio: código organizado, comentado, testável e sem dependências externas em tempo de execução (apenas JDK).

---

## ✨ Funcionalidades

- Adicionar novas tarefas via campo de texto (ou tecla **Enter**).
- Listar tarefas em uma tabela (`JTable`) com colunas **Status**, **ID**, **Descrição** e **Data de Criação**.
- Concluir a tarefa selecionada.
- Excluir a tarefa selecionada (com diálogo de confirmação).
- Validações e alertas via `JOptionPane` (descrição vazia, nenhuma tarefa selecionada, tarefa já concluída, confirmação de exclusão, erro ao salvar).
- Persistência automática:
  - **Carrega** as tarefas do arquivo `tasks.txt` ao abrir a janela.
  - **Salva** a cada alteração (adicionar/concluir/excluir) e também ao fechar a janela.

---

## 🗂️ Estrutura do projeto

```
task-manager/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    ├── main/java/com/taskmanager/
    │   ├── Main.java                          # Ponto de entrada (SwingUtilities.invokeLater)
    │   ├── model/
    │   │   └── Task.java                       # Entidade Task
    │   ├── repository/
    │   │   ├── TaskRepository.java             # Contrato de persistência
    │   │   └── FileTaskRepository.java         # Persistência via java.nio.file (Files/Paths)
    │   ├── service/
    │   │   ├── TaskService.java                # Regras de negócio (Java Collections API)
    │   │   └── TaskNotFoundException.java      # Exceção de domínio
    │   └── view/
    │       ├── MainFrame.java                  # Janela principal (JFrame) + componentes Swing
    │       └── TaskTableModel.java             # TableModel customizado para o JTable
    └── test/java/com/taskmanager/service/
        └── TaskServiceTest.java                # Testes unitários (JUnit 5) da camada de serviço
```

### Por que essa organização?

| Pacote        | Responsabilidade                                                                 |
|---------------|-----------------------------------------------------------------------------------|
| `model`       | Entidade de domínio (`Task`), sem lógica de negócio ou de infraestrutura.        |
| `repository`  | Isola a persistência (arquivo texto) atrás de uma interface (`TaskRepository`), permitindo trocar a implementação ou usar dublês (fakes) em testes. |
| `service`     | Concentra as regras de negócio (validações, geração de ID, ordenação, contadores) e orquestra o repositório. É a camada testada com JUnit. |
| `view`        | Componentes Swing. Não conhece o repositório diretamente — depende apenas de `TaskService`. |

---

## ✅ Pré-requisitos

- **JDK 17** ou superior instalado e configurado no `PATH`.
- **Apache Maven 3.8+** (opcional, mas recomendado — usado para build, testes e empacotamento).

Verifique com:

```bash
java -version
javac -version
mvn -version
```

---

## ▶️ Como compilar e executar

### Opção A — Usando Maven (recomendado)

**Linux / macOS (bash):**

```bash
# Na raiz do projeto (onde está o pom.xml)
mvn clean package

# Executa a interface gráfica a partir do JAR gerado
java -jar target/task-manager.jar
```

**Windows (PowerShell):**

```powershell
# Na raiz do projeto (onde está o pom.xml)
mvn clean package

# Executa a interface gráfica a partir do JAR gerado
java -jar target\task-manager.jar
```

Para rodar apenas os testes unitários:

```bash
mvn test
```

> O arquivo `tasks.txt` é criado automaticamente no diretório de onde o comando `java -jar ...` é executado (normalmente a raiz do projeto).

---

### Opção B — Sem Maven (usando apenas `javac`/`java`)

**Linux / macOS (bash):**

```bash
# Na raiz do projeto
mkdir -p out

# Compila todas as classes de produção
javac -d out -encoding UTF-8 $(find src/main -name "*.java")

# Executa a interface gráfica
java -cp out com.taskmanager.Main
```

**Windows (PowerShell):**

```powershell
# Na raiz do projeto
New-Item -ItemType Directory -Force -Path out | Out-Null

# Compila todas as classes de produção
Get-ChildItem -Recurse -Filter *.java src\main | ForEach-Object { $_.FullName } | Out-File sources.txt
javac -d out -encoding UTF-8 "@sources.txt"

# Executa a interface gráfica
java -cp out com.taskmanager.Main
```



## 💾 Formato do arquivo `tasks.txt`

Cada linha representa uma tarefa, no formato:

```
id|completed|createdAt|descrição
```

Exemplo:

```
1|false|2026-09-07T14:32:10.123456|Estudar Java Swing
2|true|2026-09-07T14:35:02.987654|Revisar Pull Request
```

> Por simplicidade, o caractere `|` presente na descrição é substituído por `/` antes de salvar, evitando ambiguidade no parser. Linhas corrompidas ou em formato inesperado são ignoradas ao carregar, sem interromper a aplicação.

---

## 🚀 Possíveis evoluções

- Editar a descrição de uma tarefa existente.
- Filtro/busca por status ou texto na tabela.
- Persistência em SQLite/JSON.
- Internacionalização (i18n).
- Empacotamento nativo com `jpackage` (instalador `.exe`/`.dmg`/`.deb`).

---

## 📄 Licença

Projeto de portfólio livre para estudo e reutilização.




###  🌘Autor
* *Shadow_Voidh:* [Github](https://github.com/shadowvoidh)

###  📬 Contato
* *GitHub:* [@shadowvoidh](https://github.com/shadowvoidh)
* *Instagram:* [@shadow_voidh](https://www.instagram.com/shadow_voidh/)
* *LinkedIn:* [Pedro Carnio](https://linkedin.com/in/pedrocarnio)
* *Discord:* shadow_voidh
* *E-mail:* shadow.voidh@gmail.com
