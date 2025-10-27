package com.example.SpringSecurity_FormLogin_04;

import com.example.SpringSecurity_FormLogin_04.entity.User;
import com.example.SpringSecurity_FormLogin_04.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringSecurityFormLogin04Application implements CommandLineRunner {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityFormLogin04Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
		User existingAdmin = userRepository.findByUsername("admin").orElse(null);
		if (existingAdmin == null) {
			User admin = User.builder()
					.username("admin")
					.password(bCryptPasswordEncoder.encode("admin"))
					.roles("ROLE_ADMIN") // ✅ always prefix roles with ROLE_
					.build();
			userRepository.save(admin);
			System.out.println("✅ Created admin user: admin / admin");
		} else {
			System.out.println("ℹ️ Admin user already exists — skipping creation.");
		}

		User existingUser = userRepository.findByUsername("shubham").orElse(null);
		if (existingUser == null) {
			User user = User.builder()
					.username("shubham")
					.password(bCryptPasswordEncoder.encode("shubham"))
					.roles("ROLE_USER")
					.build();
			userRepository.save(user);
			System.out.println("✅ Created normal user: shubham / shubham");
		} else {
			System.out.println("ℹ️ User 'shubham' already exists — skipping creation.");
		}
    }
}
