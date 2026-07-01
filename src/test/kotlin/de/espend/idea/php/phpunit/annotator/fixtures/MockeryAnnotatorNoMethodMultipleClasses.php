<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\Adapter\Phpunit\MockeryTestCase;
use Mockery\MockInterface;

class MockeryAnnotatorNoMethodMultipleClasses extends MockeryTestCase
{

    /** @var Dependency|MockInterface */
    private $dependency;

    protected function setUp(): void
    {
        parent::setUp();
        $this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class);
    }

    public function testWithExpects(): void
    {
        $this->dependency->expects('calledMethod')->andReturns('mocked result');
        $this->dependency->expects('<warning descr="Method 'nomethod' not found in any of classes [AlternativeInterface, Dependency]">nomethod</warning>')->andReturns('mocked result');
    }

    public function testWithAllows(): void
    {
        $this->dependency->allows('calledMethod');
        $this->dependency->allows('<warning descr="Method 'nomethod' not found in any of classes [AlternativeInterface, Dependency]">nomethod</warning>');
    }
}
