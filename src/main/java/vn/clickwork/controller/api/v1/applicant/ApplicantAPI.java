package vn.clickwork.controller.api.v1.applicant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.model.Response;
import vn.clickwork.model.request.ApplicantDetailRequest;
import vn.clickwork.service.ApplicantService;

@RestController
@RequestMapping("/api/applicant")
public class ApplicantAPI {
	
	@Autowired
	private ApplicantService applicantServ;
	
	@GetMapping("/profile")
    public ResponseEntity<Response> getApplicantProfile(@AuthenticationPrincipal UserDetails userDetails) {
       String username = userDetails.getUsername(); // Lấy từ JWT
       return applicantServ.findByUsername(username);
    }
	
	@PutMapping("/profile/update")
	public ResponseEntity<Response> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ApplicantDetailRequest applicant) {
		return applicantServ.update(applicant);
	}
	
	@PostMapping("/profile/update/avatar")
	public ResponseEntity<Response> updateAvatar(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("avatarFile") MultipartFile file) {
		String username = userDetails.getUsername(); // Lấy từ JWT
		return applicantServ.updateAvatar(username, file);
	}
	

}
