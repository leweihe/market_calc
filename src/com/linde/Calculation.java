package com.linde;

import com.linde.object.Market;
import com.linde.object.MarketTree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Calculation {

    private static void putCalcResults(MarketTree m) throws Exception {
        if (m.getPercentage() == null)
            return;
        List<MarketTree> distLevel = adjust(m.getChildMarket(), m.getPercentage());
        Long totalNum = distLevel.stream().mapToLong(x -> x.getTotalNumber().longValue()).sum();
        m.setTotalNumber(new BigDecimal(totalNum));
        m.setActuralCount(m.getPercentage().multiply(m.getTotalNumber()));
        m.setChildMarket(distLevel);
    }

    private static List<MarketTree> adjust(final List<MarketTree> list, final BigDecimal targetPercentage) throws Exception {
        List<MarketTree> result = new ArrayList<>();
        List<MarketTree> ignoreList = list.stream().filter(m -> {
            if (m.getPercentage() == null) {
                return true;
            }
            return m.getPercentage().compareTo(targetPercentage) == 0;

        }).collect(Collectors.toList());
        List<MarketTree> listToCalc = list.stream().filter(m -> {
            if (m.getPercentage() == null) {
                return false;
            }
            return m.getPercentage().compareTo(targetPercentage) != 0;
        }).collect(Collectors.toList());

        if (Calculation.validateData(list, targetPercentage)) {
            result = loopCall(listToCalc, ignoreList, targetPercentage, 0, false);
        } else {
            System.out.println("no records at both side will extend ignore list total amount " + targetPercentage);
            System.out.println("ignore list");
            ignoreList.stream().forEach(System.out::println);
            System.out.println("list to call");
            listToCalc.stream().forEach(System.out::println);

            result = loopCall(listToCalc, ignoreList, targetPercentage, 0, true);
        }

        return result;
    }

    private static List<MarketTree> loopCall(final List<MarketTree> list, final List<MarketTree> ignoreList,
                                             final BigDecimal targetPercentage, int count, final Boolean adjustIgnoreListFlag) {

        BigDecimal currentValue = adjustIgnoreListFlag == true ? calc(Stream.of(list, ignoreList).flatMap(n -> n.stream()).collect(Collectors.toList())) : calc(list);

        Double tmpDouble = currentValue.doubleValue() - targetPercentage.doubleValue();
        if (count >= 2 && adjustIgnoreListFlag) {
            tmpDouble = currentValue.setScale(2, BigDecimal.ROUND_HALF_UP).add(targetPercentage.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(-1))).doubleValue();
        }
        boolean condition = false;
        condition = currentValue.compareTo(targetPercentage) == 0 || Math.abs(tmpDouble) < 0.0001;

        condition |= (count >= 2000 && Math.abs(tmpDouble) <= 0.01);

        if (condition) {
            return Stream.of(list, ignoreList).flatMap(n -> n.stream()).collect(Collectors.toList());
        }

        if (adjustIgnoreListFlag) {
            ignoreList.forEach(m -> {
                m.setTotalNumber(m.getTotalNumber().multiply(new BigDecimal(100)));
                m.setActuralCount(m.getPercentage().multiply(m.getTotalNumber()));
                if (m.getChildMarket().size() > 0) {
                    m.getChildMarket().forEach(n -> {
                        n.setTotalNumber(n.getTotalNumber().multiply(new BigDecimal(100)));
                        n.setActuralCount(n.getPercentage().multiply(n.getTotalNumber()));
                        if (n.getChildMarket().size() > 0) {
                            n.getChildMarket().forEach(o -> {
                                o.setTotalNumber(o.getTotalNumber().multiply(new BigDecimal(100)));
                                o.setActuralCount(o.getPercentage().multiply(o.getTotalNumber()));
                            });
                        }
                    });
                }
            });
        } else {
            List<MarketTree> biggerList = list.stream().filter(m -> {
                if (m.getPercentage() == null) {
                    return false;
                }
                return m.getPercentage().compareTo(targetPercentage) > 0;
            }).collect(Collectors.toList());
            List<MarketTree> smallerList = list.stream().filter(m -> {
                if (m.getPercentage() == null) {
                    return false;
                }
                return m.getPercentage().compareTo(targetPercentage) < 0;
            }).collect(Collectors.toList());
            BigDecimal param = new BigDecimal(Math.abs(tmpDouble) > 0.001 ? 10 : 1);

            if (currentValue.compareTo(targetPercentage) > 0) {
                smallerList.forEach(m -> {
                    m.setTotalNumber(m.getTotalNumber().add(param));
                });
            } else {
                biggerList.forEach(m -> {
                    m.setTotalNumber(m.getTotalNumber().add(param));
                });
            }
            list.clear();
            list.addAll(biggerList);
            list.addAll(smallerList);
        }
        count += 1;
        return loopCall(list, ignoreList, targetPercentage, count, adjustIgnoreListFlag);

    }

    private static BigDecimal calc(final List<MarketTree> list) {
        BigDecimal totalNumber = new BigDecimal(0);
        BigDecimal actualNumber = new BigDecimal(0);
        for (MarketTree m : list) {
            if (m.getPercentage() == null)
                continue;
            totalNumber = totalNumber.add(m.getTotalNumber());
            actualNumber = actualNumber.add(m.getTotalNumber().multiply(m.getPercentage()));
        }
        BigDecimal result = actualNumber.divide(totalNumber, 3, BigDecimal.ROUND_HALF_UP);
        return result;
    }

    public static boolean validateData(final List<MarketTree> list, final BigDecimal targetPercentage) {
        List<Market> biggerList = list.stream().filter(m -> {
            if (m.getPercentage() == null) {
                return false;
            }
            return m.getPercentage().compareTo(targetPercentage) > 0;
        }).collect(Collectors.toList());
        List<Market> smallerList = list.stream().filter(m -> {
            if (m.getPercentage() == null) {
                return false;
            }
            return m.getPercentage().compareTo(targetPercentage) < 0;
        }).collect(Collectors.toList());
        return (biggerList.size() == 0 && smallerList.size() == 0) || (biggerList.size() > 0 && smallerList.size() > 0);
    }

    public static List<MarketTree> doCalculate(final List<MarketTree> list) throws Exception {
        List<MarketTree> result = new ArrayList<>(list);

        for (MarketTree bran : list) {
            if (RuleEnum.BRANCH.equals(bran.getRule())) {
                for (MarketTree region : bran.getChildMarket()) {
                    if (RuleEnum.REGION.equals(region.getRule())) {
                        for (MarketTree dist : region.getChildMarket()) {
                            if (!RuleEnum.DISTRICT.equals(dist.getRule())) {
                                putCalcResults(dist);
                            }
                        }
                        putCalcResults(region);
                    }
                }
                putCalcResults(bran);
            }
        }
        return result;
    }

}
