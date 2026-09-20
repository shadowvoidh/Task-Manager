**[PT-BR 🇧🇷 ]** |   **[[ENG 🇺🇸 ]](SECURITY.en.md)**

# 🔒 Política de Segurança — Task Manager (Java Swing)

Este documento descreve o modelo de segurança do **Task Manager**, como
reportar vulnerabilidades e boas práticas recomendadas para quem for rodar
ou estender o projeto.

---

## 📦 Versões Suportadas

O projeto é distribuído como código-fonte Java, sem binário/instalador
oficial publicado no momento. Recomenda-se sempre usar a versão mais
recente da branch `main`.

| Componente | Versão de referência | Suportado |
| ---------- | --------------------- | :-------: |
| Java UI    | JDK 17+                | ✅        |
| Java UI    | JDK < 17               | ❌        |

---

## 🧭 Modelo de Ameaça

O **Task Manager** é uma aplicação **desktop 100% local e offline**, sem
qualquer componente de rede ou servidor. Isso reduz significativamente a
superfície de ataque em comparação com aplicações cliente-servidor. Pontos
relevantes do modelo de segurança atual:

* **Sem rede** — a aplicação não abre portas, não faz requisições HTTP e não
  se comunica com nenhum serviço externo. Todo o processamento acontece na
  máquina do usuário.
* **Persistência em texto plano (`tasks.txt`)** — as tarefas são salvas sem
  criptografia, no diretório de execução do programa. **Não** armazene
  informações sensíveis (senhas, dados pessoais confidenciais, etc.) nas
  descrições das tarefas, pois qualquer pessoa com acesso ao arquivo pode
  lê-las em texto puro.
* **Sem controle de acesso** — como é uma aplicação single-user local, não
  há autenticação nem separação de permissões. A proteção do arquivo
  `tasks.txt` depende inteiramente das permissões do sistema operacional do
  usuário.
* **Parsing defensivo do arquivo de dados** — linhas corrompidas ou em
  formato inesperado em `tasks.txt` são ignoradas ao carregar, em vez de
  travar a aplicação, reduzindo o risco de um arquivo malformado (ou editado
  manualmente de forma maliciosa) causar comportamento inesperado.
* **Sem dependências externas em runtime** — a aplicação roda apenas com o
  JDK; o Maven é usado somente em tempo de build/teste, reduzindo a
  superfície de dependências de terceiros que chegam ao usuário final.

---

## 🚨 Riscos Conhecidos e Mitigações

| Risco                                                             | Mitigação atual / recomendada                                                                                       |
| -------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| Leitura do `tasks.txt` por outro usuário/processo no mesmo sistema | Ajuste as permissões do arquivo/diretório no SO; evite salvar dados sensíveis nas descrições.                        |
| Corrupção manual do arquivo `tasks.txt`                            | O parser ignora linhas inválidas/corrompidas silenciosamente ao carregar, sem interromper a aplicação.               |
| Caractere delimitador (`\|`) presente na descrição da tarefa colidindo com o formato do arquivo | O caractere `\|` na descrição é substituído por `/` antes de salvar, evitando quebra do parser. |
| Dependências de teste desatualizadas (JUnit 5 via Maven)           | Rode `mvn versions:display-dependency-updates` periodicamente e mantenha o `pom.xml` atualizado.                     |
| Perda de dados por falha ao salvar (disco cheio, permissão negada) | A camada `FileTaskRepository`/`TaskService` deve tratar exceções de I/O e alertar o usuário via `JOptionPane` em vez de falhar silenciosamente. |

---

## 🛡️ Boas Práticas para Quem for Estender o Projeto

1. **Não** armazene dados sensíveis (senhas, tokens, dados pessoais
   confidenciais) como texto de tarefas — o arquivo `tasks.txt` não é
   criptografado.
2. Se for adicionar persistência em banco de dados (ex: SQLite) ou formato
   JSON, mantenha a separação de camadas (`repository` isolado atrás de uma
   interface), como já é feito hoje, para facilitar auditoria e testes.
3. Ao aceitar entrada do usuário (descrição da tarefa), sanitize/trate
   caracteres especiais antes de persistir, especialmente se migrar para um
   formato mais estruturado (CSV, JSON) — hoje o `|` já é tratado, mas
   novos delimitadores exigirão o mesmo cuidado.
4. Rode `mvn dependency-check:check` (OWASP Dependency-Check) ou equivalente
   antes de adicionar novas dependências ao `pom.xml`, especialmente se o
   projeto crescer e passar a incluir bibliotecas de terceiros em runtime.
5. Se o projeto evoluir para suportar múltiplos usuários ou sincronização em
   nuvem, reavalie totalmente este documento — o modelo de ameaça atual
   assume uso local e single-user.

---

## 📣 Como Reportar uma Vulnerabilidade

Se você encontrar uma vulnerabilidade de segurança neste projeto, **não abra
uma issue pública**. Em vez disso:

1. Envie um e-mail para **shadow.voidh@gmail.com** com:
   - descrição da vulnerabilidade;
   - passos para reproduzir (se possível);
   - impacto potencial;
   - sugestão de correção (opcional).
2. Você pode esperar uma confirmação de recebimento em até **5 dias úteis**.
3. Pedimos, por gentileza, um prazo razoável para correção antes de qualquer
   divulgação pública (*responsible disclosure*).

Relatos feitos de boa-fé são muito bem-vindos e apreciados.

---

## 📬 Contato

* *GitHub:* [@shadowvoidh](https://github.com/shadowvoidh)
* *E-mail:* shadow.voidh@gmail.com
* *LinkedIn:* [Pedro Carnio](https://linkedin.com/in/pedrocarnio)
* *Discord:* shadow_voidh
