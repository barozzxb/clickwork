package vn.clickwork.controller.api.v1.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.model.response.SearchResponse;
import vn.clickwork.service.SearchService;

@RestController
@RequestMapping("/api/search")
public class SearchAPI {

	@Autowired
	SearchService searchService;
	
	@PostMapping("/query")
	public ResponseEntity<SearchResponse> searchKeyword(@RequestBody String keyword) {
		return searchService.searchKeyword(keyword);
	}
}
