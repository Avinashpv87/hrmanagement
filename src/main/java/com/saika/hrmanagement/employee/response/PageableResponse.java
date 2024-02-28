package com.saika.hrmanagement.employee.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mani
 * @param <T>
 */
@Data
@NoArgsConstructor
public class PageableResponse<T> {
    int currentPageSize;
    int totalPages;
    int currentPage;
    int totalCount;
    T data;
}
