<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\MockInterface;
use PHPUnit\Framework\TestCase;

class MainClassWithMockeryTest extends TestCase
{
    /** @var Dependency|MockInterface */
    private $dependency;

    protected function setUp(): void
    {
        $this->dependency = Mockery::mock(Dependency::class);
    }

    public function testInvokeWithShouldReceive(): void
    {
        $this->dependency->expects('calledMethod')->twice()->andReturns('mocked result');
    }
}