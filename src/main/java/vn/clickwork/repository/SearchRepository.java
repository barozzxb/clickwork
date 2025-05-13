package vn.clickwork.repository;

import java.util.List;

import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Job;

public interface SearchRepository {

	List<Employer> searchEmployer(String keyword);

	List<Job> searchKeyword(String keyword, int page, int size);

	List<Job> searchKeyword(String keyword);

}
