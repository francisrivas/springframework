/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.expression.spel.ast;

import java.math.BigDecimal;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.NumberUtils;

/**
 * Implements division operator.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class OpDivide extends Operator {

	public OpDivide(int pos, SpelNodeImpl... operands) {
		super("/", pos, operands);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Object operandOne = getLeftOperand().getValueInternal(state).getValue();
		Object operandTwo = getRightOperand().getValueInternal(state).getValue();
		if (operandOne instanceof Number && operandTwo instanceof Number) {
			Number op1 = (Number) operandOne;
			Number op2 = (Number) operandTwo;
			
			 if ( op1 instanceof BigDecimal || op2 instanceof BigDecimal ) {
	                BigDecimal op1BD = NumberUtils.convertNumberToTargetClass(op1, BigDecimal.class);
	                BigDecimal op2BD = NumberUtils.convertNumberToTargetClass(op2, BigDecimal.class);
	                int scale = Math.max(op1BD.scale(), op2BD.scale());
	                scale = scale > 2 ? scale : 3;
	                // set scale of the max of the two operands in case zero set default 2
	                return new TypedValue(op1BD.divide(op2BD, scale, BigDecimal.ROUND_HALF_UP));
	        } else if (op1 instanceof Double || op2 instanceof Double) {
				return new TypedValue(op1.doubleValue() / op2.doubleValue());
			} else if (op1 instanceof Long || op2 instanceof Long) {
				return new TypedValue(op1.longValue() / op2.longValue());
			} else { // TODO what about non-int result of the division?
				return new TypedValue(op1.intValue() / op2.intValue());
			}
		}
		Object result = state.operate(Operation.DIVIDE, operandOne, operandTwo);
		return new TypedValue(result);
	}

}
