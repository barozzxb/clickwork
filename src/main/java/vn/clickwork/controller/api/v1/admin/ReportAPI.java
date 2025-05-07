package vn.clickwork.controller.api.v1.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.clickwork.model.dto.StatisticsDTO;
import vn.clickwork.service.ReportService;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportAPI {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<StatisticsDTO> getAllStatistics() {
        StatisticsDTO report = reportService.getAllStatistics();
        return ResponseEntity.ok(report);
    }
}
