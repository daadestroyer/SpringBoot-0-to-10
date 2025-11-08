package com.example.SpringBootTesting_07;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Intro {
	@Test
	public void test1(){
		int a = 5;
		int b = 3;

		int result = a + b;
		Assertions.assertThat(result).isEqualTo(8).isCloseTo(9, Offset.offset(1));
	}
}
