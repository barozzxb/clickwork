package vn.clickwork.model.request;

public class ReportResolveRequest {
    private String status;
    private boolean violationConfirmed;

    // Getters, setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isViolationConfirmed() { return violationConfirmed; }
    public void setViolationConfirmed(boolean violationConfirmed) { this.violationConfirmed = violationConfirmed; }
}