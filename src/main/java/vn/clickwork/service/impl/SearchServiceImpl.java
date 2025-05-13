package vn.clickwork.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Job;
import vn.clickwork.model.response.SearchResponse;
import vn.clickwork.repository.SearchRepository;
import vn.clickwork.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService{

	@Autowired
	SearchRepository searchRepo;
	
	@Override
	public ResponseEntity<SearchResponse> searchKeyword(String keyword) {
		List<Job> jobs = searchRepo.searchKeyword(keyword);
		List<Employer> employers = searchRepo.searchEmployer(keyword);
		
		if (jobs.isEmpty() && employers.isEmpty()) {
			return ResponseEntity.ok(new SearchResponse(false, "Không tìm thấy dữ liệu", null, null));
		}
		
		return new ResponseEntity<SearchResponse>(new SearchResponse(true, "Lấy dữ liệu thành công", employers, jobs), HttpStatus.OK);
	}
}
