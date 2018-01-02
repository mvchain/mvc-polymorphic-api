package com.mvc.ethereum.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 官方包下的转换方法使用了BigInteger, 导致转化后小数被抹去, 这里换成了BigDecimal
 */
public enum Denomination {
    WEI(newBigDecimal(0)),
    SZABO(newBigDecimal(12)),
    FINNEY(newBigDecimal(15)),
    ETHER(newBigDecimal(18));

    private BigDecimal amount;

    private Denomination(BigDecimal value) {
        this.amount = value;
    }

    public BigDecimal value() {
        return this.amount;
    }

    public long longValue() {
        return this.value().longValue();
    }

    private static BigDecimal newBigDecimal(int value) {
        return BigDecimal.valueOf(10L).pow(value);
    }

    public static String toFriendlyString(BigInteger value) {
        BigDecimal decimal = new BigDecimal(value);
        if (decimal.compareTo(ETHER.value()) != 1 && decimal.compareTo(ETHER.value()) != 0) {
            if (decimal.compareTo(FINNEY.value()) != 1 && decimal.compareTo(FINNEY.value()) != 0) {
                return decimal.compareTo(SZABO.value()) != 1 && decimal.compareTo(SZABO.value()) != 0 ? Float.toString(decimal.divide(WEI.value()).floatValue()) + " WEI" : Float.toString(decimal.divide(SZABO.value()).floatValue()) + " SZABO";
            } else {
                return Float.toString(decimal.divide(FINNEY.value()).floatValue()) + " FINNEY";
            }
        } else {
            return Float.toString(decimal.divide(ETHER.value()).floatValue()) + " ETHER";
        }
    }

    public static BigInteger getFriendlyValue(BigDecimal value){
        return value.multiply(ETHER.value()).toBigInteger();
    }

}
