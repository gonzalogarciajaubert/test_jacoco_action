package com.example;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases for the example of test coverage.
 */
class HelloWorldTests {

	@Test
	public void test1() {
		HelloWorld hw = new HelloWorld();
		assertEquals("Hello World!", hw.getGreeting());
	}

	@Test
	public void test2() {
		HelloWorld hw = new HelloWorld();
		assertTrue(hw.didWeGreet("World"));
	}
}
