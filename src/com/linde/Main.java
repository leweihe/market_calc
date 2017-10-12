package com.linde;

import com.linde.object.MarketTree;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

//    public static void main1(final String[] args) {
//        System.out.println("****************calc 1**********************");
//        List<Market> list = new ArrayList<Market>();
//        list.add(new Market("BE", new BigDecimal(100), new BigDecimal("75")));
//        list.add(new Market("BH", new BigDecimal(100), new BigDecimal("73")));
//        list.add(new Market("BT", new BigDecimal(100), new BigDecimal("75")));
//        list.add(new Market("BW", new BigDecimal(100), new BigDecimal("65")));
//        list.add(new Market("BX", new BigDecimal(100), new BigDecimal("72")));
//        BigDecimal targetPercentage = new BigDecimal("70");
//        if (Calculation.validateData(list, targetPercentage)) {
//            List<Market> ignoreList = list.stream().filter(m -> m.getPercentage().compareTo(targetPercentage) == 0).collect(Collectors.toList());
//            list = list.stream().filter(m -> m.getPercentage().compareTo(targetPercentage) != 0).collect(Collectors.toList());
//            if (list.size() == 0) {
//
//            } else {
//                List<Market> result = Calculation.adjust(list, ignoreList, targetPercentage);
//                result.stream().forEach(System.out::println);
//            }
//        } else {
//            System.out.println("no possible");
//        }
//
//        System.out.println("****************calc 2**********************");
//        list = new ArrayList<Market>();
//        list.add(new Market("QD", new BigDecimal(100), new BigDecimal("60")));
//        list.add(new Market("QH", new BigDecimal(100), new BigDecimal("73")));
//        list.add(new Market("QJ", new BigDecimal(100), new BigDecimal("77")));
//        list.add(new Market("QW", new BigDecimal(100), new BigDecimal("75")));
//        list.add(new Market("QY", new BigDecimal(100), new BigDecimal("66")));
//        BigDecimal targetPercentage2 = new BigDecimal("72");
//        if (Calculation.validateData(list, targetPercentage2)) {
//            List<Market> ignoreList = list.stream().filter(m -> m.getPercentage().compareTo(targetPercentage2) == 0).collect(Collectors.toList());
//            list = list.stream().filter(m -> m.getPercentage().compareTo(targetPercentage2) != 0).collect(Collectors.toList());
//            if (list.size() == 0) {
//
//            } else {
//                List<Market> result = Calculation.adjust(list, ignoreList, targetPercentage2);
//                result.stream().forEach(System.out::println);
//            }
//        } else {
//            System.out.println("no possible");
//        }
//
//        System.out.println("****************calc 3**********************");
//        list = new ArrayList<Market>();
//        list.add(new Market("ZA", new BigDecimal(100), new BigDecimal("69")));
//        list.add(new Market("ZE", new BigDecimal(100), new BigDecimal("72")));
//        list.add(new Market("ZK", new BigDecimal(100), new BigDecimal("68")));
//        list.add(new Market("ZN", new BigDecimal(100), new BigDecimal("71")));
//        list.add(new Market("ZT", new BigDecimal(100), new BigDecimal("70")));
//        list.add(new Market("ZW", new BigDecimal(100), new BigDecimal("67")));
//        list.add(new Market("ZX", new BigDecimal(100), new BigDecimal("73")));
//        list.add(new Market("ZZ", new BigDecimal(100), new BigDecimal("70")));
//        BigDecimal targetPercentage3 = new BigDecimal("70");
//        if (Calculation.validateData(list, targetPercentage3)) {
//            List<Market> ignoreList = list.stream().filter(m -> m.getPercentage().compareTo(targetPercentage3) == 0).collect(Collectors.toList());
//            list = list.stream().filter(m -> m.getPercentage().compareTo(targetPercentage3) != 0).collect(Collectors.toList());
//            if (list.size() == 0) {
//
//            } else {
//                List<Market> result = Calculation.adjust(list, ignoreList, targetPercentage3);
//                result.stream().forEach(System.out::println);
//
//            }
//        } else {
//            System.out.println("no possible");
//        }
//
//        System.out.println("****************lv 2 1**********************");
//        list = new ArrayList<Market>();
//        list.add(new Market("V1", new BigDecimal(100), new BigDecimal("71.8")));
//        list.add(new Market("V2", new BigDecimal(100), new BigDecimal("57")));
//        list.add(new Market("V3", new BigDecimal(100), new BigDecimal("85")));
//        BigDecimal targetPercentage4 = new BigDecimal("71");
//        if (Calculation.validateData(list, targetPercentage4)) {
//            List<Market> ignoreList = list.stream().filter(m -> m.getPercentage().compareTo(targetPercentage4) == 0).collect(Collectors.toList());
//            list = list.stream().filter(m -> m.getPercentage().compareTo(targetPercentage4) != 0).collect(Collectors.toList());
//            if (list.size() == 0) {
//
//            } else {
//                List<Market> result = Calculation.adjust(list, ignoreList, targetPercentage4);
//                result.stream().forEach(System.out::println);
//
//            }
//        } else {
//            System.out.println("no possible");
//        }
//    }

    public static void main(String[] args) {
        ReadWriteExcelFile excelFileHelper = new ReadWriteExcelFile();
        List<MarketTree> data;


        String inputFolder = "C:\\Users\\cn40580\\Desktop\\BATCH\\";
        String outputFolder = "C:\\Users\\cn40580\\Desktop\\BATCH_OUT\\";


        File f = new File(inputFolder);

        if (f.isDirectory()) {
            for (int i = 0; i < f.list().length; i++) {
                String fileName = f.list()[i];
                try {
                    if(fileName.contains("ruler"))
                        continue;
                    data = excelFileHelper.readXLSXFile(inputFolder, fileName);

                    List<MarketTree> finalResult = Calculation.doCalculate(data);
                    excelFileHelper.writeXLSXFile(finalResult, outputFolder + "OUTPUT_" + fileName);

                } catch (IOException e) {
                    System.out.println("file : " + fileName + "throws exception");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("file : " + fileName + " throws exception");
                    e.printStackTrace();
                }
            }
        }

    }
}
