package com.saika.hrmanagement.common.entity;

import com.fasterxml.jackson.core.io.NumberInput;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;

@Data
@AllArgsConstructor
public class TimesheetChartAggregate {
    @Id
    private IdValue _id;

    private String totalHours;
    private double count;
}
