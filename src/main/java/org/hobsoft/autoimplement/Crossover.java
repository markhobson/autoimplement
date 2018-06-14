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

import com.github.javaparser.ast.expr.Expression;

import static org.hobsoft.autoimplement.Expressions.findRandomExpression;
import static org.hobsoft.autoimplement.Expressions.replaceOrReroot;

/**
 * Breeds AST expressions.
 */
public class Crossover
{
	private final Random random;
	
	public Crossover(Random random)
	{
		this.random = random;
	}
	
	public Expression crossover(Expression mum, Expression dad)
	{
		if (random.nextBoolean())
		{
			Expression oldMum = mum;
			mum = dad;
			dad = oldMum;
		}
		
		mum = mum.clone();
		dad = dad.clone();
		
		Expression baby = mum;
		
		Expression mumNode = findRandomExpression(mum, random);
		Expression dadNode = findRandomExpression(dad, random);
		baby = replaceOrReroot(baby, mumNode, dadNode);
		
		return baby;
	}
}
