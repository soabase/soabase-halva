/**
 * Copyright 2016 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.soabase.halva.caseclass;

import org.junit.Assert;
import org.junit.Test;

import static io.soabase.halva.caseclass.ChildBeanCase.ChildBeanCase;
import static io.soabase.halva.caseclass.ParentBeanCase.ParentBeanCase;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;

public class TestBeanValidation
{
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @CaseClass(beanValidation = true)
    interface ChildBean
    {
        @NotNull
        @Size(min = 6, max = 12)
        String name();

        @Min(5)
        @Max(10)
        int quantity();
    }

    @CaseClass(beanValidation = true)
    interface ParentBean
    {
        @NotNull
        @Valid
        ChildBean child();
    }

    @Test
    public void testBeanValidationPasses()
    {
        ParentBean parent = ParentBeanCase(ChildBeanCase("tester", 10));
        Set<ConstraintViolation<ParentBean>> violations = validator.validate(parent);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void testBeanValidationFails()
    {
        ParentBean parent = ParentBeanCase(ChildBeanCase("A very long name", 4));
        Set<ConstraintViolation<ParentBean>> violations = validator.validate(parent);

        Assert.assertEquals(violations.size(), 2);
    }
}
