# IntelliJ IDEA / PhpStorm PHPUnit Enhancement

[![Build Status](https://github.com/Haehnchen/idea-php-phpunit-plugin/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/Haehnchen/idea-php-phpunit-plugin/actions/workflows/gradle.yml)
[![Version](http://phpstorm.espend.de/badge/9674/version)](https://plugins.jetbrains.com/plugin/9674)
[![Downloads](http://phpstorm.espend.de/badge/9674/downloads)](https://plugins.jetbrains.com/plugin/9674)
[![Downloads last month](http://phpstorm.espend.de/badge/9674/last-month)](https://plugins.jetbrains.com/plugin/9674)
[![Donate to this project using Paypal](https://img.shields.io/badge/paypal-donate-yellow.svg)](https://www.paypal.me/DanielEspendiller)

PhpStorm plugin to provide smart autocomplete, code navigation and refactoring features for mocked class methods. Supported all versions of PhpStorm since 2017.1

Key         | Value
----------- | -----------
Plugin Url  | https://plugins.jetbrains.com/plugin/9674
ID          | de.espend.idea.php.phpunit
Changelog   | [CHANGELOG](CHANGELOG.md)
Build and Deployment | [MAINTENANCE](MAINTENANCE.md)
Origin Fork | [maxfilatov/phpuaca](https://github.com/maxfilatov/phpuaca/)

## Installation

Stable version, JetBrains repository:
* Go to `PhpStorm -> Preferences... -> Plugins -> Browse repositories ...` and search for PHPUnit Enhancement plugin
* Restart PhpStorm

## Feature list

* Method autocompletion for class, abstract class and trait mock objects;
  * Type providers: `getMock`, `getMockForAbstractClass`, etc. will return mock object with methods of mocking class and `PHPUnit_Framework_MockObject_MockObject`;
  * Supported PHPUnit methods:
    * `PHPUnit_Framework_MockObject_MockBuilder::setMethods`
    * `PHPUnit_Framework_TestCase::getMock`
    * `PHPUnit_Framework_TestCase::getMockClass`
    * `PHPUnit_Framework_TestCase::getMockForAbstractClass`
    * `PHPUnit_Framework_TestCase::getMockForTrait`
    * `PHPUnit_Framework_MockObject_Builder_InvocationMocker::method` 
* Code navigation (go to declaration, find usages, etc.) and refactoring (rename methods);
* Highlighting of incorrect method usages;
* Prophecy support.
* Mockery support.

### Mocks

```php
/** @var $x \PHPUnit\Framework\TestCase */
$x->createMock(Foo::class)->bar();
```

```php
/** @var $x \PHPUnit\Framework\TestCase */
$x->prophesize(Foo::class)->bar();
```

```php
class Foo extends \PHPUnit\Framework\TestCase
{
   public function foobar()
   {
       $foo = $this->createMock(Foo::class);
       $foo->method('<caret>')
   }
}
```

```php
class Foo extends \PHPUnit\Framework\TestCase
{
   public function testFoo()
   {
       $bar = $this->createPartialMock(\\Foo\Bar::class, ['<caret>']);
   }
}
```

```php
class Foo extends \PHPUnit\Framework\TestCase
{
   public function setUp()
   {
       $this->foo = $this->createMock('Foo\Bar');
   }
   public function foobar()
   {
       $this->foo->method('<caret>');
   }
}
```

```php
class FooTest extends \PHPUnit\Framework\TestCase
    {
        public function setUp()
        {
            $this->foo = $this->prophesize(Foo::class);
        }
        public function testFoobar()
        {
            $this->foo->getBar()->willReturn();
        }
    }
```

```php
class FooTest extends \PHPUnit\Framework\TestCase
    {
        public function setUp()
        {
            $this->foo = $this->getMockBuilder(\Foo::class);
        }
        public function testFoobar()
        {
            $this->foo->getMock()->bar();
        }
    }
```

### Prophecy

```php
class FooTest extends \PHPUnit\Framework\TestCase
{
    public function testFoobar()
    {
        $foo = $this->prophesize(Foo::class);
        $foo->getBar()->willReturn();
    }
}
```

```php
class FooTest extends \PHPUnit\Framework\TestCase
{
    public function setUp()
    {
        $this->foo = $this->prophesize(Foo::class);
    }
    
    public function testFoobar()
    {
        $this->foo->getBar()->willReturn();
    }
}

```

```php
class FooTest extends \PHPUnit\Framework\TestCase
    {
        public function testFoobar()
        {
            $foo = $this->prophesize(Foo::class);
            $foo->reveal()->getBar();
        }
    }
```

### Intention / Generator

Use intention / generator to add new method mocks. Every caret position inside a mock object is detected

```php
$foo = $this->getMockBuilder(Foobar::class)->getMock();
$foo->method('getFoobar')->willReturn();

$foo = $this->createMock(Foobar::class);
$foo->method('getFoobar')->willReturn();
```

```php
$this->foobar = $this->getMockBuilder(Foobar::class)->getMock();
// ...
$this->foobar->method('getFoobar')->willReturn();

$this->foobar = $this->createMock(Foobar::class);
// ...
$this->foobar->method('getFoobar')->willReturn();
```

```php
new Foobar();
// ...
new Foobar(
    $this->createMock(Foo::class),
    $this->createMock(FooBar::class)
);
```

```php
/**
 * @expectedException \Foo\FooException
 */
public function testExpectedException()
{
    $foo = new FooBar();
    $foo->throwFooException();
}
```

Examples
--------

![PHPUnit_Framework_MockObject_MockBuilder::setMethods](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16946.png)

![PHPUnit_Framework_MockObject_Builder_InvocationMocker::method](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16945.png)

![PHPUnit_Framework_MockObject_Builder_InvocationMocker::method](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16944.png)

![PHPUnit Runner LineMarker](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16951.png)

![PHPUnit Prophecy](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16953.png)

![PHPUnitExpected exception](https://plugins.jetbrains.com/files/9674/screenshot_17449.png)

![Mockery Support](https://plugins.jetbrains.com/files/9674/screenshot_8dc4aac6-3194-4799-b889-7475e7f2e7ff)

![Mockery Support](https://plugins.jetbrains.com/files/9674/screenshot_d44c8d9b-3038-4ece-80f1-f88f3b3ea84a)

## Mockery

Has support for
* Method referencing and autocomplete for method string in `allows`, `expects`, `shouldReceive`, `shouldNotReceive`, 
`shouldHaveReceived`, `shouldNotHaveReceived`. As well as in Generated partial mocks.
* Highlighting for incorrect methods used inside an `allows` etc., when method is private, protected, or not found.
* Type providers to enable new Mockery syntax: `$mock->allows()->foo('arg')->andReturns('mocked_result')`.
* Configurable inspection for replacing legacy Mockery syntax: replacing`$mock->shouldReceive("foo")->with("arg")->andReturn("result")` 
with `$mock->allows()->foo("arg")->andReturns("result")`. 

### Referencing

In the following code snippets referencing, autocompletion, and refactoring are supported at the carets.
Note that these all work with aliases, overloaded mocks, proxies, and partial mocks.

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::mock('MockeryPlugin\DemoProject\Dependency');
        $mock->allows('fo<caret>o');
        $mock->expects('f<caret>oo');
        $mock->shouldReceive('ba<caret>r');
        $mock->shouldNotReceive('b<caret>ar');
    }
}
```

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $this->mock = Mockery::mock('MockeryPlugin\DemoProject\Dependency');
    }
    
    public function test(): void
    {
        $this->mock->allows('fo<caret>o')->andReturns('result');
    }
}
```

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::spy('MockeryPlugin\DemoProject\Dependency');
        // ...
        $mock->shouldHaveReceived('b<caret>ar');
        $mock->shouldNotHaveReceived('fo<caret>o');
    }
}
```

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::mock('MockeryPlugin\DemoProject\Dependency');
        $mock->shouldReceive('foo', 'b<caret>ar')
        $mock->shouldReceive([
            'foo' => 'mocked result',
            'ba<caret>r' => 'mocked result'
        ]);
    }
}
```

### Generated Partial Mocks

Method name referencing/refactoring is supported when creating generated partial mocks.
```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::mock(Dependency::class . "[f<caret>oo]");
        $mock = Mockery::mock('MockeryPlugin\DemoProject\Dependency[f<caret>oo]');
    }
}
```

### Method Annotations

A warning highlight is given when the method being used is protected, private, or not found.

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::mock(Dependency::class);
        $mock->expects('protectedMethod');
        $mock->expects('privateMethod');
        $mock->expects('unknownMethod');
    }
}
```

### Inspection

An inspection is provided which will highlight legacy mockery syntax and provides a quick fix 
to update. Legacy Mockery uses `shouldReceive`/`shouldNotReceive`, and it gets replaced by `allows`/`expects`, e.g.

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::mock(Dependency::class);
        $mock->shouldReceive('foo')->with('arg')->andReturn('result');
        // replaced by
        $mock->allows('foo')->with('arg')->andReturns('result');
        
        $mock->shouldReceive('foo')->with('arg')->andReturn('result')->once();
        // replaced by
        $mock->expects('foo')->with('arg')->andReturns('result');
    }
}
```

If a `shouldReceive` has multiple method parameters then these will get combined into an array parameter. 
But the inspection can be configured to prefer writing multiple `allows`/`expects` statements.

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::mock(Dependency::class);
        $mock->$this->dependency->shouldReceive('foo', 'bar');
        // replaced by
        $mock->allows(['foo', 'bar']);
        
        $mock->shouldReceive('foo', 'bar')->andReturns('mocked result');
        // replaced by
        $mock->allows(['foo' => 'mocked result', 'bar' => 'mocked result']);
    }
}
```

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::mock(Dependency::class);
        $mock->$this->dependency->shouldReceive('foo', 'bar');
        // replaced by
        $this->dependency->allows('foo');
        $this->dependency->allows('bar');
        
        $mock->shouldReceive('foo', 'bar')->andReturns('mocked result');
        // replaced by
        $this->dependency->allows('foo')->andReturns('mocked result');
        $this->dependency->allows('bar')->andReturns('mocked result');    }
}
```

The inspection can also be configured to prefer the new Mockery syntax in which the mocked methods are called
like normal rather than as a string.

```php
class Foo extends Mockery\Adapter\Phpunit\MockeryTestCase
{
    protected function setUp(): void
    {
        $mock = Mockery::mock(Dependency::class);
        $mock->shouldReceive('foo')->with('arg')->andReturn('result');
        // replaced by
        $mock->allows()->foo('arg')->andReturns('result');
        
        $mock->shouldReceive('foo')->with('arg')->andReturn('result')->once();
        // replaced by
        $mock->expects()->foo('arg')->andReturns('result');
    }
}
```

### New Mockery Syntax Type Provider

Type providers are implemented so that when calling `allows()` on a mock it will have the type of the 
mocked class. Further `allows()->foo()` will be given the type Mockery/Expectation so that methods like `andReturns(..)` 
work as expected. This extends also to `expects()`, `shouldReceive()`, `shouldNotReceive()` and `shouldHaveReceived()`. 
Note: this new syntax does not extend tp `shouldNotHaveReceived`.

In the following example the first caret has type Dependency, and the second type Expectation.
```php
$mock = Mockery::mock(Dependency::class);
$mock->allows<caret>()->foo<caret>('arg')->andReturns('result');
```