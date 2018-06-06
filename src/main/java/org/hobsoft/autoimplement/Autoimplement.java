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

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import org.hobsoft.autoimplement.example.Calculator;
import org.joor.Reflect;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import static java.util.stream.Collectors.toList;

import static com.github.javaparser.ast.Modifier.PUBLIC;
import static com.github.javaparser.ast.type.PrimitiveType.intType;

/**
 * Writes code so you don't have to.
 * 
 * @param <T> the interface to evolve
 */
public class Autoimplement<T>
{
	private static final int POPULATION_SIZE = 10;
	
	private final TestRunner<T> testRunner;
	
	private int implCount;
	
	public Autoimplement(Class<T> implementationClass, Class<?> testClass)
	{
		testRunner = new TestRunner<>(implementationClass, testClass);
		implCount = 0;
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
			.map(this::createImplementation)
			.mapToDouble(this::fitness)
			.toArray();
		
		for (int i = 0; i < population.size(); i++)
		{
			System.out.println(population.get(i));
			System.out.println(fitness[i]);
		}
	}
	
	private double fitness(T implementation)
	{
		TestExecutionSummary summary = testRunner.run(implementation);
		
		return (double) summary.getTestsSucceededCount() / summary.getTestsFoundCount();
	}
	
	private T createImplementation(Expression expression)
	{
		String name = String.format("CalculatorImpl%d", implCount++);
		return compile(createImplementationUnit(name, expression));
	}
	
	private static CompilationUnit createImplementationUnit(String name, Expression expression)
	{
		CompilationUnit unit = new CompilationUnit();
		
		ClassOrInterfaceDeclaration type = unit.addClass(name);
		type.addImplementedType(Calculator.class);
		
		MethodDeclaration method = new MethodDeclaration(EnumSet.of(PUBLIC), intType(), "add");
		method.addParameter(intType(), "x");
		method.addParameter(intType(), "y");
		type.addMember(method);
		
		BlockStmt body = new BlockStmt();
		body.addStatement(new ReturnStmt(expression));
		method.setBody(body);
		
		return unit;
	}
	
	private T compile(CompilationUnit unit)
	{
		String name = unit.getTypes()
			.stream()
			.findFirst()
			.map(NodeWithSimpleName::getNameAsString)
			.orElseThrow(() -> new IllegalArgumentException("Missing type"));
		
		return Reflect.compile(name, unit.toString())
			.create()
			.get();
	}
}
