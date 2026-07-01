<?php

namespace Foo
{
    class Bar
    {
        public function getFoobar()
        {
        }
    }
}

namespace PHPUnit\Framework
{
    abstract class TestCase
    {
        protected function createPartialMock(string $originalClassName, array $methods): MockObject
        {
        }
    }
}
