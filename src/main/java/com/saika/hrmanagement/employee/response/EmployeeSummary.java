package com.saika.hrmanagement.employee.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EmployeeSummary {

    private long enrolled;
    private long notEnrolled;
    private long active;
    private long notActive;
    private long deleted;
    private long totalVisit;
    private Map<String, Long> workAuthTypeCount = new HashMap<>();
    private Map<String, Long> employeeDesignationCount = new HashMap<>();

}
