package com.saika.hrmanagement.employee.controller.helper;


import com.saika.hrmanagement.common.constant.EStatus;
import com.saika.hrmanagement.common.constant.EWorkAuthType;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.EmployeeDetailRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "FirstName", "LastName", "Email", "EmployeeId",
                                "PrimaryPhoneNumber", "DateOfJoin", "VisaType", "VisaStatus",
                                "Designation", "EmploymentType", "WageType", "Skills"};
    static String SHEET = "Employees";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<EmployeeDetailRequest> excelToEmployeeRequestList(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<EmployeeDetailRequest> employeeDetailRequestList = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                EmployeeDetailRequest employeeDetail = new EmployeeDetailRequest();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    DataFormatter formatter = new DataFormatter();
                    String cellValue = formatter.formatCellValue(currentCell);
                    if (currentCell != null) {
                        switch (cellIdx) {
                            case 0:
                                employeeDetail.setFirstName(cellValue);
                                break;
                            case 1:
                                employeeDetail.setLastName(cellValue);
                                break;
                            case 2:
                                employeeDetail.setEmail(cellValue);
                                break;
                            case 3:
                                employeeDetail.setEmployeeIdentityNumber(cellValue);
                                break;
                            case 4:
                                employeeDetail.setPrimaryPhoneNumber(cellValue);
                                break;
                            case 5:
                                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                employeeDetail.setDateOfJoin(LocalDate.parse(cellValue, df));
                                break;
                            case 6:
                                employeeDetail.setWorkAuthType(EWorkAuthType.valueOf(cellValue));
                                break;
                            case 7:
                                employeeDetail.setWorkStatus(EStatus.valueOf(cellValue));
                                break;
                            case 8:
                                employeeDetail.setDesignation(cellValue);
                                break;
                            case 9:
                                employeeDetail.setEmployementType(cellValue);
                                break;
                            case 10:
                                employeeDetail.setWageType(cellValue);
                                break;
                            case 11:
                                if (currentCell.getStringCellValue() != null && !currentCell.getStringCellValue().isEmpty()) {
                                    employeeDetail.setSkills(Arrays.asList(currentCell.getStringCellValue().split(",")));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    cellIdx++;
                }
                employeeDetailRequestList.add(employeeDetail);
            }
            workbook.close();
            return employeeDetailRequestList;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "IOException, fail to parse Excel file:! " +  e.getMessage() );
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception, fail to parse Excel file:! " +  e.getMessage() );
        }
    }
}
