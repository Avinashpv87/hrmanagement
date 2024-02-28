package com.saika.hrmanagement.employee.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartResponse {

    private String name;
    private String type;
    private Integer[] data;

}
