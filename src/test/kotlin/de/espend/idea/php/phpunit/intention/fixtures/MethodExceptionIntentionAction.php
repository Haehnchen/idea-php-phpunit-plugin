<?php

namespace PHPUnit\Framework
{
    class TestCase
    {
    }
}

namespace Foo
{
    class ExpectedException extends \Exception
    {
    }

    class Service
    {
        /**
         * @throws \Foo\ExpectedException
         */
        public function throwsExpectedException()
        {
            throw new ExpectedException();
        }
    }
}
