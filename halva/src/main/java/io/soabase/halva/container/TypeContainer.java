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
package io.soabase.halva.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface TypeContainer
{
    /**
     * Suffix for the generated class. i.e. if the template is "Foo" the generated class is
     * named "FooContainer" (or whatever the suffix is)
     *
     * @return suffix
     */
    String suffix() default "Container";

    /**
     * If a non-empty string, is used instead of {@link #suffix()}. The
     * generated class name is the name of the template <em>minus</em> the value
     * of this attribute.
     *
     * @return string suffix to remove to produce the class name
     */
    String unsuffix() default "_";

    /**
     * If true, generated classes inside the container are renamed as normal. If false,
     * they retain their declared names.
     *
     * @return true/false
     */
    boolean renameContained() default false;
}
