<?php

namespace Foo
{
    class Bar
    {
        public function getFoobar()
        {
        }

        /**
         * @deprecated
         */
        public function getFoobarDeprecated()
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
         * @return \PHPUnit_Framework_MockObject_MockObject
         */
        protected function createMock($originalClassName)
        {
            return new \PHPUnit_Framework_MockObject_MockObject();
        }

        /**
         * @param string $originalClassName
         *
         * @return \PHPUnit_Framework_MockObject_MockObject
         */
        protected function createPartialMock(string $originalClassName, array $methods)
        {
            return new \PHPUnit_Framework_MockObject_MockObject();
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
    }

    class PHPUnit_Framework_MockObject_MockBuilder
    {

    }

    class PHPUnit_Framework_MockObject_Builder_InvocationMocker
    {
        /**
         * @param \PHPUnit_Framework_Constraint|string $constraint
         *
         * @return \PHPUnit_Framework_MockObject_Builder_InvocationMocker
         */
        public function method($constraint)
        {
            return new PHPUnit_Framework_MockObject_Builder_InvocationMocker();
        }
    }
}