// Auto generated from com.company.JsonTest by Soabase io.soabase.halva.caseclass.CaseClass annotation processor
package com.company;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyClassTuple;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import io.soabase.halva.tuple.details.Tuple3;
import java.io.Serializable;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;

@JsonDeserialize(
        builder = JsonTestCase.Builder.class
)
@Generated("io.soabase.halva.caseclass.CaseClass")
public class JsonTestCase implements Serializable, JsonTest, Tuplable, ClassTuplable {
    private static final Class classTuplableClass = JsonTestCaseMatch(AnyVal.any(), AnyVal.any(), AnyVal.any()).getClass();

    @JsonProperty
    private final String firstName;

    @JsonProperty
    private final String lastName;

    @JsonProperty
    private final int age;

    protected JsonTestCase(String firstName, String lastName, int age) {
        if ( firstName == null ) {
            firstName = "";
        }
        if ( lastName == null ) {
            lastName = "";
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
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

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static JsonTestCase JsonTestCase(String firstName, String lastName, int age) {
        return new JsonTestCase(firstName, lastName, age);
    }

    public static AnyClassTuple<JsonTestCase> JsonTestCaseMatch(AnyVal<? extends String> firstName, AnyVal<? extends String> lastName, AnyVal<? extends Integer> age) {
        return new AnyClassTuple<JsonTestCase>(Tuple.Tu(Any.anyLoose(firstName), Any.anyLoose(lastName), Any.anyLoose(age))){};
    }

    @Override
    @JsonIgnore
    public Class getClassTuplableClass() {
        return classTuplableClass;
    }

    @Override
    public boolean equals(Object rhsObj) {
        if ( this == rhsObj ) {
            return true;
        }
        if ( rhsObj == null || getClass() != rhsObj.getClass() ) {
            return false;
        }
        JsonTestCase rhs = (JsonTestCase)rhsObj;
        if ( !firstName.equals(rhs.firstName) ) {
            return false;
        }
        if ( !lastName.equals(rhs.lastName) ) {
            return false;
        }
        if ( age != rhs.age ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + age;
        return result;
    }

    @Override
    public Tuple3<String, String, Integer> tuple() {
        return Tuple.Tu(firstName(), lastName(), age());
    }

    public String debugString() {
        return "JsonTestCase { " +
            "firstName=\"" + firstName + "\"; " +
            "lastName=\"" + lastName + "\"; " +
            "age=" + age + "; " +
        '}';
    }

    @Override
    public String toString() {
        return "JsonTestCase(" +
        "\"" + firstName + "\"" + 
        ", \"" + lastName + "\"" + 
        ", " + age +
        ')';
    }

    @JsonPOJOBuilder(
            withPrefix = ""
    )
    public static final class Builder {
        @JsonProperty
        private String firstName;

        @JsonProperty
        private String lastName;

        @JsonProperty
        private int age;

        @JsonCreator
        private Builder() {
        }

        private Builder(JsonTestCase rhs) {
            firstName = rhs.firstName;
            lastName = rhs.lastName;
            age = rhs.age;
        }

        public JsonTestCase build() {
            return new JsonTestCase(
                firstName, 
                lastName, 
                age
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
    }
}
