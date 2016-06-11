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
package com.company;

import io.soabase.halva.caseclass.CaseObject;
import java.util.Date;
import java.util.List;

import static io.soabase.halva.suagar.Sugar.List;

@CaseObject
public interface ExampleObject extends Example
{
    default String firstName()
    {
        return "John";
    }

    default String lastName()
    {
        return "Galt";
    }

    default int age()
    {
        return 42;
    }

    default boolean active()
    {
        return false;
    }

    default List<Date> importantDates()
    {
        return List();
    }
}
