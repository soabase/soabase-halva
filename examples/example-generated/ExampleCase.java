// Auto generated from com.company.Example by Soabase CaseClass annotation processor
package com.company;

import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.ClassTuple;
import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import io.soabase.halva.tuple.details.Tuple5;
import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExampleCase implements Example, Serializable, Tuplable, ClassTuplable {
    private static final Class classTuplableClass = ExampleCaseT("", "", "", "", "").getClass();

    private final String firstName;

    private final String lastName;

    private final int age;

    private final boolean active;

    private final List<Date> importantDates;

    protected ExampleCase(String firstName, String lastName, int age, boolean active, List<Date> importantDates) {
        if ( firstName == null ) {
            firstName = "";
        }
        if ( lastName == null ) {
            lastName = "";
        }
        if ( importantDates == null ) {
            importantDates = Collections.unmodifiableList(new ArrayList<>());
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.active = active;
        this.importantDates = importantDates;
    }

    @Override
    public String firstName() {
        return firstName;
    }

    @Override
    public String lastName() {
        return lastName;
    }

    @Override
    public int age() {
        return age;
    }

    @Override
    public boolean active() {
        return active;
    }

    @Override
    public List<Date> importantDates() {
        return importantDates;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static ExampleCase ExampleCase(String firstName, String lastName, int age, boolean active, List<Date> importantDates) {
        return new ExampleCase(firstName, lastName, age, active, importantDates);
    }

    @Override
    public boolean equals(Object rhsObj) {
        if ( this == rhsObj ) {
            return true;
        }
        if ( rhsObj == null || getClass() != rhsObj.getClass() ) {
            return false;
        }
        ExampleCase rhs = (ExampleCase)rhsObj;
        if ( !firstName.equals(rhs.firstName) ) {
            return false;
        }
        if ( !lastName.equals(rhs.lastName) ) {
            return false;
        }
        if ( age != rhs.age ) {
            return false;
        }
        if ( active != rhs.active ) {
            return false;
        }
        if ( !importantDates.equals(rhs.importantDates) ) {
            return false;
        }
        return true;
    }

    @Override
    public Tuple5<String, String, Integer, Boolean, List<Date>> tuple() {
        return Tuple.T(firstName(), lastName(), age(), active(), importantDates());
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + age;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + importantDates.hashCode();
        return result;
    }

    public String debugString() {
        return "ExampleCase { " +
            "firstName=\"" + firstName + "\"; " +
            "lastName=\"" + lastName + "\"; " +
            "age=" + age + "; " +
            "active=" + active + "; " +
            "importantDates=" + importantDates + "; " +
        '}';
    }

    @Override
    public String toString() {
        return "ExampleCase(" +
        "\"" + firstName + "\"" + 
        ", \"" + lastName + "\"" + 
        ", " + age +
        ", " + active +
        ", " + importantDates +
        ')';
    }

    public static ClassTuple ExampleCaseT(Object _1, Object _2, Object _3, Object _4, Object _5) {
        return () -> Tuple.T(_1, _2, _3, _4, _5);
    }

    @Override
    public Class getClassTuplableClass() {
        return classTuplableClass;
    }

    public static final class Builder {
        private String firstName;

        private String lastName;

        private int age;

        private boolean active;

        private List<Date> importantDates;

        private Builder() {
        }

        private Builder(ExampleCase rhs) {
            firstName = rhs.firstName;
            lastName = rhs.lastName;
            age = rhs.age;
            active = rhs.active;
            importantDates = rhs.importantDates;
        }

        public ExampleCase build() {
            return new ExampleCase(
                firstName, 
                lastName, 
                age, 
                active, 
                importantDates
            );
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder importantDates(List<Date> importantDates) {
            this.importantDates = importantDates;
            return this;
        }
    }
}
