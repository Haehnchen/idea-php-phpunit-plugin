# Kotlin Migration Workflow

This workflow migrates the plugin from Java to Kotlin step by step while keeping behavior identical. The goal is a mechanical code migration only: no nullable redesign, no API cleanup, no compatibility shims, and no feature changes.

## Ground Rules

- Keep every migration slice behavior-preserving.
- Add missing freeze tests before converting risky logic.
- Convert one behavior slice per commit.
- Keep `plugin.xml` updates in the same commit as the class they reference.
- Keep Gradle/Kotlin setup changes separate, and only change them if compilation proves it is required.
- Preserve current `@NotNull` and `@Nullable` contracts mechanically.
- Preserve Java-callable APIs while Java and Kotlin coexist.
- Do not turn static Java utility APIs into top-level Kotlin functions unless all callers move in the same commit.
- Do not change the performance shape of hot paths. In particular, `PhpTypeProvider4#getType(...)` must stay PSI-local and must not gain `PhpIndex`, `completeType(...)`, reference resolution, or broad `getType().getTypes()` walks.
- Preserve legacy PHPUnit names such as `PHPUnit_Framework_*` and namespaced `PHPUnit\Framework\*`.

## Agent Split

Use two agents for each migration batch.

### Agent 1: Test Freeze Agent

Owns tests only. This agent adds or adjusts fixtures before production conversion.

Responsibilities:

- Identify the exact behavior that needs freezing for the next slice.
- Add focused tests and PHP fixtures where coverage is weak.
- Run the focused test command for the slice.
- Commit only test and fixture changes.

Commit format:

```bash
git commit -m "Freeze <slice> behavior before Kotlin migration"
```

### Agent 2: Kotlin Migration Agent

Owns production conversion only after Agent 1 is green.

Responsibilities:

- Convert the approved Java files to Kotlin.
- Keep package names, public class names, constructors, nested class JVM names, annotations, and extension-point compatibility intact.
- Avoid any cleanup not required by the Java-to-Kotlin conversion.
- Run compile and focused tests for the slice.
- Commit only conversion changes and required `plugin.xml` updates.

Commit format:

```bash
git commit -m "Migrate <slice> to Kotlin"
```

## Baseline

Start with a clean baseline before any conversion work.

```bash
git status --short
PHPSTORM_ENV=skip ./gradlew compileJava compileKotlin
PHPSTORM_ENV=skip ./gradlew clean test
PHPSTORM_ENV=skip ./gradlew check test buildPlugin
```

The current Gradle setup already applies Kotlin JVM `2.4.0`. There are currently no `src/main/kotlin` or `src/test/kotlin` source directories, so create them only when the first Kotlin files are introduced.

## Slice 0: Freeze Missing Coverage

Do this before production migration starts.

Agent 1 adds focused tests for:

- `MockProphecyTypeProviderTest`: `getMockClass`, `\Prophecy\Prophet`, and legacy `PHPUnit_Framework_TestCase` variants.
- `SetUpTypeProviderTest`: negative non-test-class behavior.
- `PhpTypeProviderUtil`: class constants, field defaults, and piped signatures.
- PHPUnit mock-string behavior: `AvailabilityHelper` array/list scope, `FilterFactory` protected-property access, `ClassFinder` variable class parameters, and `PhpUnitMockStringReference` range/rename behavior.
- Mockery shared utilities: `CreateMockeryMockMethodReferencesProcessor` alias/overload/comma/generated-partial normalization and `MockeryReferencingUtil` parameter, array hash, array element, generated partial string, and concatenation scopes.
- `DeprecatedMockedMethodInspectionTest`: positive deprecated string class-name coverage.
- `TestRunIntentionActionTest`: non-test negative case.
- `RelatedTestCaseLineMarkerProviderTest`: `FakeTest` non-PHPUnit negative case.

Verification:

```bash
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.type.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.utils.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.references.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.inspection.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.intention.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.linemarker.*"
PHPSTORM_ENV=skip ./gradlew clean test
```

Commit:

```bash
git add src/test
git commit -m "Freeze migration-sensitive PHPUnit behavior"
```

## Slice 1: Small Extension Shells

Convert simple extension entry classes first.

Files:

- `src/main/java/de/espend/idea/php/phpunit/completion/PhpUnitMockStringCompletionContributor.java`
- `src/main/java/de/espend/idea/php/phpunit/completion/PhpUnitCompletionContributor.java`
- `src/main/java/de/espend/idea/php/phpunit/completion/PhpUnitMockStringCompletionConfidence.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/PhpUnitMockStringReferenceContributor.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/PhpUnitMockStringReferenceProvider.java`

