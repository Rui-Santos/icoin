package com.icoin.trading.tradeengine;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Description;
import org.joda.money.BigMoney;
import org.joda.money.Money;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-21
 * Time: PM8:14
 * To change this template use File | Settings | File Templates.
 */
public class EqualsWithMoneyFieldMatcher<T> extends GenericMatcher<T> {
    protected T expected;

    public EqualsWithMoneyFieldMatcher(T expected) {
        super((Class<T>) expected.getClass());
        this.expected = expected;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("Expected %s.\n", ToStringBuilder.reflectionToString(expected, ToStringStyle.MULTI_LINE_STYLE)));
    }

    @Override
    public boolean doMatches(Object actual) {
        final ArrayList<String> excludes = Lists.newArrayList();

        Field[] fields = expected.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Class s = field.getType();
            if (BigMoney.class.isAssignableFrom(s)) {
                excludes.add(field.getName());
            } else if (Money.class.isAssignableFrom(s)) {
                excludes.add(field.getName());
            } else if (BigDecimal.class.isAssignableFrom(s)) {
                excludes.add(field.getName());
            }
        }


        return EqualsBuilder.reflectionEquals(expected, actual, excludes) && isEqual(expected, actual, excludes);
    }

    private boolean isEqual(T expected, Object actual, ArrayList<String> comparedFields) {
        for (String comparedField : comparedFields) {
            Field field = FieldUtils.getDeclaredField(expected.getClass(), comparedField, true);

            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            try {
                final Object expectedObject = field.get(expected);
                final Object actualObject = field.get(actual);

                if (BigMoney.class.isAssignableFrom(expectedObject.getClass())) {
                    BigMoney expectedValue = (BigMoney) expectedObject;
                    BigMoney actualValue = (BigMoney) actualObject;
                    if (!expectedValue.isEqual(actualValue)) return false;
                } else if (Money.class.isAssignableFrom(expectedObject.getClass())) {
                    Money expectedValue = (Money) expectedObject;
                    Money actualValue = (Money) actualObject;
                    if (!expectedValue.isEqual(actualValue)) return false;
                } else if (BigDecimal.class.isAssignableFrom(expectedObject.getClass())) {
                    BigDecimal expectedValue = (BigDecimal) expectedObject;
                    BigDecimal actualValue = (BigDecimal) actualObject;
                    if (expectedValue.compareTo(actualValue) != 0) return false;
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }

        }


        return true;
    }


}
