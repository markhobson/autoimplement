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

import org.hobsoft.autoimplement.example.Calculator;
import org.joor.Reflect;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import static com.github.javaparser.ast.Modifier.PUBLIC;
import static com.github.javaparser.ast.type.PrimitiveType.intType;

/**
 * Compiles AST expressions to object instances.
 * 
 * @param <T> the type to compile to
 */
public class ExpressionCompiler<T>
{
	private int implCount;
	
	public ExpressionCompiler()
	{
		implCount = 0;
	}
	
	public CompilationUnit source(Expression expression)
	{
		CompilationUnit unit = new CompilationUnit();
		
		String name = String.format("CalculatorImpl%d", implCount++);
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

	public T compile(Expression expression)
	{
		return compile(source(expression));
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
