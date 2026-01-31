# Maven Quick Cheat Sheet

A compact reference of common Apache Maven commands, lifecycles, flags, and workflows — perfect for quick lookup.

---

## Table of Contents
- Basics & Project Layout
- Frequently used commands
- Maven lifecycles
- Useful flags and properties
- Multi-module / Reactor builds
- Dependency & version tools
- Releases & publishing
- CI / performance tips
- Troubleshooting & debugging
- Helpful plugin commands

---

## Basics & Project Layout
- Project descriptor: `pom.xml`
- Standard source layout:
  - Java: `src/main/java`
  - Resources: `src/main/resources`
  - Tests: `src/test/java`, `src/test/resources`
- Coordinates: `groupId:artifactId:version[:packaging][:classifier]`
- Parent POM and inheritance allow centralizing config
- BOM (Bill of Materials) for managing versions via `<dependencyManagement>`

---

## Frequently used commands
- Build (compile -> test -> package):
  ```
  mvn package
  ```
- Clean build:
  ```
  mvn clean package
  ```
- Just compile:
  ```
  mvn compile
  ```
- Run unit tests:
  ```
  mvn test
  ```
- Skip tests during build:
  ```
  mvn -DskipTests package     # compiles and packages but skips tests
  mvn -DskipTests=true package
  ```
- Skip test compilation & execution:
  ```
  mvn -Dmaven.test.skip=true package
  ```
- Install to local repository (~/.m2/repository):
  ```
  mvn install
  ```
- Deploy to remote repository (configured in distributionManagement):
  ```
  mvn deploy
  ```
- Generate effective POM (merged POM with inheritance and profiles applied):
  ```
  mvn help:effective-pom
  ```
- Show dependency tree:
  ```
  mvn dependency:tree
  ```
- Download dependencies (resolve):
  ```
  mvn dependency:resolve
  ```
- Show plugin list:
  ```
  mvn help:all-profiles
  mvn help:effective-settings
  ```

---

## Maven lifecycles (high level)
- clean: `pre-clean`, `clean`, `post-clean`
- default (build): `validate`, `compile`, `test`, `package`, `integration-test`, `verify`, `install`, `deploy`
- site: `site`, `deploy` (site generation)
Example: `mvn verify` runs up to verification phase (incl. integration tests if configured).

---

## Useful flags and options
- `-P <profile>`: activate profile(s)
  ```
  mvn -Pci package
  ```
- `-Dproperty=value`: set system/property values
  ```
  mvn -DskipTests=true package
  ```
- `-pl` (projects list) and `-am` (also make):
  ```
  mvn -pl module-a,module-b -am package
  ```
- `-rf` (resume from):
  ```
  mvn -rf :module-b
  ```
- `-T` threaded builds (parallel):
  ```
  mvn -T 1C package   # 1 thread per CPU core
  mvn -T 4 package    # 4 threads
  ```
- `-U` force update of snapshots:
  ```
  mvn -U clean install
  ```
- `-e` show full stack trace, `-X` enable debug logging:
  ```
  mvn -e -X test
  ```

---

## Multi-module / Reactor builds
- Build entire multi-module project from parent root:
  ```
  mvn clean install
  ```
- Build a subset (and build dependencies):
  ```
  mvn -pl moduleA -am install
  ```
- Resume after a failed module:
  ```
  mvn -rf :failed-module
  ```
- Skip modules:
  ```
  mvn -pl !module-to-skip install
  ```
- Use `dependency:tree` at module level to inspect transitive deps.

---

## Dependency & version management
- Show dependency tree with verbose versions:
  ```
  mvn dependency:tree -Dverbose
  ```
- Find conflicts:
  ```
  mvn dependency:analyze
  mvn dependency:analyze-only
  ```
- Use BOM to align versions:
  ```xml
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.apache</groupId>
        <artifactId>apache-bom</artifactId>
        <version>1.2.3</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  ```