Checks:

```bash
PHPSTORM_ENV=skip ./gradlew compileJava compileKotlin
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.completion.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.references.*"
```

Commit:

```bash
git add src/main
git commit -m "Migrate extension shells to Kotlin"
```

## Slice 2: PHPUnit Mock-String Core

Convert the mock-string leaf classes as one closed behavior slice.

Files:

- `src/main/java/de/espend/idea/php/phpunit/utils/mockstring/FilterConfig.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/mockstring/Filter.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/mockstring/MockBuilderFilter.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/mockstring/InvocationMockerFilter.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/mockstring/MethodMockFilter.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/mockstring/AvailabilityHelper.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/mockstring/ClassFinder.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/mockstring/FilterFactory.java`

Important constraints:

- `FilterFactory` uses reflective constructor lookup. Preserve constructor visibility and parameter shape.
- Preserve the legacy config names exactly.
- Do not merge this slice with reference or completion behavior cleanup.

Checks:

```bash
PHPSTORM_ENV=skip ./gradlew compileJava compileKotlin
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.utils.PhpUnitMockStringFilterFactoryTest"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.completion.PhpUnitMockStringCompletionContributorTest"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.references.PhpUnitMockStringReferenceContributorTest"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.renaming.PhpUnitMockStringRenameTest"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.annotator.PhpUnitMockStringAnnotatorTest"
```

Commit:

```bash
git add src/main
git commit -m "Migrate PHPUnit mock-string core to Kotlin"
```

## Slice 3: Shared PSI and Processor Utilities

Convert shared utilities before migrating feature bundles that depend on them.

Files:

- `src/main/java/de/espend/idea/php/phpunit/utils/ChainVisitorUtil.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/processor/CreateMockMethodReferenceProcessor.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/processor/CreateMockeryMockMethodReferencesProcessor.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/processor/IndexLessMethodParameterChainProcessor.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/processor/MethodReferenceNameProcessor.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/MockeryReferencingUtil.java`
- `src/main/java/de/espend/idea/php/phpunit/type/utils/ProphecyTypeUtil.java`
- `src/main/java/de/espend/idea/php/phpunit/type/utils/PhpTypeProviderUtil.java`

Important constraints:

- Keep null-return behavior identical.
- Keep Java-callable static utility shape until all callers are Kotlin.
- Do not introduce broader PSI traversal.

Checks:

```bash
PHPSTORM_ENV=skip ./gradlew compileJava compileKotlin
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.utils.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.type.*"
PHPSTORM_ENV=skip ./gradlew test --tests "*Mockery*"
```

Commit:

```bash
git add src/main
git commit -m "Migrate shared PSI utilities to Kotlin"
```

## Slice 4: Completion, Reference, And Annotator Bundles

Convert feature bundles after the core helpers are stable.

Batch A: PHPUnit mock-string feature bundle.

- `src/main/java/de/espend/idea/php/phpunit/completion/PhpUnitMockStringCompletionProvider.java`
- `src/main/java/de/espend/idea/php/phpunit/completion/PhpUnitMockStringCompletionContributor.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/PhpUnitMockStringReference.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/PhpUnitMockStringReferenceContributor.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/PhpUnitMockStringReferenceProvider.java`
- `src/main/java/de/espend/idea/php/phpunit/annotator/PhpUnitMockStringAnnotator.java`

Batch B: Mockery bundle.

- `src/main/java/de/espend/idea/php/phpunit/completion/MockeryCompletionContributor.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/MockeryReferenceContributor.java`
- `src/main/java/de/espend/idea/php/phpunit/annotator/MockeryAnnotator.java`
- `src/main/java/de/espend/idea/php/phpunit/utils/MockeryPsiRefactoringUtil.java`

Batch C: PHPUnit class/method reference bundle.

- `src/main/java/de/espend/idea/php/phpunit/reference/PhpUnitReferenceContributor.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/PhpClassMethodReference.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/PhpClassMethodReferenceForPartialMock.java`
- `src/main/java/de/espend/idea/php/phpunit/reference/PhpUnitCreatePartialMock.java`

Important constraints:

- `PhpUnitCreatePartialMock` is referenced in `plugin.xml` through nested JVM names. Kotlin nested classes must remain non-`inner` classes so the JVM names stay `PhpUnitCreatePartialMock$ReferenceContributor` and `PhpUnitCreatePartialMock$Completion`.
- Keep rename and find-usages behavior exactly as frozen by fixtures.

