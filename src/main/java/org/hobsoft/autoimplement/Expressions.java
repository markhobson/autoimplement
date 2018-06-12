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
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;

/**
 * Utilities for working with AST expressions.
 */
public final class Expressions
{
	private Expressions()
	{
		throw new AssertionError();
	}
	
	public static Expression getRandomExpression(Expression exp, Random rand)
	{
		List<Node> nodes = exp.stream().collect(Collectors.toList());
		Node node = nodes.get(rand.nextInt(nodes.size()));
		while (node instanceof SimpleName)
		{
			node = nodes.get(rand.nextInt(nodes.size()));
		}
		return (Expression) node;
	}
}
