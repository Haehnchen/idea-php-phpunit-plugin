<?php

namespace Foo\Test\Bar\Car
{
    use PHPUnit\Framework\TestCase;

    class FoobarTest extends TestCase
    {
    }
}

namespace Foo\Bar\Car
{
    use PHPUnit\Framework\TestCase;

    class DirectTest extends TestCase
    {
    }

    class FakeTest
    {
    }
}

namespace Foo\Bar\Tests\Car
{
    use PHPUnit\Framework\TestCase;

    class TestsNamespaceTest extends TestCase
    {
    }
}

namespace PHPUnit\Framework
{
    abstract class TestCase
    {
    }
}
