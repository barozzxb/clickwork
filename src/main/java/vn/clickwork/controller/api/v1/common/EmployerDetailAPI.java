package vn.clickwork.controller.api.v1.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.model.Response;
import vn.clickwork.model.dto.EmployerDTO;
import vn.clickwork.model.dto.EmployerProfileDTO;
import vn.clickwork.service.EmployerService;

@RestController
@RequestMapping("/api/employer-detail")
public class EmployerDetailAPI {

	@Autowired
	EmployerService employerService;
	
	@GetMapping("/{username}")
	public ResponseEntity<Response> getEmployerDetail(@PathVariable("username") String username) {
		return employerService.findByEUsername(username);
	}
}
