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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

public class TestValidation
{
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @CaseClass(json = true, validate = true)
    interface ChildBean
    {
        @NotNull
        @Size(min = 6, max = 12)
        String name();

        @Min(5)
        @Max(10)
        @JsonIgnore // not a repeatable annotation type
        int quantity();
    }

    @CaseClass(json = true, validate = true)
    interface ParentBean
    {
        @NotNull
        @Valid
        @JsonProperty("child") // not a repeatable annotation type
        ChildBean child();
    }

    @Test
    public void testValidationPasses()
    {
        ParentBean parent = ParentBeanCase(ChildBeanCase("tester", 10));
        Set<ConstraintViolation<ParentBean>> violations = validator.validate(parent);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void testValidationFails()
    {
        ParentBean parent = ParentBeanCase(ChildBeanCase("A very long name", 4));
        Set<ConstraintViolation<ParentBean>> violations = validator.validate(parent);

        Assert.assertEquals(violations.size(), 2);
    }
}
