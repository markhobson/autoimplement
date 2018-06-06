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

import java.util.Optional;

import org.hobsoft.autoimplement.example.Calculator;
import org.hobsoft.autoimplement.example.CalculatorTest;

/**
 * Runs Autoimplement against the calculator example.
 */
public class Main
{
	public static void main(String[] args)
	{
		Autoimplement<Calculator> autoimplement = new Autoimplement<>(Calculator.class, CalculatorTest.class);
		Optional<String> source = autoimplement.evolve();
		
		System.out.format(source.map(s -> "%nWinner!%n").orElse("%nNo winner found :(%n"));
		source.ifPresent(s -> System.out.format("%n%s%n", s));
	}
}
