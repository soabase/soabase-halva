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
package io.soabase.halva.any;

import io.soabase.halva.alias.TypeAliasType;

class AnyDeclarationImpl<T extends REAL, REAL> implements AnyDeclaration<T>
{
    private final Class<T> clazz;
    private final AnyType<T> typeLiteral;
    private final TypeAliasType<REAL, T> typeAliasType;

    AnyDeclarationImpl(Class<T> clazz, AnyType<T> typeLiteral, TypeAliasType<REAL, T> typeAliasType)
    {
        this.clazz = clazz;
        this.typeLiteral = typeLiteral;
        this.typeAliasType = typeAliasType;
    }

    @Override
    public Any<T> define()
    {
        return new AnyImpl<>(this, clazz, typeLiteral, typeAliasType);
    }
}
