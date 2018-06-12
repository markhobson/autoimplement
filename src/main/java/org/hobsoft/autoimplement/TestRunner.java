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
