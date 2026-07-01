<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject {

    use Exception;

    class Dependency implements DependencyInterface
    {
        /**
         * usage search for this method should find all references in all test files
         */
        public function calledMethod(string $parameter): string
        {
            return "parameter: $parameter";
        }

        public function secondCalledMethod(string $parameter): string
        {
            return "parameter: $parameter";
        }

        /**
         * usage search for this method should find all references in all test files
         *
         * @throws Exception
         */
        public function uncalledMethod(): void
        {
            throw new Exception('This method should never have been called!');
        }

        /**
         * @throws Exception
         */
        public function secondUncalledMethod(): void
        {
            throw new Exception('This method should never have been called!');
        }

        /**
         * @throws Exception
         */
        protected function protectedMethod(): void{
            throw new Exception('This method should never have been called!');
        }

        /**
         * @throws Exception
         */
        private function privateMethod(): void{
            throw new Exception('This method should never have been called!');
        }
    }

    interface DependencyInterface
    {
        public function calledMethod(string $parameter): string;

        public function secondCalledMethod(string $parameter): string;

        public function uncalledMethod(): void;

        public function secondUncalledMethod(): void;
    }

    // This interface isn't implemented by Dependency
    interface AlternativeInterface
    {
        public function alternativeCalledMethod(string $parameter): string;
        public function alternativeSecondCalledMethod(string $parameter): string;
        public function alternativeUncalledMethod(): void;
        public function alternativeSecondUncalledMethod(): void;
    }

    class DependencyWithConstructor implements DependencyInterface
    {
        private string $suffix;

        public function __construct(string $suffix){
            $this->suffix = $suffix;
        }

        /**
         * usage search for this method should find all references in all test files
         */
        public function calledMethod(string $parameter): string
        {
            return "parameter: $parameter" . $this->suffix;
        }

        public function secondCalledMethod(string $parameter): string
        {
            return "parameter: $parameter" . $this->suffix;
        }

        /**
         * usage search for this method should find all references in all test files
         *
         * @throws Exception
         */
        public function uncalledMethod(): void
        {
            throw new Exception('This method should never have been called!');
        }

        /**
         * @throws Exception
         */
        public function secondUncalledMethod(): void
        {
            throw new Exception('This method should never have been called!');
        }

        /**
         * @throws Exception
         */
        protected function protectedMethod(): void{
            throw new Exception('This method should never have been called!');
        }

        /**
         * @throws Exception
         */
        private function privateMethod(): void{
            throw new Exception('This method should never have been called!');
        }
    }
}

namespace {

    use Mockery\LegacyMockInterface;

    class Mockery
    {
        /**
         * @param $originalClassName
         * @return Mockery\LegacyMockInterface|Mockery\MockInterface
         */
        public static function mock($originalClassName)
        {
            return new Mockery\LegacyMockInterface();
        }

        /**
         * @param $originalClassName
         * @return Mockery\LegacyMockInterface|Mockery\MockInterface
         */
        public static function spy($originalClassName)
        {
            return new Mockery\LegacyMockInterface();
        }

        /**
         * @param $originalClassName
         * @return Mockery\LegacyMockInterface|Mockery\MockInterface
         */
        public static function namedMock($originalClassName)
        {
            return new Mockery\LegacyMockInterface();
        }
    }
}

namespace Mockery {


    class MockInterface extends LegacyMockInterface
    {
        /**
         * @param mixed $something  String method name or map of method => return
         * @return self|\Mockery\ExpectationInterface|\Mockery\Expectation
         */
        public function allows($something = []){
            return new Expectation();
        }

        /**
         * @param mixed $something  String method name (optional)
         * @return \Mockery\ExpectationInterface|\Mockery\Expectation
         */
        public function expects($something = null){
            return new Expectation();
        }
    }

    class LegacyMockInterface
    {
        /**
         * @param string|array ...$methodNames one or many methods that are expected to be called in this mock
         *
         * @return \Mockery\ExpectationInterface|\Mockery\Expectation
         */
        public function shouldReceive(...$methodNames){
            return new Expectation();
        }

        /**
         * @param string|array ...$methodNames one or many methods that are expected not to be called in this mock
         * @return \Mockery\ExpectationInterface|\Mockery\Expectation
         */
        public function shouldNotReceive(...$methodNames){
            return new Expectation();
        }

        /**
         * @param null|string $method
         * @param null|array|Closure $args
         * @return \Mockery\ExpectationInterface|\Mockery\Expectation
         */
        public function shouldHaveReceived($method = null, $args = null){
            return new Expectation();
        }

        /**
         * @param null|string $method
         * @param null|array|Closure $args
         * @return \Mockery\ExpectationInterface|\Mockery\Expectation
         */
        public function shouldNotHaveReceived($method, $args = null){
            return new Expectation();
        }

        /**
         * @return MockInterface
         */
        public function makePartial()
        {
            return new MockInterface();
        }


    }

    class Expectation implements ExpectationInterface
    {

        /**
         * Expected argument setter for the expectation
         *
         * @param mixed ...$args
         *
         * @return self
         */
        public function with(...$args)
        {
            return $this;
        }


        /**
         * Set a return value, or sequential queue of return values
         *
         * @param mixed ...$args
         * @return self
         */
        public function andReturn(...$args)
        {
            return $this;
        }

        /**
         * Set a return value, or sequential queue of return values
         *
         * @param mixed ...$args
         * @return self
         */
        public function andReturns(...$args)
        {
            return $this;
        }

        /**
         * Indicates that this expectation is expected exactly once
         *
         * @return self
         */
        public function once()
        {
            return $this->times(1);
        }

        /**
         * @return self
         */
        public function twice()
        {
            return $this->times(2);
        }

        /**
         * @param int $limit
         * @return self
         */
        public function times($limit = null)
        {
            return $this;
        }
    }

    interface ExpectationInterface
    {
        /**
         * @param mixed $args
         * @return self
         */
        public function andReturn(...$args);

        /**
         * @return self
         */
        public function andReturns();
    }
}

namespace Mockery\Adapter\Phpunit{
    class MockeryTestCase{
        protected function setUp(){

        }
    }
}
