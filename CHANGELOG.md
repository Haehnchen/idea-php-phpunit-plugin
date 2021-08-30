# Changelog

## Versions
* 5.x: PhpStorm 2020.2+
* 4.x: PhpStorm 2020.1+ (no support)
* 3.x: PhpStorm 2017.3+ (no support)
* 2.x: PhpStorm 2017.1+ (no support)
* 1.x: PhpStorm 2016.1+ (no support)

## 6.2
* fix "ReplaceLegacyMockeryInspection" short naming (Daniel Espendiller)

## 6.1
* Removed incorrect short name for Mockery Inspection (Joseph Lawson)

## 6.0
* migrate to github action (Daniel Espendiller)
* Updated README detailing Mockery support. Updated plugin.xml to mention new Mockery support features in feature list (Joseph Lawson)
* Added inspections for highlighting and then replacing legacy Mockery syntax with Mockery 1.0.0 methods. Can be configured to Mockery 1.0.0 style of choice. (Joseph Lawson)
* Added type provider to give correct types to Mockery methods that use the new Mockery 1.0.0 syntax e.g. $mock->allows()->foo(). (Joseph Lawson)
* Added annotator for highlighting incorrectly mocked methods in Mockery (Joseph Lawson)
* Added autocompletion for method string in Mockery methods (Joseph Lawson)
* Added tests for rename, refactoring and find usages of mocked methods (Joseph Lawson)
* Added reference contributor for Mockery. Provides references from method strings in Mockery method to their declaration (Joseph Lawson)
* Changed test directory structure to match main Changed CHAR from 元 to its unicode \u5143 (Joseph Lawson)

## 5.1
* Fix folder indention for "AddMethodIntention" (Daniel Espendiller)

## 5.0
* Migrate type provide to "typeProvider4" implementation (Daniel Espendiller)
* Fix intention files [#55](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/55) (Daniel Espendiller)

## 4.1
* Provide a line marker to navigate to a related test classes, based on the naming [#16](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/16) (Daniel Espendiller)
* Supporting type resolving in closures [#38](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/38) (Daniel Espendiller)
* Support Argument::any (Prophecy\\Argument\\Token\\TokenInterface) type resolving based on prophecy class context [#12](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/12) [#29](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/29) (Daniel Espendiller)
* Provide type resolving for \\Prophecy\\Argument::type argument [#29](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/29) (Daniel Espendiller)

## 4.0
* Allow all PhpUnit mock instance in chaining for finding createMock [#39](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/39) (Daniel Espendiller)
* Provide support for "PHPUnit\\Framework\\MockObject\\Stub::method" [#42](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/42) [#39](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/39) (Daniel Espendiller)
* Dropping feature support for build < 2020.1; api is still valid so by now no build-since highering needed (Daniel Espendiller)
* Support also Behat tests file structure (Daniel Espendiller)
* Fix isPrimitiveType checking for constructor creation intention (Daniel Espendiller)
* Remove deprecated code (Daniel Espendiller)
* Remove runner linemarker in favor of already provide by PhpStorm (Daniel Espendiller)
* Add plugin icon (Daniel Espendiller)
* Allow "setUpBeforeTest" to be a valid "setUp" method for checking property types #35 (Daniel Espendiller)
* Allow some more use cases for checking test context eg for Behat [#37](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/37) (Daniel Espendiller)
* Add support for phpspec/prophecy-phpunit where "prophesize" method is provided as a trait [#46](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/46) (Daniel Espendiller)
* Move to gradle build (Daniel Espendiller)
* Fix for build on travis ([#45](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/45)) (Roman Tymoshyk)
* Add missing namespaces PhpUnit mock instances (Daniel Espendiller)

## 3.8
* Fix performance issue: migrate test linemarker runner to leaf elements

## 3.7
* Handle deletion of PhpThrownExceptionsAnalyzer#getExceptionClasses in PhpStorm 2017.3 [#28](https://github.com/Haehnchen/idea-php-phpunit-plugin/pull/28)

## 2.7
* Provide expectedException generator for given method scope [#27](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/27)
    
## 2.6
* Support parent construct methods in "Add constructor mocks"
* Support newExpression without parameter list for "Add constructor mocks"

## 2.5
* Add intention to create mocked constructor parameters
* Extend scope for method reference chaining detection in method mock generator
* Fix scope detection for test runner
* Constructor intention must allow variable declaration scope

## 2.4
* Add mock method intention / generator [#5](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/5)

## 2.3
* Pipe property declaration of setUp test method [#11](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/11) [#13](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/13) [#14](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/14) [#17](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/17)
* Migrate createMock, getMock and reveal types to chaining method detection [#11](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/11) [#13](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/13) [#14](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/14) [#17](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/17)
* Optimize completion for createMock to use chaining detection

## 2.2.1
* Add MockBuilder->getMock TypeProvider [#10](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/10)

## 2.2
* Reimplement magic Prophecy method [#8](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/8)
* Add Prophecy reveal type provider [#9](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/9)

## 2.1
* Add custom linemarker runner for units test method and classes and provide intention [#4](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/4)
* Reimplement TypeProvider in the correct way without index access [#6](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/6)

## 2.0
* Initial release fork of [maxfilatov/phpuaca](https://github.com/maxfilatov/phpuaca/)
* Support class constants in createMock method completion

## 1.3.5
* Type providers for PHPUnit and Prophecy has been temporary removed for plugin stability reasons, will be fixed soon;

## 1.3.3
* support for Prophecy property, argument and return type completion (by [Steve Müller](https://github.com/deeky666));
* multiple fixes to prevent IDE freezes during updating indices process;
* support for latest IDE versions (by [Sebastian Hopfe](https://github.com/shopfe));

## 1.3
* code navigation (go to declaration, find usages, etc.) and refactoring (rename methods);
* highlighting of incorrect method usages;
* PHPUnit type provider;
* Prophecy type provider;
* legacy tests support: method completion for mocks defined by string class name instead of class constant reference, <tt>::class</tt> (by [Thomas Schulz](https://github.com/King2500)).

### 1.2
* Fixed false activation of plugin in classes with magic methods defined in PHPDoc blocks.

## 1.1
* Added support for methods <tt>PHPUnit_Framework_TestCase::getMock</tt>, <tt>PHPUnit_Framework_TestCase::getMockClass</tt>, <tt>PHPUnit_Framework_TestCase::getMockForAbstractClass</tt> and <tt>PHPUnit_Framework_TestCase::getMockForTrait</tt>;
* Already specified methods in mock definitions aren't used in next autocomplete suggestions;
* Fixed multiple false activations of suggestion popup.

## 1.0
* Added support for methods <tt>PHPUnit_Framework_MockObject_MockBuilder::setMethods</tt> and <tt>PHPUnit_Framework_MockObject_Builder_InvocationMocker::method</tt>.