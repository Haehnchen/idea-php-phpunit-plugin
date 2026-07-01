<?php

namespace {
    abstract class PHPUnit_Framework_TestCase
    {
        /**
         * @return PHPUnit_Framework_MockObject_MockBuilder
         */
        protected function getMockBuilder($originalClassName)
        {
            return new PHPUnit_Framework_MockObject_MockBuilder();
        }

        /**
         * @return PHPUnit_Framework_MockObject_MockObject
         */
        protected function getMock($originalClassName, array $methods = array())
        {
            return new PHPUnit_Framework_MockObject_MockObject();
        }

        /**
         * @return PHPUnit\Framework\MockObject\MockObject
         */
        protected function createMock($originalClassName)
        {
            return new PHPUnit\Framework\MockObject\MockObject();
        }

        protected function getMockClass($originalClassName, array $methods = array())
        {
            return '';
        }

        protected function getMockForAbstractClass(
            $originalClassName,
            array $arguments = array(),
            $mockClassName = '',
            $callOriginalConstructor = true,
            $callOriginalMethods = true,
            $callAutoload = true,
            array $mockedMethods = array()
        ) {
            return new PHPUnit_Framework_MockObject_MockObject();
        }

        protected function getMockForTrait(
            $traitName,
            array $arguments = array(),
            $mockClassName = '',
            $callOriginalConstructor = true,
            $callOriginalMethods = true,
            $callAutoload = true,
            array $mockedMethods = array()
        ) {
            return new PHPUnit_Framework_MockObject_MockObject();
        }
    }

    class PHPUnit_Framework_MockObject_MockObject
    {
        /**
         * @return PHPUnit_Framework_MockObject_Builder_InvocationMocker
         */
        public function expects($matcher = null)
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
        /**
         * @return $this
         */
        public function setMethods(array $methods = array())
        {
            return $this;
        }

        /**
         * @return PHPUnit_Framework_MockObject_MockObject
         */
        public function getMock()
        {
            return new PHPUnit_Framework_MockObject_MockObject();
        }
    }

    class PHPUnit_Framework_MockObject_Builder_InvocationMocker
    {
        /**
         * @return $this
         */
        public function method($constraint)
        {
            return $this;
        }
    }

    class MethodMock
    {
        public static function resetMethodCalledStack($className, $methodName)
        {
        }

        public static function getCalledArgs($className, $methodName)
        {
        }

        public static function isMethodCalled($className, $methodName)
        {
        }

        public static function countMethodCalled($className, $methodName)
        {
        }

        public static function revertMethod($className, $methodName)
        {
        }

        public static function interceptMethodByCode($className, $methodName)
        {
        }

        public static function interceptMethod($className, $methodName)
        {
        }

        public static function mockMethodResult($className, $methodName)
        {
        }

        public static function mockMethodResultByMap($className, $methodName)
        {
        }

        public static function revertMethodResult($className, $methodName)
        {
        }

        public static function callProtectedMethod($className, $methodName)
        {
        }
    }

    class PHPUnit_Helper
    {
        public static function getProtectedPropertyValue($className, $propertyName)
        {
        }

        public static function setProtectedPropertyValue($className, $propertyName, $value = null)
        {
        }

        public static function callProtectedMethod($className, $methodName)
        {
        }
    }

    class PhpUnitMockStringTarget
    {
        protected $protectedProperty;

        public function __construct()
        {
        }

        public function __destruct()
        {
        }

        public function publicMethod()
        {
        }

        protected function protectedMethod()
        {
        }
    }

    abstract class PhpUnitMockAbstractTarget
    {
        abstract public function publicMethod();
    }

    trait PhpUnitMockTraitTarget
    {
        public function traitMethod()
        {
        }
    }
}

namespace PHPUnit\Framework {
    abstract class TestCase extends \PHPUnit_Framework_TestCase
    {
    }
}

namespace PHPUnit\Framework\MockObject {
    class MockObject
    {
        /**
         * @return Builder\InvocationMocker
         */
        public function method($constraint)
        {
            return new Builder\InvocationMocker();
        }
    }
}

namespace PHPUnit\Framework\MockObject\Builder {
    class InvocationMocker
    {
        /**
         * @return $this
         */
        public function method($constraint)
        {
            return $this;
        }
    }
}
