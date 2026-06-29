<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\Adapter\Phpunit\MockeryTestCase;
use Mockery\MockInterface;

class MockeryAnnotatorArrayScopes extends MockeryTestCase
{
    /** @var Dependency|MockInterface */
    private $dependency;

    protected function setUp(): void
    {
        parent::setUp();
        $this->dependency = Mockery::mock(Dependency::class);
    }

    public function testArrayElementScope(): void
    {
        $this->dependency->expects(['calledMethod'])->andReturns('mocked result');
        $this->dependency->expects(['<warning descr="Method 'nomethod' not found in class Dependency">nomethod</warning>'])->andReturns('mocked result');
    }

    public function testArrayHashScope(): void
    {
        $this->dependency->expects(['calledMethod' => 'nomethod'])->andReturns('mocked result');
        $this->dependency->expects(['<warning descr="Method 'nomethod' not found in class Dependency">nomethod</warning>' => 'result'])->andReturns('mocked result');
    }
}
