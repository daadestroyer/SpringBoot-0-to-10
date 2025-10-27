package com.example.JWT;

import com.example.JWT.entity.Role;
import com.example.JWT.entity.User;
import com.example.JWT.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
class JwtApplicationTests {

	@Autowired
	private JWTService jwtService;

	@Test
	void contextLoads() {
		User user = new User(4L, "shubham@gmail.com", "1234", Set.of(Role.ADMIN, Role.USER));
		String token = jwtService.generateAccessToken(user);
		System.out.println(token);
		Long userIdFromToken = jwtService.getUserIdFromToken(token);
		System.out.println(userIdFromToken);

	}

}
