// Auto generated from com.company.ExampleObject by Soabase CaseObject annotation processor
package com.company;

import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.ClassTuple;
import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import io.soabase.halva.tuple.details.Tuple0;
import java.io.Serializable;
import java.lang.Class;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

public class ExampleObjectCase implements ExampleObject, Serializable, Tuplable, ClassTuplable {
    public static final ExampleObjectCase ExampleObjectCase = new ExampleObjectCase();

    private static final Class classTuplableClass = ExampleObjectCaseTu().getClass();

    private ExampleObjectCase() {
    }

    @Override
    public boolean equals(Object rhsObj) {
        if ( this == rhsObj ) {
            return true;
        }
        if ( rhsObj == null || getClass() != rhsObj.getClass() ) {
            return false;
        }
        return true;
    }

    @Override
    public Tuple0 tuple() {
        return Tuple.Tu();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String debugString() {
        return "ExampleObjectCase { " +
        '}';
    }

    @Override
    public String toString() {
        return "ExampleObjectCase(" +
        ')';
    }

    public static ClassTuple ExampleObjectCaseTu() {
        return () -> Tuple.Tu();
    }

    @Override
    public Class getClassTuplableClass() {
        return classTuplableClass;
    }
}
