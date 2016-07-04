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

import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.alias.TypeAliasType;
import io.soabase.halva.comprehension.For;
import io.soabase.halva.matcher.Matcher;
import io.soabase.halva.sugar.ConsList;
import java.util.Optional;

/**
 * <p>
 *     A general purpose box/container for both value matching and value extraction. AnyVals are
 *     used by Halva in both For-comprehension and Pattern Matching/Extraction. AnyVals are created
 *     via the {@link Any} factory or by directly extending the AnyVal class (see below for details).
 * </p>
 *
 * <p>
 *     AnyVals are used in a number of ways depending on context:
 *
 *     <ul>
 *     <li>
 *         <strong>Simple value box: </strong> {@link For} comprehension methods use AnyVals to hold
 *         the binding value from a sequence being iterated over. AnyVals used in this context are
 *         created by the method: {@link Any#any()}
 *     </li>
 *
 *     <li>
 *         <strong>Literal value matches: </strong> in pattern matching you may want to match a literal
 *         value. There are several methods in {@link Any} to create literal AnyVals:
 *         <ul>
 *         <li>
 *             <em>{@link Any#lit(Object)}</em> - creates a match for a constant/literal
 *         </li>
 *
 *         <li>
 *             <em>{@link Any#anyNull()}</em> - matches any <code>null</code> value
 *         </li>
 *
 *         <li>
 *             <em>{@link Any#anySome(AnyVal)}</em> - matches any {@link Optional} that is loaded
 *             with a value. The value is extracted to the given AnyVal.
 *         </li>
 *
 *         <li>
 *             <em>{@link Any#anyNone()}</em> - matches any empty {@link Optional}
 *         </li>
 *
 *         <li>
 *             <em>{@link Any#anyOptional(AnyVal)}</em> - matches any {@link Optional} that is loaded
 *             or empty. The value is extracted to the given AnyVal if present.
 *         </li>
 *         </ul>
 *     </li>
 *
 *     <li>
 *         <strong>Type-safe extractors: </strong> these are used with {@link Matcher} to extract
 *         values during pattern matching/extraction. Unlike other AnyVals, these must be created by
 *         defining a new class that extends AnyVal. This is due to Java's type erasure. Extending
 *         AnyVal allows Halva to partially reify the type and ensure type safety. For example, to create
 *         a type-safe extractor for a List of Strings, do: <code>AnyVal&lt;List&lt;String>> m = new
 *         AnyVal&lt;List&lt;String>>(){};</code> Note the required <em>{}</em> at the end of the expression.
 *     </li>
 *
 *     <li>
 *         <strong>TypeAlias extractors: </strong> special-purpose AnyVals that allow matching on generated
 *         halva {@link TypeAlias} instances. Create via {@link Any#typeAlias(TypeAliasType)}
 *     </li>
 *
 *     <li>
 *         <strong>List slice extractors: </strong> create AnyVals that match literal/any of the head/tail
 *         or any combination from a halva {@link ConsList}. See {@link Any#headTail(Object, ConsList)},
 *         {@link Any#headAnyTail(Object, AnyVal)}, {@link Any#anyHeadTail(AnyVal, ConsList)}, and
 *         {@link Any#anyHeadAnyTail(AnyVal, AnyVal)}
 *     </li>
 *     </ul>
 * </p>
 */
public abstract class AnyVal<T>
{
    private final T matchValue;
    private T value;
    private final InternalType internalType;
    private final boolean isSettable;

    protected AnyVal()
    {
        this(null, true, true);
    }

    AnyVal(T matchValue, boolean isSettable, boolean throwIfMisspecified)
    {
        this.matchValue = matchValue;
        this.internalType = InternalType.getInternalType(getClass(), throwIfMisspecified);
        this.isSettable = isSettable;
    }

    /**
     * Return the currently loaded value for this AnyVal. NOTE: not all AnyVal
     * types support this method
     *
     * @return value or null
     * @throws UnsupportedOperationException if not supported
     */
    public T val()
    {
        return value;
    }

    /**
     * Load a new value for this AnyVal. NOTE: not all AnyVal
     * types support this method
     *
     * @param value new value to set
     * @throws UnsupportedOperationException if not supported
     */
    public void set(T value)
    {
        if ( matchValue == null )
        {
            this.value = value;
        }
        // else NOP
    }

    /**
     * Return true if the given value can safely/correctly be set/loaded
     * into this AnyVal
     *
     * @param value value to check
     * @return true/false
     */
    public boolean canSet(T value)
    {
        return isSettable ? internalCanSet(value) : matches(value);
    }

    boolean internalCanSet(T value)
    {
        if ( internalType != null )
        {
            try
            {
                InternalType valueType = InternalType.getInternalType(value.getClass(), false);
                return internalType.isAssignableFrom(valueType);
            }
            catch ( ClassCastException dummy )
            {
                // dummy
            }
        }
        return false;
    }

    InternalType getInternalType()
    {
        return internalType;
    }

    AnyVal<T> loosely()
    {
        return isSettable ? new LooseAny<>(this) : this;
    }

    private boolean matches(T value)
    {
        return (matchValue != null) && matchValue.equals(value);
    }
}
