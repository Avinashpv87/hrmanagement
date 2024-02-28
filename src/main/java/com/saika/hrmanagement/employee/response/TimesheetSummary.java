package com.saika.hrmanagement.employee.response;

import lombok.Data;

@Data
public class TimesheetSummary {
    private long approved;
    private long rejected;
    private long submitted;
    private long saved;
}
