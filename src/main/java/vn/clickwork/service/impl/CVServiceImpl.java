package vn.clickwork.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.entity.Account;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.CV;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.CVDTO;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.CVRepository;
import vn.clickwork.service.CVService;

@Service
public class CVServiceImpl implements CVService {

	@Autowired
	private CVRepository cvRepository;
	@Autowired
	private ApplicantRepository applicantRepository;

	public List<CV> findByApplicant(Applicant applicant) {
		return cvRepository.findByApplicant(applicant);
	}

	public <S extends CV> S save(S entity) {
		return cvRepository.save(entity);
	}

	public Optional<CV> findById(Long id) {
		return cvRepository.findById(id);
	}

	public void deleteById(Long id) {
		cvRepository.deleteById(id);
	}

	@Override
	public ResponseEntity<Response> findByUsername(@RequestBody String username) {
		Applicant applicant = applicantRepository.findByAccount_Username(username);
		List<CV> cvList = cvRepository.findByApplicant(applicant);
		if (!cvList.isEmpty()) {
			List<CVDTO> cvDTOList = cvList.stream().map(cv -> {
				CVDTO dto = new CVDTO();
				dto.setId(cv.getId());
				dto.setName(cv.getName());
				dto.setFile(cv.getFile());
				return dto;
			}).toList();
			return ResponseEntity.ok(new Response(true, "Tải dữ liệu thành công", cvDTOList));
		} else {
			return ResponseEntity.ok(new Response(false, "Tải dữ liệu khong thành công", null));
		}
	}

	@Override
	public ResponseEntity<Response> uploadCV(String username, String filename, MultipartFile file) {
		if (file.isEmpty()) {
			throw new RuntimeException("File rỗng");
		}

		String uploadDir = "uploads/cvs/";

		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		Path path = Paths.get(uploadDir + fileName);

		try {
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
		}

		Applicant applicant = applicantRepository.findByAccount_Username(username);
		if (applicant == null) {
			throw new RuntimeException("Không tìm thấy người dùng");
		}
		
		String fileurl = "/uploads/cvs/" + fileName;
		
		CV cv = new CV();
		cv.setName(filename);
		cv.setFile(fileurl);
		cv.setApplicant(applicant);

		cvRepository.save(cv);

		return ResponseEntity.ok(new Response(true, "Upload CV thành công", cv));
	}
	
	@Override
	public ResponseEntity<Response> deleteCV(Long id) {
		try {
			Optional<CV> optcv = this.findById(id);
			if (optcv.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(false, "Không tìm thấy CV", null));
			}
			CV cv = optcv.get();
			String filePath = "uploads/cvs" + File.separator + Paths.get(cv.getFile()).getFileName().toString();
	        File file = new File(filePath);
	        if (file.exists()) {
	            file.delete();
	        }
			this.deleteById(id);
			return ResponseEntity.ok(new Response(true, "Xóa CV thành công", null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(false, "Xóa CV thất bại", null));
		}
	}
	
	private CVDTO toDTO(CV cv) {
		CVDTO dto = new CVDTO();
		dto.setId(cv.getId());
		dto.setName(cv.getName());
		dto.setFile(cv.getFile());
		return dto;
	}
	
}
