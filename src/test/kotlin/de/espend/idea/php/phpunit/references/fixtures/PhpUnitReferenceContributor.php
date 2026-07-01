<?php

namespace Foo
{
    class Bar
    {
        public function getFoobar()
        {
        }

        public function getAlternativeFoobar()
        {
        }
    }
}

namespace PHPUnit\Framework
{
    abstract class TestCase
    {
        /**
         * @param string $originalClassName
         *
         * @return \PHPUnit\Framework\MockObject\MockObject
         */
        protected function createMock($originalClassName)
        {
            return new \PHPUnit\Framework\MockObject\MockObject();
        }

        /**
         * @param string $originalClassName
         * @param array $methods
         *
         * @return \PHPUnit\Framework\MockObject\MockObject
         */
        protected function createPartialMock($originalClassName, array $methods)
        {
            return new \PHPUnit\Framework\MockObject\MockObject();
        }
    }
}

namespace PHPUnit\Framework\MockObject
{
    class MockObject
    {
        /**
         * @return \PHPUnit\Framework\MockObject\Builder\InvocationMocker
         */
        public function expects()
        {
            return new \PHPUnit\Framework\MockObject\Builder\InvocationMocker();
        }

        /**
         * @return \PHPUnit\Framework\MockObject\Builder\InvocationMocker
         */
        public function method($constraint)
        {
            return new \PHPUnit\Framework\MockObject\Builder\InvocationMocker();
        }
    }
}

namespace PHPUnit\Framework\MockObject\Builder
{
    class InvocationMocker
    {
        /**
         * @return \PHPUnit\Framework\MockObject\Builder\InvocationMocker
         */
        public function method($constraint)
        {
            return new \PHPUnit\Framework\MockObject\Builder\InvocationMocker();
        }
    }
}

namespace
{
    abstract class PHPUnit_Framework_TestCase
    {
        /**
         * @param string $originalClassName
         *
         * @return PHPUnit_Framework_MockObject_MockObject
         */
        protected function createMock($originalClassName)
        {
            return new PHPUnit_Framework_MockObject_MockObject();
        }

        /**
         * @param string $originalClassName
         * @param array $methods
         *
         * @return PHPUnit_Framework_MockObject_MockObject
         */
        protected function createPartialMock($originalClassName, array $methods)
        {
            return new PHPUnit_Framework_MockObject_MockObject();
        }
    }

    /**
     * @method PHPUnit_Framework_MockObject_Builder_InvocationMocker method($constraint)
     */
    class PHPUnit_Framework_MockObject_MockObject
    {
        /**
         * @return PHPUnit_Framework_MockObject_Builder_InvocationMocker
         */
        public function expects()
        {
            return new PHPUnit_Framework_MockObject_Builder_InvocationMocker();
        }

        /**
         * @return PHPUnit_Framework_MockObject_Builder_InvocationMocker
         */
        public function method($constraint)
        {
            return new PHPUnit_Framework_MockObject_Builder_InvocationMocker();
        }
    }

    class PHPUnit_Framework_MockObject_MockBuilder
    {

    }

    class PHPUnit_Framework_MockObject_Builder_InvocationMocker
    {
    }
}
