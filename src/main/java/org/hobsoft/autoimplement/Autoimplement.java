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

import java.io.PrintStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.platform.launcher.listeners.TestExecutionSummary;

import com.github.javaparser.ast.expr.Expression;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

/**
 * Writes code so you don't have to.
 * 
 * @param <T> the interface to evolve
 */
public class Autoimplement<T>
{
	private static final int POPULATION_SIZE = 10;
	
	private static final int MAX_GENERATIONS = 1000;
	
	private static final double TARGET_FITNESS = 1.0;
	
	private final ExpressionCompiler<T> expressionCompiler;
	
	private final TestRunner<T> testRunner;
	
	private final Random random;
	
	private final Mutator mutator;
	
	public Autoimplement(Class<T> implementationClass, Class<?> testClass)
	{
		expressionCompiler = new ExpressionCompiler<>();
		testRunner = new TestRunner<>(implementationClass, testClass);
		random = new Random();
		mutator = new Mutator(random);
	}
	
	public Optional<String> evolve(PrintStream log)
	{
		List<Expression> population = randomPopulation();
		
		Optional<Expression> winner = Optional.empty();
		int generation = 0;
		
		while (!winner.isPresent() && generation < MAX_GENERATIONS)
		{
			List<Entry<Expression, Double>> populationAndFitness = population.stream()
				.map(individual -> new SimpleEntry<>(individual, fitness(individual)))
				.collect(toList());
			
			Entry<Expression, Double> fittest = populationAndFitness.stream()
				.max(comparingDouble(Entry::getValue))
				.orElseThrow(() -> new IllegalStateException("Empty population"));
			
			log.format("Generation #%d: %s%n", generation + 1, fittest.getKey());
			
			if (fittest.getValue() >= TARGET_FITNESS)
			{
				winner = Optional.of(fittest.getKey());
			}
			else
			{
				population = evolve(populationAndFitness);
				generation++;
			}
		}
		
		return winner.map(expression -> expressionCompiler.source(expression).toString());
	}
	
	private List<Expression> randomPopulation()
	{
		return Stream.generate(new ExpressionFactory())
			.limit(POPULATION_SIZE)
			.collect(toList());
	}
	
	private double fitness(Expression expression)
	{
		T implementation = expressionCompiler.compile(expression);
		TestExecutionSummary summary = testRunner.run(implementation);
		
		return (double) summary.getTestsSucceededCount() / summary.getTestsFoundCount();
	}
	
	private List<Expression> evolve(List<Entry<Expression, Double>> populationAndFitness)
	{
		return Stream.generate(() -> reproduce(populationAndFitness))
			.limit(populationAndFitness.size())
			.collect(toList());
	}
	
	private Expression reproduce(List<Entry<Expression, Double>> populationAndFitness)
	{
		Expression mum = select(populationAndFitness);
		Expression dad = select(populationAndFitness);
		
		return mutator.mutate(crossover(mum, dad));
	}
	
	private Expression select(List<Entry<Expression, Double>> populationAndFitness)
	{
		// Roulette wheel selection
		
		double totalFitness = populationAndFitness
			.stream()
			.mapToDouble(Entry::getValue)
			.sum();
		
		double thresholdFitness = random.nextInt((int) totalFitness + 1);
		double cumulativeFitness = 0;
		
		for (Entry<Expression, Double> entry : populationAndFitness)
		{
			cumulativeFitness += entry.getValue();
			
			if (cumulativeFitness >= thresholdFitness)
			{
				return entry.getKey();
			}
		}
		
		throw new IllegalStateException("No parents found");
	}
	
	private Expression crossover(Expression mum, Expression dad)
	{
		return Crossover.crossover(mum, dad);
	}
}
