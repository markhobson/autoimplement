package org.hobsoft.autoimplement;

import java.util.Random;
import java.util.function.Supplier;

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