Checks:

```bash
PHPSTORM_ENV=skip ./gradlew compileJava compileKotlin
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.completion.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.references.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.renaming.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.annotator.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.usages.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.inspection.ReplaceLegacyMockeryInspectionTest"
```

Commit each batch separately.

## Slice 5: Type Providers

Convert type providers late because they are registered hot paths.

Batch A: PHPUnit mock provider.

- `src/main/java/de/espend/idea/php/phpunit/type/GetMockTypeProvider.java`

Batch B: Prophecy providers.

- `src/main/java/de/espend/idea/php/phpunit/type/MockProphecyTypeProvider.java`
- `src/main/java/de/espend/idea/php/phpunit/type/ProphecyTypeProvider.java`
- `src/main/java/de/espend/idea/php/phpunit/type/RevealProphecyTypeProvider.java`
- `src/main/java/de/espend/idea/php/phpunit/type/ProphecyArgumentTypeProvider.java`

Batch C: Mockery providers.

- `src/main/java/de/espend/idea/php/phpunit/type/MockeryMethodNameTypeProvider.java`
- `src/main/java/de/espend/idea/php/phpunit/type/MockeryExpectationTypeProvider.java`

Batch D: setup-assigned field provider.

- `src/main/java/de/espend/idea/php/phpunit/type/SetUpTypeProvider.java`

Important constraints:

- Keep `getType(...)` cheap and local.
- Keep heavier work in `complete(...)`, `getBySignature(...)`, or existing helper paths.
- Preserve current encoded signature format.
- Do not simplify child-type behavior in `MockeryExpectationTypeProvider`.

Checks:

```bash
PHPSTORM_ENV=skip ./gradlew compileJava compileKotlin
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.type.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.utils.*"
```

Commit each batch separately.

## Slice 6: Inspections, Intentions, Line Markers, Icons, And Error Reporter

Convert the highest side-effect surfaces last.

Files:

- `src/main/java/de/espend/idea/php/phpunit/inspection/DeprecatedMockedMethodInspection.java`
- `src/main/java/de/espend/idea/php/phpunit/inspection/ReplaceLegacyMockeryInspection.java`
- `src/main/java/de/espend/idea/php/phpunit/intention/AddMockMethodIntention.java`
- `src/main/java/de/espend/idea/php/phpunit/intention/ConstructorMockIntention.java`
- `src/main/java/de/espend/idea/php/phpunit/intention/MethodExceptionIntentionAction.java`
- `src/main/java/de/espend/idea/php/phpunit/intention/TestRunIntentionAction.java`
- `src/main/java/de/espend/idea/php/phpunit/linemarker/RelatedTestCaseLineMarkerProvider.java`
- `src/main/java/de/espend/idea/php/phpunit/PhpUnitIcons.java`
- `src/main/java/de/espend/idea/php/phpunit/ide/PluginErrorReporterSubmitter.java`

Checks:

```bash
PHPSTORM_ENV=skip ./gradlew compileJava compileKotlin
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.inspection.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.intention.*"
PHPSTORM_ENV=skip ./gradlew test --tests "de.espend.idea.php.phpunit.linemarker.*"
PHPSTORM_ENV=skip ./gradlew buildPlugin
```

Commit each batch separately.

## Final Verification

After the last slice:

```bash
PHPSTORM_ENV=skip ./gradlew compileJava compileKotlin
PHPSTORM_ENV=skip ./gradlew clean test
PHPSTORM_ENV=skip ./gradlew check test buildPlugin
```

Then run IntelliJ inspections on touched Java/Kotlin files:

1. `intellij-cli action sync_files`
2. `intellij-cli index_status`
3. `intellij-cli get_file_problems errorsOnly=true <touched files>`

If inspections produce too much output, split by slice.

## Per-Slice Review Checklist

- The old Java file is removed only when the Kotlin replacement compiles.
- The Kotlin file keeps the same package and public class name.
- Public constructors required by extension points still exist.
- Nested extension classes keep the same JVM names.
- `plugin.xml` references still point to loadable classes.
- `@JvmStatic`, `@JvmField`, or companion objects are used only when needed to preserve Java-callable shape.
- No new nullable API decisions are introduced.
- No broad project scans, index calls, or reference resolution are added to hot paths.
- Focused tests for the slice pass.
- `compileJava compileKotlin` passes.
- Commit contains exactly one behavior slice.
