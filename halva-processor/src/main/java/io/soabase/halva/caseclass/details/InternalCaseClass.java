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
package io.soabase.halva.caseclass.details;

import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.caseclass.CaseObject;

class InternalCaseClass
{
    private final boolean isObject;
    private final String suffix;
    private final String unsuffix;
    private final boolean json;
    private final String type;

    InternalCaseClass(CaseObject caseObject)
    {
        this.isObject = true;
        this.suffix = caseObject.suffix();
        this.unsuffix = caseObject.unsuffix();
        this.json = false;
        type = "CaseObject";
    }

    InternalCaseClass(CaseClass caseClass)
    {
        this.isObject = false;
        this.suffix = caseClass.suffix();
        this.unsuffix = caseClass.unsuffix();
        this.json = caseClass.json();
        type = "CaseClass";
    }

    String getType()
    {
        return type;
    }

    boolean isObject()
    {
        return isObject;
    }

    String suffix()
    {
        return suffix;
    }

    String unsuffix()
    {
        return unsuffix;
    }

    boolean json()
    {
        return json;
    }
}
