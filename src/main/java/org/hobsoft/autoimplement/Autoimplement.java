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
import java.util.stream.Stream;

import org.junit.platform.launcher.listeners.TestExecutionSummary;

import com.github.javaparser.ast.expr.Expression;

import static java.util.stream.Collectors.toList;

/**
 * Writes code so you don't have to.
 * 
 * @param <T> the interface to evolve
 */
public class Autoimplement<T>
{
	private static final int POPULATION_SIZE = 10;
	
	private final ExpressionCompiler<T> expressionCompiler;
	
	private final TestRunner<T> testRunner;
	
	public Autoimplement(Class<T> implementationClass, Class<?> testClass)
	{
		expressionCompiler = new ExpressionCompiler<>();
		testRunner = new TestRunner<>(implementationClass, testClass);
	}
	
	public void evolve()
	{
		List<Expression> population = randomPopulation();
		
		evolve(population);
	}
	
	private List<Expression> randomPopulation()
	{
		return Stream.generate(new ExpressionFactory())
			.limit(POPULATION_SIZE)
			.collect(toList());
	}
	
	private void evolve(List<Expression> population)
	{
		double[] fitness = population.stream()
			.mapToDouble(this::fitness)
			.toArray();
		
		for (int i = 0; i < population.size(); i++)
		{
			System.out.println(population.get(i));
			System.out.println(fitness[i]);
		}
	}
	
	private double fitness(Expression expression)
	{
		T implementation = expressionCompiler.compile(expression);
		TestExecutionSummary summary = testRunner.run(implementation);
		
		return (double) summary.getTestsSucceededCount() / summary.getTestsFoundCount();
	}
}
