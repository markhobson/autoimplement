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
package org.hobsoft.autoimplement.example;

import org.hobsoft.autoimplement.AutoimplementExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Example behaviour desired of the evolved implementation.
 */
@ExtendWith(AutoimplementExtension.class)
public class CalculatorTest
{
	@Test
	public void canCalculateAddition1(Calculator calculator)
	{
		assertEquals(1113914, calculator.add(926410, 187504));
	}

	@Test
	public void canCalculateAddition2(Calculator calculator)
	{
		assertEquals(1692649, calculator.add(707069, 985580));
	}
}
