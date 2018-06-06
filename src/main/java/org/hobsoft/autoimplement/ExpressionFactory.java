package org.hobsoft.autoimplement;

import java.util.Random;
import java.util.function.Supplier;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class ExpressionFactory implements Supplier<Expression>
{
	private final Random random;
	
	public ExpressionFactory()
	{
		random = new Random();
	}
	
	@Override
	public Expression get()
	{
		BinaryExpr expression = new BinaryExpr();
		expression.setOperator(randomOperator());
		expression.setLeft(randomOperand());
		expression.setRight(randomOperand());
		return expression;
	}
	
	private Operator randomOperator()
	{
		double rand = random.nextDouble();
		if (rand < 0.25)
		{
			return Operator.PLUS;
		}
		else if (rand < 0.5)
		{
			return Operator.MINUS;
		}
		else if (rand < 0.75)
		{
			return Operator.MULTIPLY;
		}
		else
		{
			return Operator.DIVIDE;
		}
	}
	
	private Expression randomOperand()
	{
		return (random.nextDouble() < 0.5)
			? randomLiteral()
			: randomName();
	}
	
	private LiteralExpr randomLiteral()
	{
		return new IntegerLiteralExpr(random.nextInt(10));
	}
	
	private Expression randomName()
	{
		return (random.nextDouble() < 0.5)
			? new NameExpr("x")
			: new NameExpr("y");
	}
}
