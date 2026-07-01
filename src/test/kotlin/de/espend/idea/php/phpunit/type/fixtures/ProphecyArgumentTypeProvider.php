<?php

namespace PHPUnit\Framework
{

    use Prophecy\Prophecy\ObjectProphecy;

    class TestCase extends \PHPUnit_Framework_MockObject_InvocationMocker
    {
        /**
         * @param null $classOrInterface
         * @return \Prophecy\Prophecy\ObjectProphecy
         */
        protected function prophesize($classOrInterface = null)
        {
            return new ObjectProphecy();
        }
    };
}

namespace Prophecy
{
    class Argument
    {
        public static function any()
        {
        }

        public static function cetera()
        {
        }
    }
}

namespace
{
    class PHPUnit_Framework_MockObject_InvocationMocker
    {
        public function willReturn($value, ...$nextValues)
        {
        }
    }

    class Foo
    {
        public function getBar(array $test, Foo $foo)
        {
        }
    }
}
