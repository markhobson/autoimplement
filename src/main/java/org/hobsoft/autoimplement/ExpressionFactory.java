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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;

/**
 * Creates random AST expressions.
 */
public class ExpressionFactory implements Supplier<Expression>
{
	private static final int MUTATION_COUNT = 10;
	
	private final OperandFactory operandFactory;
	
	private final Mutator mutator;
	
	public ExpressionFactory(OperandFactory operandFactory, Mutator mutator)
	{
		this.operandFactory = operandFactory;
		this.mutator = mutator;
	}
	
	@Override
	public Expression get()
	{
		Expression expression = operandFactory.get();
		
		for (int iteration = 0; iteration < MUTATION_COUNT; iteration++)
		{
			expression = mutator.doMutate(expression);
		}
		
		return expression;
	}
}
