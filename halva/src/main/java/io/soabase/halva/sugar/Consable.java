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
package io.soabase.halva.sugar;

/**
 * "cons" style methods
 */
public interface Consable<T, R extends Consable<T, R>>
{
    /**
     * Return the first item of the list
     *
     * @return first item
     * @throws IndexOutOfBoundsException if the list is empty
     */
    T head();

    /**
     * Return a new list that has all the elements of this list except the head
     *
     * @return the tail
     * @throws IndexOutOfBoundsException if the list is empty
     */
    R tail();

    /**
     * Return a new list that is the contents of this list concatenated with the
     * contents of the given list
     *
     * @param rhs list to concatenate
     * @return new list
     */
    R concat(R rhs);

    /**
     * Return a new list that consists of the given value as the head and this list
     * as the tail
     *
     * @param newHead value
     * @return new list
     */
    R cons(T newHead);
}
