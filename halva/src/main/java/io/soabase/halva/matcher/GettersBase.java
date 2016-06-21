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
package io.soabase.halva.matcher;

import java.util.Optional;

/**
 * <em>Terminal operations</em>
 */
public interface GettersBase
{
    /**
     * Process all the added cases and return the match
     *
     * @return Optional of the match. If the optional is empty, there is no match
     */
    <T> Optional<T> getOpt();

    /**
     * Process all the added cases and return the match
     *
     * @return the match
     * @throws MatchError if there are no matches
     */
    <T> T get();

    /**
     * Process all the added cases without returning any value
     */
    void apply();
}
