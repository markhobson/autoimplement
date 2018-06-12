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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;

import static org.hobsoft.autoimplement.Expressions.getRandomExpression;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.DIVIDE;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.MINUS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.MULTIPLY;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.PLUS;

/**
 * Mutates AST expressions.
 */
public class Mutator
{
	private static final double MUTATION_RATE = 0.1;
	
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
	
	private final Random random;
	
	public Mutator(Random random)
	{
		this.random = random;
	}
	
	public Expression mutate(Expression expression)
	{
		if (random.nextDouble() < MUTATION_RATE)
		{
			expression = doMutate(expression);
		}
		
		return expression;
	}
	
	private Expression doMutate(Expression expression)
	{
		switch (random.nextInt(3))
		{
			case 0:
				expression = changeNode(expression);
				break;
			
			case 1:
				expression = addOperatorOperand(expression);
				break;
			
			case 2:
				expression = removeOperatorOperand(expression);
				break;
		}
		
		return expression;
	}
	
	private Expression changeNode(Expression exp)
	{
		Expression randExp = getRandomExpression(exp, random);
		if (randExp.isBinaryExpr())
		{
			randExp.asBinaryExpr().setOperator(randomElement(OPERATORS));
		}
		else
		{
			if (randExp.findRootNode().equals(randExp))
			{
				randExp = exp;
			}
			else
			{
				if (random.nextDouble() > 0.5)
				{
					Expression parent = (Expression) randExp.getParentNode().orElseThrow(IllegalStateException::new);
					parent.asBinaryExpr().setLeft(randomElement(OPERANDS));
				}
				else
				{
					Expression parent = (Expression) randExp.getParentNode().orElseThrow(IllegalStateException::new);
					parent.asBinaryExpr().setRight(randomElement(OPERANDS));
				}
			}
		}
		return randExp;
	}
	
	private Expression addOperatorOperand(Expression exp)
	{
		Expression randExp = getRandomExpression(exp, random);
		BinaryExpr newExp = new BinaryExpr();
		newExp.setOperator(randomElement(OPERATORS));
		if (random.nextDouble() > 0.5)
		{
			newExp.setLeft(randomElement(OPERANDS));
			newExp.setRight(randExp.clone());
		}
		else
		{
			newExp.setLeft(randExp.clone());
			newExp.setRight(randomElement(OPERANDS));
		}
		randExp.replace(newExp);
		return randExp;
	}
	
	private Expression removeOperatorOperand(Expression exp)
	{
		Expression randExp = getRandomExpression(exp, random);
		
		if (!randExp.findRootNode().equals(randExp))
		{
			if (randExp.isBinaryExpr())
			{
				if (random.nextDouble() > 0.5)
				{
					randExp.replace(randExp.asBinaryExpr().getLeft());
				}
				else
				{
					randExp.replace(randExp.asBinaryExpr().getRight());
				}
			}
			else
			{
				Expression parent = (Expression) randExp.getParentNode()
					.orElseThrow(IllegalStateException::new);
				
				if (parent.asBinaryExpr().getLeft().equals(randExp))
				{
					parent.replace(parent.asBinaryExpr().getRight());
				}
				else
				{
					parent.replace(parent.asBinaryExpr().getLeft());
				}
			}
		}
		return randExp;
	}
	
	private <T> T randomElement(Collection<T> collection)
	{
		int index = random.nextInt(collection.size());
		return new ArrayList<>(collection).get(index);
	}
}
