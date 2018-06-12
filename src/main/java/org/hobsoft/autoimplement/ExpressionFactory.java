/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hobsoft.autoimplement;

import java.util.Random;
import java.util.function.Supplier;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;

/**
 * Creates random AST expressions.
 */
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
