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
// Auto generated from com.company.StreamForFactory by Soabase MonadicFor annotation processor
package io.soabase.halva.comprehension;

import io.soabase.halva.any.AnyVal;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamFor
{
    private final MonadicForImpl<Stream> delegate;

    private StreamFor(MonadicForImpl<Stream> delegate) {
        this.delegate = delegate;
    }

    public static <A> StreamFor forComp(AnyVal<A> any, Stream<A> firstMonad) {
        return new StreamFor(new MonadicForImpl<>(any, firstMonad, new StreamForFactory()));
    }

    public <A> StreamFor forComp(AnyVal<A> any, Supplier<? extends Stream<A>> supplier) {
        delegate.forComp(any, supplier);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <A> Stream<A> yield(Supplier<A> supplier) {
        return delegate.yield(supplier);
    }

    public <R> StreamFor letComp(AnyVal<R> any, Supplier<R> supplier) {
        delegate.letComp(any, supplier);
        return this;
    }

    public StreamFor filter(Supplier<Boolean> supplier) {
        delegate.filter(supplier);
        return this;
    }
}
