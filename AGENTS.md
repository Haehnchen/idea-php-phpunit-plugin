# AGENTS.md

## Project Overview

This is the **PHPUnit Enhancement** plugin for IntelliJ IDEA/PhpStorm. It provides IDE assistance around PHPUnit, Prophecy, and Mockery test code: completion, references/navigation, rename/find-usages support, inspections, intentions, type providers, and related test-case line markers.

**Plugin ID:** `de.espend.idea.php.phpunit`
**Plugin Name:** `PHPUnit Enhancement`
**Key Dependency:** `com.jetbrains.php`

## Build and Development Commands

### Building the Plugin

```bash
./gradlew clean buildPlugin
```

The distributable ZIP artifact is written to `build/distributions/`.

### Running Tests

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "de.espend.idea.php.phpunit.type.GetMockTypeProviderTest"

# Run tests matching a pattern
./gradlew test --tests "*Mockery*"
```

## High-Level Architecture

### Main Package Organization

The main plugin code lives under `src/main/java/de/espend/idea/php/phpunit/`:

- `type/` - `PhpTypeProvider4` implementations for PHPUnit mock creation, Prophecy, Mockery, and `setUp()`-assigned fields.
- `completion/` - PHPUnit and Mockery completion contributors.
- `reference/` - PHPUnit/Mockery reference contributors, including `createPartialMock()` method-name references.
- `inspection/` - Mockery legacy-syntax and deprecated mocked-method inspections.
- `intention/` - PHPUnit intentions for running tests, adding mock methods, constructor mocks, and expected exceptions.
- `annotator/` - Mockery method validation.
- `linemarker/` - related test-case line markers.
- `utils/` and `utils/processor/` - shared PSI/type helper logic for mock creation and chained method calls.
- `ide/` - plugin error reporter integration.

The inherited fork code lives under `src/main/java/com/phpuaca/` and still owns parts of string completion, references, annotators, filtering, and helper utilities. Keep changes sympathetic to that older structure unless the task explicitly asks for a migration.

### Plugin Registration

`src/main/resources/META-INF/plugin.xml` registers:

- PHP `typeProvider4` extensions for PHPUnit, Prophecy, and Mockery.
- PHP annotators and completion contributors.
- PHP reference contributors.
- The related test-case line marker provider.
- PHPUnit intentions and local inspections.

When adding or moving an IDE feature, update `plugin.xml` and add a focused fixture test for the registered surface.

## Testing Patterns

Use `PhpUnitLightCodeInsightFixtureTestCase` as the base class for light fixture tests. It provides helpers for:

- Completion: `assertCompletionContains(...)`
- References/navigation: `assertPhpReferenceResolveTo(...)`, `assertPhpReferenceNotResolveTo(...)`, `assertReferencesMatch(...)`
- Inspections: `assertLocalInspectionContains(...)`
- Intentions: `assertIntentionIsAvailable(...)`
- Line markers: `assertLineMarker(...)`
- Type assertions: `assertMethodContainsTypes(...)`

Fixture files live beside the test package in `src/test/java/de/espend/idea/php/phpunit/**/fixtures`. Use `myFixture.copyFileToProject(...)` for reusable PHP class stubs and `myFixture.configureByText(...)` for small inline scenarios.

Light tests use in-memory VFS paths, so code under test must not assume real filesystem paths unless the test explicitly copies fixtures into the project.

## Important Development Notes

- **Performance:** Prefer PSI-local checks, indexes, and cached values for expensive work. Do not iterate the whole project file tree from contributors, annotators, inspections, line markers, or type providers.
- **Index-Safe Hot Paths:** `PhpTypeProvider4#getType(...)` must stay cheap and local. Do not add `PhpIndex` queries, `completeType(...)`, reference resolution, or broad `getType().getTypes()` walks there; defer heavier work to completion/resolve paths or `complete(...)` where the provider contract allows it.
- **PSI Safety:** Follow IntelliJ threading rules: read actions for PSI access and write actions for document/PSI mutations. Most plugin logic should be read-only.
- **PHPUnit Compatibility:** Preserve both legacy `PHPUnit_Framework_*` names and namespaced `PHPUnit\Framework\*` names when touching mock/test-case behavior.
- **Mock Framework Coverage:** PHPUnit, Prophecy, and Mockery behavior often share call-chain assumptions. When changing one mock path, check adjacent completion, reference, rename/find-usages, and inspection tests.
- **Kotlin:** The current production code is Java. If Kotlin is introduced for IDE extensions, companion objects may only contain a logger and constants.

## Decompiler Tools

For analyzing bundled JetBrains plugins such as PHP, use **vineflower** rather than IntelliJ Fernflower output.

**vineflower**

- **GitHub:** https://github.com/Vineflower/vineflower
- **Download:** https://repo1.maven.org/maven2/org/vineflower/vineflower/1.11.2/vineflower-1.11.2.jar
- **Recommended local copy:** `decompiled/vineflower.jar`
- **Usage:** `java -jar decompiled/vineflower.jar input.jar output/`

**Bundled Plugin JARs**

- **Location:** `~/.gradle/caches/[gradle-version]/transforms/*/transformed/com.jetbrains.[plugin]-[intellij-version]/[plugin]/lib/[plugin].jar`
- **Example:** `~/.gradle/caches/9.5.1/transforms/*/transformed/com.jetbrains.php-253.*/php/lib/php.jar`
