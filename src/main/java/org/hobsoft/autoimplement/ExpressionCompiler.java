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
