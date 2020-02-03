package com.wy.common.util;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author wangyi
 */
public class SpelHelper {

    private static ConcurrentHashMap<String, Expression> m = new ConcurrentHashMap<>();

    /**
     * 动态执行
     *
     * @param exp
     * @param values
     * @param <T>
     * @return
     */
    public static <T> T exec(String exp, Map<String, Object> values) {
        Expression expression;
        if (m.contains(exp)) {
            expression = m.get(exp);
        } else {
            expression = new SpelExpressionParser().parseExpression(exp);
            m.put(exp, expression);
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(values);
        return (T) expression.getValue(context);
    }
}
