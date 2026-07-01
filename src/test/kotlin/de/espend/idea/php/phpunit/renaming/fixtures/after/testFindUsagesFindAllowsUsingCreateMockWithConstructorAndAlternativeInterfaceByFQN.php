<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\Adapter\Phpunit\MockeryPHPUnitIntegration;
use Mockery\MockInterface;
use phpDocumentor\Reflection\Types\Boolean;
use PHPUnit\Framework\TestCase;

class DependencyWithConstructor
{
    private string $suffix;

    public function __construct(string $suffix)
    {
        $this->suffix = $suffix;
    }

    /**
     * usage search for this method should find all references in all test files
     */
    public function newName(string $parameter): string
        {
            return "parameter: $parameter" . $this->suffix;
        }

    public function secondCalledMethod(string $parameter): string
    {
        return "parameter: $parameter" . $this->suffix;
    }
}

class MainClassWithMockeryTest extends TestCase
{
    use MockeryPHPUnitIntegration;

    private MainClass $underTest;

    /** @var Dependency|MockInterface */
    private $dependency;

    protected function setUp(): void
    {
        parent::setUp();
        $this->dependency = Mockery::mock('MockeryPlugin\DemoProject\DependencyWithConstructor', 'MockeryPlugin\DemoProject\AlternativeInterface', ['suffix']);
        $this->underTest = new MainClass($this->dependency);
    }

    public function testInvokeWithAllows(): void
    {
        $this->dependency->allows('newName')->andReturns('mocked result');
        $result = $this->underTest->invokeMethodOfDependency('test parameter');
        self::assertSame('mocked result', $result);
    }
}

