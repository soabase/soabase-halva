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
import io.soabase.halva.sugar.ConsList;

public interface AnyDeclaration<T>
{
    static <T> AnyDeclaration<T> of(Class<T> clazz)
    {
        return new AnyDeclarationImpl<>(clazz, null, null);
    }

    static <T> AnyDeclaration<T> of(AnyType<T> typeLiteral)
    {
        return new AnyDeclarationImpl<>(null, typeLiteral, null);
    }

    static <T extends REAL, REAL> AnyDeclaration<T> ofTypeAlias(TypeAliasType<REAL, T> typeAliasType)
    {
        return new AnyDeclarationImpl<>(null, typeAliasType.getAliasType(), typeAliasType);
    }

    static AnyDeclaration<Object> any() { return of(Object.class); }
    static AnyDeclaration<Integer> anyInt() { return of(Integer.class); }
    static AnyDeclaration<Long> anyLong() { return of(Long.class); }
    static AnyDeclaration<Double> anyDouble() { return of(Double.class); }
    static AnyDeclaration<String> anyString() { return of(String.class); }
    static AnyDeclaration<ConsList<Integer>> anyIntList() { return of(new AnyType<ConsList<Integer>>(){}); }
    static AnyDeclaration<ConsList<Long>> anyLongList() { return of(new AnyType<ConsList<Long>>(){}); }
    static AnyDeclaration<ConsList<Double>> anyDoubleList() { return of(new AnyType<ConsList<Double>>(){}); }
    static AnyDeclaration<ConsList<String>> anyStringList() { return of(new AnyType<ConsList<String>>(){}); }

    Any<T> define();
}
