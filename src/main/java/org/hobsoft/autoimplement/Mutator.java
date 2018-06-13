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

import com.github.javaparser.ast.Node;
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
	
	public Expression mutate(Expression root)
	{
		if (random.nextDouble() < MUTATION_RATE)
		{
			root = doMutate(root);
		}
		
		return root;
	}
	
	private Expression doMutate(Expression root)
	{
		switch (random.nextInt(3))
		{
			case 0:
				root = changeNode(root);
				break;
			
			case 1:
				root = addNode(root);
				break;
			
			case 2:
				root = removeOperatorOperand(root);
				break;
		}
		
		return root;
	}
	
	private Expression changeNode(Expression root)
	{
		Expression node = getRandomExpression(root, random);
		
		if (node.isBinaryExpr())
		{
			changeOperator(node.asBinaryExpr());
		}
		else if (isRoot(node))
		{
			root = randomOperand();
		}
		else
		{
			if (!node.replace(randomOperand()))
			{
				throw new IllegalStateException("Cannot replace node");
			}
		}
		
		return root;
	}
	
	private void changeOperator(BinaryExpr operator)
	{
		operator.setOperator(randomElement(OPERATORS));
	}
	
	private Expression addNode(Expression root)
	{
		Expression node = getRandomExpression(root, random);
		
		BinaryExpr newNode = randomOperator();
		newNode.setLeft(node.clone());
		newNode.setRight(randomOperand());
		
		if (random.nextBoolean())
		{
			swapChildren(newNode);
		}
		
		node.replace(newNode);
		
		return root;
	}
	
	private Expression removeOperatorOperand(Expression exp)
	{
		Expression randExp = getRandomExpression(exp, random);
		
		if (!isRoot(randExp))
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
		return exp;
	}
	
	private static boolean isRoot(Node node)
	{
		return !node.getParentNode().isPresent();
	}
	
	private static void swapChildren(BinaryExpr operator)
	{
		Expression left = operator.getLeft();
		operator.setLeft(operator.getRight());
		operator.setRight(left);
	}
	
	private Expression randomOperand()
	{
		return randomElement(OPERANDS);
	}
	
	private BinaryExpr randomOperator()
	{
		BinaryExpr operator = new BinaryExpr();
		operator.setOperator(randomElement(OPERATORS));
		return operator;
	}
	
	private <T> T randomElement(Collection<T> collection)
	{
		int index = random.nextInt(collection.size());
		return new ArrayList<>(collection).get(index);
	}
}
