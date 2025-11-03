package com.example.SpringBootTesting_07;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class App1 {
	private int number;
	@BeforeAll
	static void setupAll() {
		System.out.println("👉 Runs once BEFORE all tests");
	}
	@AfterAll
	static void tearDownAll() {
		System.out.println("👋 Runs once AFTER all tests");
	}
	@BeforeEach
	void setup() {
		number = 5;
		System.out.println("🔹 Runs BEFORE each test → number set to " + number);
	}
	@AfterEach
	void cleanup() {
		System.out.println("🧹 Runs AFTER each test\n");
	}
	@Test
	@DisplayName("✅ Test 1: Addition")
	void testAddition() {
		number += 5;
		System.out.println("Test 1 running → number = " + number);
		Assertions.assertEquals(10, number);
	}
	@Test
	@DisplayName("✅ Test 2: Multiplication")
	void testMultiplication() {
		number *= 2;
		System.out.println("Test 2 running → number = " + number);
		Assertions.assertEquals(10, number);
	}
}
