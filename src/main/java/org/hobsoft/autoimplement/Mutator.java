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
import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;

import static org.hobsoft.autoimplement.Expressions.findRandomExpression;
import static org.hobsoft.autoimplement.Expressions.replaceOrReroot;

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
	
	private final OperandFactory operandFactory;
	
	private final Random random;
	
	public Mutator(OperandFactory operandFactory, Random random)
	{
		this.operandFactory = operandFactory;
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
	
	Expression doMutate(Expression root)
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
				root = removeNode(root);
				break;
		}
		
		return root;
	}
	
	private Expression changeNode(Expression root)
	{
		Expression node = findRandomExpression(root, random);
		
		if (node.isBinaryExpr())
		{
			changeOperator(node.asBinaryExpr());
		}
		else
		{
			root = replaceOrReroot(root, node, randomOperand());
		}
		
		return root;
	}
	
	private void changeOperator(BinaryExpr operator)
	{
		operator.setOperator(randomElement(OPERATORS));
	}
	
	private Expression addNode(Expression root)
	{
		Expression node = findRandomExpression(root, random);
		
		BinaryExpr newNode = randomOperator();
		newNode.setLeft(node.clone());
		newNode.setRight(randomOperand());
		
		if (random.nextBoolean())
		{
			swapChildren(newNode);
		}
		
		root = replaceOrReroot(root, node, newNode);
		
		return root;
	}
	
	private Expression removeNode(Expression root)
	{
		Expression node = findRandomExpression(root, random);
		
		if (node.isBinaryExpr())
		{
			Expression newNode = randomChild(node.asBinaryExpr());
			
			root = replaceOrReroot(root, node, newNode);
		}
		else if (node != root)
		{
			BinaryExpr parent = getParentOperator(node);
			Expression sibling = parent.getLeft() == node ? parent.getRight() : parent.getLeft();
			
			root = replaceOrReroot(root, parent, sibling);
		}
		
		return root;
	}
	
	private static BinaryExpr getParentOperator(Node node)
	{
		return node.getParentNode()
			.map(parent -> ((Expression) parent).asBinaryExpr())
			.orElseThrow(() -> new IllegalStateException("No parent"));
	}
	
	private static void swapChildren(BinaryExpr operator)
	{
		Expression left = operator.getLeft();
		operator.setLeft(operator.getRight());
		operator.setRight(left);
	}
	
	private Expression randomOperand()
	{
		return operandFactory.get();
	}
	
	private BinaryExpr randomOperator()
	{
		BinaryExpr operator = new BinaryExpr();
		operator.setOperator(randomElement(OPERATORS));
		return operator;
	}
	
	private Expression randomChild(BinaryExpr operator)
	{
		return random.nextBoolean() ? operator.getLeft() : operator.getRight();
	}
	
	private <T> T randomElement(Collection<T> collection)
	{
		int index = random.nextInt(collection.size());
		return new ArrayList<>(collection).get(index);
	}
}
