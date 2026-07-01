<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\Adapter\Phpunit\MockeryPHPUnitIntegration;
use Mockery\MockInterface;
use phpDocumentor\Reflection\Types\Boolean;
use PHPUnit\Framework\TestCase;

class Dependency
{

    /**
     * usage search for this method should find all references in all test files
     */
    public function newName(string $parameter): string
    {
        return "parameter: $parameter";
    }

    public function secondCalledMethod(string $parameter): string
    {
        return "parameter: $parameter";
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
        $this->dependency = Mockery::mock(Dependency::class);
        $this->underTest = new MainClass($this->dependency);
    }

    public function testInvokeWithShouldReceive(): void
    {
        $this->dependency->shouldReceive('newName')->andReturns('mocked result');
        $result = $this->underTest->invokeMethodOfDependency('test parameter');
        self::assertSame('mocked result', $result);
    }
}
