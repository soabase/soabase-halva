// Auto generated from com.company.ExampleObject by Soabase io.soabase.halva.caseclass.CaseObject annotation processor
package com.company;

import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import io.soabase.halva.tuple.details.Tuple0;
import java.io.Serializable;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;

@Generated("io.soabase.halva.caseclass.CaseObject")
public class ExampleObjectCase implements Serializable, ExampleObject, Tuplable {
    public static final ExampleObjectCase ExampleObjectCase = new ExampleObjectCase();

    protected ExampleObjectCase() {
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
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Tuple0 tuple() {
        return Tuple.Tu();
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
}
