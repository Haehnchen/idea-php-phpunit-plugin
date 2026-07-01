<?php

namespace PHPUnit\Framework
{
    class TestCase
    {
        static function createMock() {}
        static function prophesize() {}

        /**
         * @return \PHPUnit\Framework\MockObject\MockBuilder
         */
        public function getMockBuilder() {
            return new \PHPUnit\Framework\MockObject\MockBuilder();
        }
    };
}

namespace
{
    class Foo
    {
        /**
         * return $this
         */
        public function bar() {}
    }
}

namespace PHPUnit\Framework\MockObject
{
    class MockBuilder
    {
        /**
         * @return $this
         */
        public function disableOriginalConstructor()
        {
            return $this;
        }

        /**
         * @return $this
         */
        public function getMock()
        {
            return $this;
        }
    }
}