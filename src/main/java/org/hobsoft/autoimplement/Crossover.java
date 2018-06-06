package org.hobsoft.autoimplement;

import static org.hobsoft.autoimplement.Mutator.getRandomExpression;

import com.github.javaparser.ast.expr.Expression;

public class Crossover {
    public static Expression crossover(Expression mum, Expression dad) {
        Expression baby;

        if (Math.random() > 0.5) {
            baby = mum.clone();
            Expression mumExp = getRandomExpression(baby);
            Expression dadExp = getRandomExpression(dad);
            baby.replace(mumExp, dadExp);
        } else {
            baby = dad.clone();
            Expression dadExp = getRandomExpression(baby);
            Expression mumExp = getRandomExpression(mum);
            baby.replace(dadExp, mumExp);
        }
        return baby;
    }
}