package org.hobsoft.autoimplement;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.DIVIDE;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.MINUS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.MULTIPLY;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.PLUS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;

public class Mutator {

    private static final Double MUTATION_CHANCE = 0.05;

    private static final Set<BinaryExpr.Operator> OPERATORS = EnumSet.of(PLUS, MINUS, MULTIPLY, DIVIDE);

    private static final Set<Expression> OPERANDS = new HashSet<>(Arrays.asList(
            new IntegerLiteralExpr(0),
            new IntegerLiteralExpr(1),
            new IntegerLiteralExpr(2),
            new IntegerLiteralExpr(3),
            new IntegerLiteralExpr(4),
            new IntegerLiteralExpr(5),
            new IntegerLiteralExpr(6),
            new IntegerLiteralExpr(7),
            new IntegerLiteralExpr(8),
            new IntegerLiteralExpr(9),
            new NameExpr("x"),
            new NameExpr("y")
    ));

    private static Random rand = new Random();

    public static Expression mutate(Expression exp) {
        double val = Math.random();
        if (val < MUTATION_CHANCE) {
            return changeNode(exp);
        } else if (val < MUTATION_CHANCE * 2) {
            return addOperatorOperand(exp);
        } else if (val < MUTATION_CHANCE * 3) {
            return removeOperatorOperand(exp);
        } else {
            return exp;
        }
    }

    private static Expression removeOperatorOperand(Expression exp) {
        Expression randExp = getRandomExpression(exp);

        if (!randExp.findRootNode().equals(randExp)) {
            if (randExp.isBinaryExpr()) {
                if (rand.nextDouble() > 0.5) {
                    randExp.replace(randExp.asBinaryExpr().getLeft());
                } else {
                    randExp.replace(randExp.asBinaryExpr().getRight());
                }
            } else {
                Expression parent = (Expression) randExp.getParentNode()
                    .orElseThrow(IllegalStateException::new);

                if (parent.asBinaryExpr().getLeft().equals(randExp)) {
                    parent.replace(parent.asBinaryExpr().getRight());
                } else {
                    parent.replace(parent.asBinaryExpr().getLeft());
                }
            }
        }
        return randExp;
    }

    private static Expression addOperatorOperand(Expression exp) {
        Expression randExp = getRandomExpression(exp);
        BinaryExpr newExp = new BinaryExpr();
        newExp.setOperator(randomElement(OPERATORS));
        if (rand.nextDouble() > 0.5) {
            newExp.setLeft(randomElement(OPERANDS));
            newExp.setRight(randExp.clone());
        } else {
            newExp.setLeft(randExp.clone());
            newExp.setRight(randomElement(OPERANDS));
        }
        randExp.replace(newExp);
        return randExp;
    }

    private static Expression changeNode(Expression exp) {
        Expression randExp = getRandomExpression(exp);
        if (randExp.isBinaryExpr()) {
            randExp.asBinaryExpr().setOperator(randomElement(OPERATORS));
        } else {
            if (rand.nextDouble() > 0.5) {
                Expression parent = (Expression) randExp.getParentNode().orElseThrow(IllegalStateException::new);
                parent.asBinaryExpr().setLeft(randomElement(OPERANDS));
            } else {
                Expression parent = (Expression) randExp.getParentNode().orElseThrow(IllegalStateException::new);
                parent.asBinaryExpr().setLeft(randomElement(OPERANDS));
            }
        }
        return randExp;
    }

    public static Expression getRandomExpression(Expression exp) {
        List<Node> nodes = exp.stream().collect(Collectors.toList());
        Node node = nodes.get(rand.nextInt(nodes.size()));
        while (node instanceof SimpleName) {
            node = nodes.get(rand.nextInt(nodes.size()));
        }
        return (Expression) node;
    }

    private static <T> T randomElement(Collection<T> collection) {
        int index = rand.nextInt(collection.size());
        return new ArrayList<>(collection).get(index);
    }
}