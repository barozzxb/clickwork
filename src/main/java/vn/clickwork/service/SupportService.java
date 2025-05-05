package vn.clickwork.service;

import vn.clickwork.model.Response;
import vn.clickwork.model.dto.SupportResponseDTO;
import vn.clickwork.model.request.SupportResponseRequest;

public interface SupportService {
    Response getAllSupportRequests();
    Response getSupportRequestById(Long id);
    Response respondToSupportRequest(Long id, SupportResponseRequest request);
}