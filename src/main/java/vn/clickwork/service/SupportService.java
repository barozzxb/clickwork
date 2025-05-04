package vn.clickwork.service;

import vn.clickwork.dto.SupportRequestDTO;
import vn.clickwork.model.Response;

public interface SupportService {

    Response createSupportRequest(SupportRequestDTO dto, String actorUsername);
}
