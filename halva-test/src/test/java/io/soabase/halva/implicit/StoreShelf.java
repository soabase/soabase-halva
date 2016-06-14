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

@ImplicitClass
public class StoreShelf
{
    private final AnimalCage cage;
    private final Feed feed;

    public StoreShelf(@Implicit AnimalCage cage, @Implicit Feed feed)
    {
        this.cage = cage;
        this.feed = feed;
    }

    public AnimalCage getCage()
    {
        return cage;
    }

    public Feed getFeed()
    {
        return feed;
    }
}
