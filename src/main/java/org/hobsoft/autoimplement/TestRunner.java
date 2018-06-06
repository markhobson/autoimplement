package org.hobsoft.autoimplement;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

public class TestRunner<T>
{
	private final Class<T> implementationClass;
	
	private final Class<?> testClass;
	
	private final Launcher launcher;
	
	public TestRunner(Class<T> implementationClass, Class<?> testClass)
	{
		this.implementationClass = implementationClass;
		this.testClass = testClass;
		
		launcher = LauncherFactory.create();
	}
	
	public TestExecutionSummary run(T implementation)
	{
		AutoimplementExtension.setImplementation(implementationClass, implementation);
		
		SummaryGeneratingListener listener = new SummaryGeneratingListener();
		launcher.registerTestExecutionListeners(listener);
		launcher.execute(request().selectors(selectClass(testClass)).build());
		
		return listener.getSummary();
	}
}
