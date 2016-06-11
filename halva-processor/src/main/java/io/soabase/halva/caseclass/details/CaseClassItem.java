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

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

class CaseClassItem
{
    private final Optional<String> name;
    private final Optional<TypeMirror> type;
    private final Optional<TypeMirror> erasedType;
    private final boolean hasDefaultValue;
    private final boolean mutable;

    CaseClassItem()
    {
        name = Optional.empty();
        type = Optional.empty();
        erasedType = Optional.empty();
        hasDefaultValue = false;
        mutable = false;
    }

    CaseClassItem(String name, TypeMirror type, TypeMirror erasedType, boolean hasDefaultValue, boolean mutable)
    {
        this.name = Optional.of(name);
        this.type = Optional.of(type);
        this.erasedType = Optional.of(erasedType);
        this.hasDefaultValue = hasDefaultValue;
        this.mutable = mutable;

        if ( type.getKind() == TypeKind.WILDCARD )
        {
            System.out.println();
        }
    }

    String getName()
    {
        return name.orElseThrow(() -> new RuntimeException("Error CaseClassField accessed"));
    }

    TypeMirror getType()
    {
        return type.orElseThrow(() -> new RuntimeException("Error/Ignore CaseClassField accessed"));
    }

    TypeMirror getErasedType()
    {
        return erasedType.orElseThrow(() -> new RuntimeException("Error/Ignore CaseClassField accessed"));
    }

    boolean hasDefaultValue()
    {
        return hasDefaultValue;
    }

    boolean isMutable()
    {
        return mutable;
    }

    @Override
    public String toString()
    {
        return "CaseClassItem{" +
            "name=" + name +
            ", type=" + type +
            ", erasedType=" + erasedType +
            ", hasDefaultValue=" + hasDefaultValue +
            ", mutable=" + mutable +
            '}';
    }
}
