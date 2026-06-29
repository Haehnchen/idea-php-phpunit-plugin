<?php

class PhpUnitMockStringAnnotatorFixtureTest extends PHPUnit_Framework_TestCase
{
    public function testMockMethodNames()
    {
        $this->getMock(PhpUnitMockStringTarget::class, 'publicMethod');
        $this->getMock(PhpUnitMockStringTarget::class, 'protectedMethod');
        $this->getMock(PhpUnitMockStringTarget::class, '<warning descr="Method 'missingMethod' not found in class PhpUnitMockStringTarget">missingMethod</warning>');
        $this->getMock(PhpUnitMockStringTarget::class, '<warning descr="Method '__construct' is not allowed to use here">__construct</warning>');
        $this->getMock(PhpUnitMockStringTarget::class, '<warning descr="Method '__destruct' is not allowed to use here">__destruct</warning>');

        $mock = $this->createMock(PhpUnitMockStringTarget::class);
        $mock->method('publicMethod');
    }

    public function testMissingProtectedFieldViaHelper()
    {
        PHPUnit_Helper::getProtectedPropertyValue(
            PhpUnitMockStringTarget::class,
            '<warning descr="Method 'missingProtectedProperty' not found in class PhpUnitMockStringTarget">missingProtectedProperty</warning>'
        );

        PHPUnit_Helper::setProtectedPropertyValue(PhpUnitMockStringTarget::class, 'protectedProperty');
        PHPUnit_Helper::setProtectedPropertyValue(
            PhpUnitMockStringTarget::class,
            '<warning descr="Method 'missingProtectedProperty' not found in class PhpUnitMockStringTarget">missingProtectedProperty</warning>'
        );
    }
}
