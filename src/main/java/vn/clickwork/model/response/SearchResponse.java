package vn.clickwork.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SearchResponse {

	private boolean status;
	private String message;
	private Object employers;
	private Object jobs;
}
