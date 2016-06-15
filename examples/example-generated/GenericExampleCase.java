// Auto generated from com.company.GenericExample by Soabase CaseClass annotation processor
package com.company;

import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.ClassTuple;
import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import io.soabase.halva.tuple.details.Tuple2;
import java.io.Serializable;
import java.lang.Class;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

public class GenericExampleCase<A, B> implements GenericExample<A, B>, Serializable, Tuplable, ClassTuplable {
    private static final Class classTuplableClass = GenericExampleCaseTu("", "").getClass();

    private final A first;

    private final B second;

    protected GenericExampleCase(A first, B second) {
        if ( first == null ) {
            throw new IllegalArgumentException("\"first\" does not have a default value");
        }
        if ( second == null ) {
            throw new IllegalArgumentException("\"second\" does not have a default value");
        }
        this.first = first;
        this.second = second;
    }

    @Override
    public A first() {
        return first;
    }

    @Override
    public B second() {
        return second;
    }

    public static <A, B> Builder<A, B> builder() {
        return new Builder<>();
    }

    public Builder<A, B> copy() {
        return new Builder<>(this);
    }

    public static <A, B> GenericExampleCase<A, B> GenericExampleCase(A first, B second) {
        return new GenericExampleCase<>(first, second);
    }

    @Override
    public boolean equals(Object rhsObj) {
        if ( this == rhsObj ) {
            return true;
        }
        if ( rhsObj == null || getClass() != rhsObj.getClass() ) {
            return false;
        }
        GenericExampleCase rhs = (GenericExampleCase)rhsObj;
        if ( !first.equals(rhs.first) ) {
            return false;
        }
        if ( !second.equals(rhs.second) ) {
            return false;
        }
        return true;
    }

    @Override
    public Tuple2<A, B> tuple() {
        return Tuple.Tu(first(), second());
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    public String debugString() {
        return "GenericExampleCase { " +
            "first=" + first + "; " +
            "second=" + second + "; " +
        '}';
    }

    @Override
    public String toString() {
        return "GenericExampleCase(" +
        first +
        ", " + second +
        ')';
    }

    public static ClassTuple GenericExampleCaseTu(Object _1, Object _2) {
        return () -> Tuple.Tu(_1, _2);
    }

    @Override
    public Class getClassTuplableClass() {
        return classTuplableClass;
    }

    public static final class Builder<A, B> {
        private A first;

        private B second;

        private Builder() {
        }

        private Builder(GenericExampleCase<A, B> rhs) {
            first = rhs.first;
            second = rhs.second;
        }

        public GenericExampleCase<A, B> build() {
            return new GenericExampleCase<>(
                first, 
                second
            );
        }

        public Builder<A, B> first(A first) {
            this.first = first;
            return this;
        }

        public Builder<A, B> second(B second) {
            this.second = second;
            return this;
        }
    }
}
