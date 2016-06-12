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

    AnyDeclaration<Object> any = of(Object.class);
    AnyDeclaration<Integer> anyInt = of(Integer.class);
    AnyDeclaration<Long> anyLong = of(Long.class);
    AnyDeclaration<Double> anyDouble = of(Double.class);
    AnyDeclaration<String> anyString = of(String.class);
    AnyDeclaration<ConsList<Integer>> anyIntList = of(new AnyType<ConsList<Integer>>(){});
    AnyDeclaration<ConsList<Long>> anyLongList = of(new AnyType<ConsList<Long>>(){});
    AnyDeclaration<ConsList<Double>> anyDoubleList = of(new AnyType<ConsList<Double>>(){});
    AnyDeclaration<ConsList<String>> anyStringList = of(new AnyType<ConsList<String>>(){});

    Any<T> define();
}
