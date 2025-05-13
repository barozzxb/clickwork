package vn.clickwork.service;

import vn.clickwork.model.Response;
import vn.clickwork.model.dto.SupportRequestDTO;
import vn.clickwork.model.request.SupportResponseRequest;


public interface SupportService {
    Response getAllSupportRequests(int page, int size, String search, String sortBy, String sortDir, String status);
    Response createSupportRequest(SupportRequestDTO dto, String actorUsername);
    Response getAllSupportRequests();
    Response getSupportRequestById(Long id);
    Response respondToSupportRequest(Long id, SupportResponseRequest request);
}