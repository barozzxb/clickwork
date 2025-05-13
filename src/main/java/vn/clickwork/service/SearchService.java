package vn.clickwork.service;

import org.springframework.http.ResponseEntity;

import vn.clickwork.model.response.SearchResponse;

public interface SearchService {

	ResponseEntity<SearchResponse> searchKeyword(String keyword);

}
