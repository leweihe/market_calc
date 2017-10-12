package com.linde;

import com.linde.object.MarketTree;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ReadWriteExcelFile {

    //    private List<String> cityList = new ArrayList<>();
//    private List<String> distList = new ArrayList<>();
    private List<String> regnList = new ArrayList<>();
    private List<String> branList = new ArrayList<>();

    public static void writeXLSXFile(List<MarketTree> finalResult, String fullPath) throws IOException {

        String sheetName = "result";// name of sheet

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);

        // iterating r number of rows
        int rowNum = 0;
        rowNum = putHeaderInExcel(sheet.createRow(rowNum), rowNum);
        for (int i = 0; i < finalResult.size(); i++) {
            MarketTree level1 = finalResult.get(i);
            for (int j = 0; j < level1.getChildMarket().size(); j++) {
                MarketTree level2 = level1.getChildMarket().get(j);
                if (level2.getChildMarket() != null && level2.getChildMarket().size() > 0) {
                    for (int k = 0; k < level2.getChildMarket().size(); k++) {
                        MarketTree level3 = level2.getChildMarket().get(k);
                        if (level3.getChildMarket() != null && level3.getChildMarket().size() > 0) {
                            for (int l = 0; l < level3.getChildMarket().size(); l++) {
                                MarketTree level4 = level3.getChildMarket().get(l);
                                XSSFRow row = sheet.createRow(rowNum);
                                rowNum = putDataInExcel(level4, row, rowNum, ExcelHelper.getDistStyle(wb));
                            }
                        }
                        XSSFRow row = sheet.createRow(rowNum);
                        rowNum = putDataInExcel(level3, row, rowNum, ExcelHelper.getCityStyle(wb));
                    }
                }
                XSSFRow row = sheet.createRow(rowNum);
                rowNum = putDataInExcel(level2, row, rowNum, ExcelHelper.getRegnStyle(wb));

            }
            XSSFRow row = sheet.createRow(rowNum);
            rowNum = putDataInExcel(level1, row, rowNum, ExcelHelper.getBranStyle(wb));
        }

        FileOutputStream fileOut = new FileOutputStream(fullPath);

        // write this workbook to an Outputstream.
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }

    private static int putHeaderInExcel(XSSFRow row, Integer rowNum) {

        XSSFCell cell0 = row.createCell(0);
        cell0.setCellValue(ExcelHelper.column0);
        XSSFCell cell1 = row.createCell(1);
        cell1.setCellValue(ExcelHelper.column1);
        XSSFCell cell2 = row.createCell(2);
        cell2.setCellValue(ExcelHelper.column2);
        XSSFCell cell3 = row.createCell(3);
        cell3.setCellValue(ExcelHelper.column3);
        XSSFCell cell4 = row.createCell(4);
        cell4.setCellValue(ExcelHelper.column4);
        XSSFCell cell5 = row.createCell(5);
        cell5.setCellValue(ExcelHelper.column5);
        XSSFCell cell6 = row.createCell(6);
        cell6.setCellValue(ExcelHelper.column6);
        return rowNum + 1;
    }

    private static int putDataInExcel(MarketTree data, XSSFRow row, Integer rowNum, XSSFCellStyle style) {

        XSSFCell cell0 = row.createCell(0);
        cell0.setCellValue(data.getMonth());
        cell0.setCellStyle(style);

        XSSFCell cell1 = row.createCell(1);
        cell1.setCellValue(data.getSalesOrg());
        cell1.setCellStyle(style);

        XSSFCell cell2 = row.createCell(2);
        cell2.setCellValue(data.getSalesGrp());
        cell2.setCellStyle(style);

        XSSFCell cell3 = row.createCell(3);
        if (data.getPercentage() == null) {
            cell3.setCellValue(0);
        } else {
            cell3.setCellValue(data.getPercentage().doubleValue());
        }
        XSSFCellStyle style3Percentage = (XSSFCellStyle) style.clone();
        style3Percentage.setDataFormat(ExcelHelper.getPercentageFormat());
        cell3.setCellStyle(style3Percentage);

        XSSFCell cell4 = row.createCell(4);
        cell4.setCellValue(data.getTotalNumber().doubleValue());
        cell4.setCellStyle(style);

        XSSFCell cell5 = row.createCell(5);
        if (data.getPercentage() == null) {
            cell5.setCellValue(0);
        } else {
            cell5.setCellValue(data.getActuralCount() == null ? data.getPercentage().multiply(data.getTotalNumber()).doubleValue() : data.getActuralCount().doubleValue());
        }
        cell5.setCellStyle(style);

        XSSFCell cell6 = row.createCell(6);
        cell6.setCellValue(data.getRule().toString());
        cell6.setCellStyle(style);

        return rowNum + 1;
    }

    public List<MarketTree> readXLSXFile(String folder, String fileName) throws IOException {
        String fullPath = folder + fileName;

        InputStream ExcelFileToRead = new FileInputStream(fullPath);
        XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);

        InputStream rulerExcel = new FileInputStream(folder + "ruler.xlsx");
        XSSFWorkbook rulerWb = new XSSFWorkbook(rulerExcel);

        readRule(rulerWb);

        return readMarket(wb);
    }

    private List<MarketTree> readMarket(XSSFWorkbook wb) {

        List<MarketTree> regnMarket = new ArrayList<>();
        List<MarketTree> distMarket = new ArrayList<>();
        List<MarketTree> cityMarket = new ArrayList<>();
        List<MarketTree> branMarket = new ArrayList<>();

        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator rows = sheet.rowIterator();
        MarketTree mp = new MarketTree();

        Integer yrMonthsLine = -1;
        Integer salesOrgLine = -1;
        Integer salesGrpLine = -1;
        Integer targetRtLine = -1;

        while (rows.hasNext()) {
            XSSFRow row = (XSSFRow) rows.next();
            Iterator cells = row.cellIterator();
            MarketTree currentM = new MarketTree();


            while (cells.hasNext()) {
                XSSFCell cell = (XSSFCell) cells.next();

                if (cell.getCellTypeEnum().equals(CellType.STRING)) {
                    String contain = cell.getStringCellValue();

                    if (row.getRowNum() == 0) {
                        if (ExcelHelper.column0.equals(contain)) {
                            yrMonthsLine = cell.getColumnIndex();
                        }
                        if (ExcelHelper.column1.equals(contain)) {
                            salesOrgLine = cell.getColumnIndex();
                        }
                        if (ExcelHelper.column2.equals(contain)) {
                            salesGrpLine = cell.getColumnIndex();
                        }
                        if (contain.contains(ExcelHelper.column3)) {
                            targetRtLine = cell.getColumnIndex();
                        }

                    } else {
                        if (cell.getColumnIndex() == salesOrgLine) {
                            currentM.setSalesOrg(contain);
                        } else if (cell.getColumnIndex() == salesGrpLine) {
                            currentM.setSalesGrp(contain);
                        }
                        if (cell.getColumnIndex() == salesOrgLine) {
                            if (contain.contains("Total")) {
                                String tmpStr = contain.replace("Total", "").trim();
                                if (regnList.contains(tmpStr)) {
                                    currentM.setRule(RuleEnum.REGION);
                                } else if (branList.contains(tmpStr)) {
                                    currentM.setRule(RuleEnum.BRANCH);
                                } else {
                                    currentM.setRule(RuleEnum.CITY);
                                }
                            }
                        }
                    }


                } else if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                    Double contain = cell.getNumericCellValue();
                    if (cell.getColumnIndex() == targetRtLine) {
                        currentM.setPercentage(new BigDecimal(contain).setScale(3, BigDecimal.ROUND_HALF_UP));
                    } else if (cell.getColumnIndex() == salesOrgLine) {
                        currentM.setRule(RuleEnum.DISTRICT);
                        currentM.setTotalNumber(new BigDecimal(100));
                        currentM.setSalesOrg(contain.intValue() + "");
                    } else if (cell.getColumnIndex() == yrMonthsLine) {
                        currentM.setMonth(contain);
                    }
                } else if (cell.getCellTypeEnum().equals(CellType.BLANK)) {

                } else {
                    Date contain = cell.getDateCellValue();
                    if (cell.getColumnIndex() == yrMonthsLine) {
                        currentM.setMonth(contain.getTime() + 0D);
                    }
                }
            }

            if (RuleEnum.REGION.equals(currentM.getRule())) {
                if (cityMarket.size() == 0) {
                    currentM.setChildMarket(new ArrayList<>(distMarket));
                    distMarket.clear();
                } else {
                    currentM.setChildMarket(new ArrayList<>(cityMarket));
                    cityMarket.clear();
                }
                regnMarket.add(currentM);
            } else if (RuleEnum.CITY.equals(currentM.getRule())) {
                currentM.setChildMarket(new ArrayList<>(distMarket));
                distMarket.clear();
                cityMarket.add(currentM);
            } else if (RuleEnum.DISTRICT.equals(currentM.getRule())) {
                distMarket.add(currentM);
            } else if (RuleEnum.BRANCH.equals(currentM.getRule())) {
                currentM.setChildMarket(new ArrayList<>(regnMarket));
                regnMarket.clear();
                branMarket.add(currentM);
            }
        }
        return branMarket;
    }

    private void readRule(XSSFWorkbook wb) {

        XSSFSheet ruleSheet = wb.getSheetAt(0);
        Iterator ruleRows = ruleSheet.rowIterator();
        while (ruleRows.hasNext()) {
            XSSFRow ruleRow = (XSSFRow) ruleRows.next();
            RuleEnum currentRule = RuleEnum.NONE;
            Iterator cells = ruleRow.cellIterator();

            while (cells.hasNext()) {
                XSSFCell ruleCell = (XSSFCell) cells.next();
                if (ruleCell.getCellTypeEnum().equals(CellType.STRING)) {
                    String contain = ruleCell.getStringCellValue();
                    if (contain == null || "".equals(contain))
                        continue;

                    RuleEnum rule = Stream.of(RuleEnum.values()).filter(n -> n.toString().equals(contain)).findFirst().orElse(RuleEnum.NONE);

                    if (rule != null && !rule.equals(RuleEnum.NONE)) {
                        currentRule = rule;
                    }

                    if (rule.equals(RuleEnum.REGION)) {

                    } else if (rule.equals(RuleEnum.CITY)) {

                    } else if (rule.equals(RuleEnum.DISTRICT)) {

                    } else {
                        if (currentRule.equals(RuleEnum.REGION)) {
                            regnList.add(contain);
                        } else if (currentRule.equals(RuleEnum.CITY)) {
//                            cityList.add(contain);
                        } else if (currentRule.equals(RuleEnum.DISTRICT)) {
//                            distList.add(contain);
                        } else if (currentRule.equals(RuleEnum.BRANCH)) {
                            branList.add(contain);
                        } else if (currentRule.equals(RuleEnum.NONE)) {

                        }
                    }

                } else {
                    continue;
                }
            }
        }
    }
}