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

import java.util.List;
import java.util.Random;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;

/**
 * Utilities for working with AST expressions.
 */
final class Expressions
{
	private Expressions()
	{
		throw new AssertionError();
	}
	
	static Expression findRandomExpression(Expression root, Random random)
	{
		List<Expression> expressions = root.findAll(Expression.class);
		return expressions.get(random.nextInt(expressions.size()));
	}
	
	static Expression replaceOrReroot(Expression root, Node node, Expression newNode)
	{
		if (node == root)
		{
			return newNode;
		}
		
		strictReplace(node, newNode);
		
		return root;
	}
	
	private static void strictReplace(Node node, Node newNode)
	{
		if (!node.replace(newNode))
		{
			throw new IllegalStateException("Cannot replace node");
		}
	}
}
