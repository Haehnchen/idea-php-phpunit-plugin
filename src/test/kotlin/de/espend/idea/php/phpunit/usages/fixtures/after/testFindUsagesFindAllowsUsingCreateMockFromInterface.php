<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\Adapter\Phpunit\MockeryPHPUnitIntegration;
use Mockery\MockInterface;
use phpDocumentor\Reflection\Types\Boolean;
use PHPUnit\Framework\TestCase;

interface DependencyInterface
{
    public function called<caret>Method(string $parameter): string;

    public function secondCalledMethod(string $parameter): string;

    public function uncalledMethod(): void;

    public function secondUncalledMethod(): void;
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
        $this->dependency = Mockery::mock(DependencyInterface::class);
        $this->underTest = new MainClass($this->dependency);
    }

    public function testInvokeWithAllows(): void
    {
        $this->dependency->allows('<usage>')->andReturns('mocked result');
        $result = $this->underTest->invokeMethodOfDependency('test parameter');
        self::assertSame('mocked result', $result);
    }
}
