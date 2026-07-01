<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\Adapter\Phpunit\MockeryTestCase;
use Mockery\MockInterface;

class MockeryAnnotatorLegacyMethods extends MockeryTestCase
{
    /** @var Dependency|MockInterface */
    private $dependency;

    protected function setUp(): void
    {
        parent::setUp();
        $this->dependency = Mockery::mock(Dependency::class);
    }

    public function testShouldReceive(): void
    {
        $this->dependency->shouldReceive('calledMethod')->andReturn('mocked result');
        $this->dependency->shouldReceive('<warning descr="Method 'nomethod' not found in class Dependency">nomethod</warning>')->andReturn('mocked result');
    }

    public function testShouldNotReceive(): void
    {
        $this->dependency->shouldNotReceive('calledMethod');
        $this->dependency->shouldNotReceive('<warning descr="Method 'nomethod' not found in class Dependency">nomethod</warning>');
    }
}
