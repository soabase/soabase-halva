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
package io.soabase.halva.implicit;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ImplicitClass
public class AnimalCage
{
    private final AtomicReference<Animal> animal = new AtomicReference<>();

    public AnimalCage(@Implicit Animal firstAnimal)
    {
        animal.set(firstAnimal);
    }

    public void setAnimal(@Implicit Animal animal, @Implicit List<String> strings, int threshold)
    {
        this.animal.set(animal);
    }

    public Animal getAnimal()
    {
        return animal.get();
    }
}
