<?php

namespace PHPUnit\Framework
{
    class TestCase
    {
        function getMock($className) {}
        function getMockClass($className) {}
        function getMockForAbstractClass($className) {}
        function getMockForTrait($className) {}
        function createMock($className) {}
        function prophesize($className) {}
    };
}

namespace
{
    class PHPUnit_Framework_TestCase
    {
        function getMock($className) {}
        function getMockClass($className) {}
        function getMockForAbstractClass($className) {}
        function getMockForTrait($className) {}
        function createMock($className) {}
        function prophesize($className) {}
    }

    class Foo
    {
        public function bar() {}
    }
}

namespace Prophecy\PhpUnit
{
    trait ProphecyTrait
    {
        protected function prophesize(?string $classOrInterface = null): ObjectProphecy
        {
        }
    }
}

namespace Prophecy
{
    class Prophet
    {
        function prophesize($className) {}
    }
}
