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
	public void canCalculate2Plus2(Calculator calculator)
	{
		assertEquals(4, calculator.add(2, 2));
	}

	@Test
	public void canCalculate4Plus6(Calculator calculator)
	{
		assertEquals(10, calculator.add(4, 6));
	}
}