- Update plugin/dependency versions (versions-maven-plugin):
  ```
  mvn versions:display-dependency-updates
  mvn versions:use-latest-releases
  mvn versions:use-next-releases
  mvn versions:commit
  ```

---

## Releases & publishing
- Use Maven Release Plugin (recommended workflow):
  1. Prepare (tag + update versions):
     ```
     mvn release:prepare
     ```
  2. Perform (push tag, deploy):
     ```
     mvn release:perform
     ```
- For manual release steps:
  - Bump version in POM, tag in VCS, `mvn clean deploy` to push artifacts.
- Snapshot vs release:
  - `1.0.0-SNAPSHOT` gets updated; releases are fixed versions (1.0.0).
- CI should avoid releasing from local files; use credentials in CI and `mvn deploy` with configured remote repo.

---

## CI / performance tips
- Use `mvn -T` for parallel builds (careful with plugin thread-safety).
- Use a Nexus/Artifactory proxy to cache remote artifacts (faster and stable).
- Avoid `-U` on every CI build — only when you need snapshot refresh.
- Keep reproducible builds:
  - Avoid timestamps in generated artifacts, pin plugin versions, and use BOMs.
- Cache local repository (~/.m2/repository) across CI runs if possible.

---

## Troubleshooting & debugging
- Re-run with full debug:
  ```
  mvn -X <goal>
  ```
- Inspect effective POM:
  ```
  mvn help:effective-pom
  ```
- Clear local corrupt artifacts:
  - Delete a specific artifact folder from `~/.m2/repository` to force re-download.
- Check SSL/credentials issues:
  - Validate `~/.m2/settings.xml` server credentials and mirrors.
- Common errors:
  - Missing dependency: check repository and group/artifact coordinates.
  - Plugin not found: pin plugin version and check pluginRepositories/settings.
  - Inconsistent versions across modules: use parent or `<dependencyManagement>`.

---

## Helpful plugins & commands
- `maven-compiler-plugin`: set Java source/target
  ```
  mvn -Dproject.build.sourceEncoding=UTF-8 \
      -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 package
  ```
- `maven-surefire-plugin` (unit tests):
  - To run a single test:
    ```
    mvn -Dtest=MyTest test
    mvn -Dtest=MyTest#methodName test
    ```
- `maven-failsafe-plugin` (integration tests):
  ```
  mvn verify  # executes integration tests bound to integration-test/verify
  ```
- `maven-enforcer-plugin`: enforce rules (JDK, dependency convergence)
- `maven-checkstyle-plugin`, `spotbugs-maven-plugin`, `pmd-maven-plugin` for quality checks
- `versions-maven-plugin` for dependency/plugin version management
- `shade` or `assembly` plugin for creating fat/uber jars:
  ```
  mvn package assembly:single
  mvn package -DdescriptorId=jar-with-dependencies
  ```

---

## Quick workflows (one-liners)
- Full clean build + tests:
  ```
  mvn -T 1C -U clean verify
  ```
- Build only changed modules and their dependencies:
  ```
  mvn -pl :module -am install
  ```
- Build and skip tests (fast local iteration):
  ```
  mvn -DskipTests package
  ```
- Debug failing build with stack traces:
  ```
  mvn -e -X test
  ```

---

## Best practices (short)
- Always declare plugin versions in your POM (reproducibility).
- Use parent POM or company BOM for common settings and dependency versions.
- Keep CI builds deterministic: avoid local-only dependencies and snapshots when possible.
- Prefer `mvn verify` in CI if you use integration tests.
- Use `mvn dependency:tree` to resolve dependency conflicts early.
- Use settings.xml for server credentials and mirrors (never commit secrets to VCS).

---

If you'd like, I can:
- Add company/team-specific examples (settings.xml, central repo config).
- Create a printable one-page PDF or a shorter one-page cheat-sheet.
- Include a quick reference table of common lifecycle phases and their default bindings.
