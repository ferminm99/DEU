package natbra.appstarter.server.controllers;

import java.util.List;
import java.util.Optional;

import natbra.appstarter.server.model.auth.UserType;
import natbra.appstarter.server.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import natbra.appstarter.server.Utils;
import natbra.appstarter.server.model.auth.User;
import natbra.appstarter.server.repository.UserRepository;

@Controller
public class UserController {
private final String baseUrl = "/admin/users";
	
	@Autowired UserRepository userRepository;

	@Autowired
	UserTypeRepository userTypeRepository;
	@Autowired PasswordEncoder encoder;
	@Autowired Utils utils;
	
	@GetMapping(baseUrl)
	public HttpEntity<List<User>> getUsers(){
		return ResponseEntity.ok(userRepository.findAll());
	}

	@GetMapping(baseUrl + "/get-user")
	public HttpEntity<Optional<User>> getUser(){
		return ResponseEntity.ok(userRepository.findById(utils.getAuthUser().getId()));
	}

	@GetMapping(baseUrl + "/get-players")
	public HttpEntity<List<User>> getUsersByTrainerId(){
		return ResponseEntity.ok(userRepository.findAllByTrainerId(utils.getAuthUser().getId()));
	}

	@GetMapping(baseUrl + "/get-types")
	public HttpEntity<List<UserType>> getUserTypes(){
		return ResponseEntity.ok(userTypeRepository.findAll());
	}
	@DeleteMapping(baseUrl)
	public HttpEntity<User> deleteUser(@RequestBody User user){
		userRepository.delete(user);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping(baseUrl)
	public HttpEntity<User> addUser(@RequestBody User user){
		user.setPassword(encoder.encode(user.getPassword()));
		return ResponseEntity.ok(userRepository.save(user));
	}

	@PutMapping(baseUrl)
	public HttpEntity<User> editUser(@RequestBody User user){
		return ResponseEntity.ok(userRepository.save(user));
	}
}
