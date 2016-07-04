// Auto generated from com.company.Test by Soabase io.soabase.halva.caseclass.CaseClass annotation processor
package com.company;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyClassTuple;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import io.soabase.halva.tuple.details.Tuple12;
import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Double;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Generated;

@Generated("io.soabase.halva.caseclass.CaseClass")
public class TestCase implements Serializable, Test, Tuplable, ClassTuplable {
    private static final Class classTuplableClass = TestCaseAny(Any.any(), Any.any(), Any.any(), Any.any(), Any.any(), Any.any(), Any.any(), Any.any(), Any.any(), Any.any(), Any.any(), Any.any()).getClass();

    private final String name;

    private final int date;

    private final List<Long> longList;

    private final Map<String, List<Date>> stringDateMap;

    private final ConcurrentMap<String, String> concurrentMap;

    private final boolean truth;

    private final Date activation;

    private final double percent;

    private final float section;

    private final Boolean isIt;

    private volatile String mutableValue;

    private final Charset charset;

    protected TestCase(String name, Integer date, List<Long> longList, Map<String, List<Date>> stringDateMap, ConcurrentMap<String, String> concurrentMap, boolean truth, Date activation, double percent, float section, Boolean isIt, String mutableValue, Charset charset) {
        if ( name == null ) {
            name = "";
        }
        if ( date == null ) {
            date = Test.super.date();
        }
        if ( longList == null ) {
            longList = Collections.unmodifiableList(new ArrayList<>());
        }
        if ( stringDateMap == null ) {
            stringDateMap = Collections.unmodifiableMap(new HashMap<>());
        }
        if ( concurrentMap == null ) {
            concurrentMap = new ConcurrentHashMap<>();
        }
        if ( activation == null ) {
            activation = new Date();
        }
        if ( isIt == null ) {
            isIt = false;
        }
        if ( mutableValue == null ) {
            mutableValue = "";
        }
        if ( charset == null ) {
            throw new IllegalArgumentException("\"charset\" does not have a default value");
        }
        this.name = name;
        this.date = date;
        this.longList = longList;
        this.stringDateMap = stringDateMap;
        this.concurrentMap = concurrentMap;
        this.truth = truth;
        this.activation = activation;
        this.percent = percent;
        this.section = section;
        this.isIt = isIt;
        this.mutableValue = mutableValue;
        this.charset = charset;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int date() {
        return date;
    }

    @Override
    public List<Long> longList() {
        return longList;
    }

    @Override
    public Map<String, List<Date>> stringDateMap() {
        return stringDateMap;
    }

    @Override
    public ConcurrentMap<String, String> concurrentMap() {
        return concurrentMap;
    }

    @Override
    public boolean truth() {
        return truth;
    }

    @Override
    public Date activation() {
        return activation;
    }

    @Override
    public double percent() {
        return percent;
    }

    @Override
    public float section() {
        return section;
    }

    @Override
    public Boolean isIt() {
        return isIt;
    }

    @Override
    public String mutableValue() {
        return mutableValue;
    }

    public void mutableValue(String mutableValue) {
        this.mutableValue = mutableValue;
    }

    @Override
    public Charset charset() {
        return charset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static TestCase TestCase(String name, int date, List<Long> longList, Map<String, List<Date>> stringDateMap, ConcurrentMap<String, String> concurrentMap, boolean truth, Date activation, double percent, float section, Boolean isIt, String mutableValue, Charset charset) {
        return new TestCase(name, date, longList, stringDateMap, concurrentMap, truth, activation, percent, section, isIt, mutableValue, charset);
    }

    public static AnyClassTuple<TestCase> TestCaseAny(AnyVal<? extends String> name, AnyVal<? extends Integer> date, AnyVal<? extends List<? extends Long>> longList, AnyVal<? extends Map<? extends String, ? extends List<Date>>> stringDateMap, AnyVal<? extends ConcurrentMap<? extends String, ? extends String>> concurrentMap, AnyVal<? extends Boolean> truth, AnyVal<? extends Date> activation, AnyVal<? extends Double> percent, AnyVal<? extends Float> section, AnyVal<? extends Boolean> isIt, AnyVal<? extends String> mutableValue, AnyVal<? extends Charset> charset) {
        return new AnyClassTuple<TestCase>(Tuple.Tu(Any.loose(name), Any.loose(date), Any.loose(longList), Any.loose(stringDateMap), Any.loose(concurrentMap), Any.loose(truth), Any.loose(activation), Any.loose(percent), Any.loose(section), Any.loose(isIt), Any.loose(mutableValue), Any.loose(charset))){};
    }

    @Override
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
        TestCase rhs = (TestCase)rhsObj;
        if ( !name.equals(rhs.name) ) {
            return false;
        }
        if ( date != rhs.date ) {
            return false;
        }
        if ( !longList.equals(rhs.longList) ) {
            return false;
        }
        if ( !stringDateMap.equals(rhs.stringDateMap) ) {
            return false;
        }
        if ( !concurrentMap.equals(rhs.concurrentMap) ) {
            return false;
        }
        if ( truth != rhs.truth ) {
            return false;
        }
        if ( !activation.equals(rhs.activation) ) {
            return false;
        }
        if ( percent != rhs.percent ) {
            return false;
        }
        if ( section != rhs.section ) {
            return false;
        }
        if ( !isIt.equals(rhs.isIt) ) {
            return false;
        }
        if ( !mutableValue.equals(rhs.mutableValue) ) {
            return false;
        }
        if ( !charset.equals(rhs.charset) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + date;
        result = 31 * result + longList.hashCode();
        result = 31 * result + stringDateMap.hashCode();
        result = 31 * result + concurrentMap.hashCode();
        result = 31 * result + (truth ? 1 : 0);
        result = 31 * result + activation.hashCode();
        result = 31 * result + Double.hashCode(percent);
        result = 31 * result + Float.hashCode(section);
        result = 31 * result + isIt.hashCode();
        result = 31 * result + mutableValue.hashCode();
        result = 31 * result + charset.hashCode();
        return result;
    }

    @Override
    public Tuple12<String, Integer, List<Long>, Map<String, List<Date>>, ConcurrentMap<String, String>, Boolean, Date, Double, Float, Boolean, String, Charset> tuple() {
        return Tuple.Tu(name(), date(), longList(), stringDateMap(), concurrentMap(), truth(), activation(), percent(), section(), isIt(), mutableValue(), charset());
    }

    public String debugString() {
        return "TestCase { " +
            "name=\"" + name + "\"; " +
            "date=" + date + "; " +
            "longList=" + longList + "; " +
            "stringDateMap=" + stringDateMap + "; " +
            "concurrentMap=" + concurrentMap + "; " +
            "truth=" + truth + "; " +
            "activation=" + activation + "; " +
            "percent=" + percent + "; " +
            "section=" + section + "; " +
            "isIt=" + isIt + "; " +
            "mutableValue=\"" + mutableValue + "\"; " +
            "charset=" + charset + "; " +
        '}';
    }

    @Override
    public String toString() {
        return "TestCase(" +
        "\"" + name + "\"" + 
        ", " + date +
        ", " + longList +
        ", " + stringDateMap +
        ", " + concurrentMap +
        ", " + truth +
        ", " + activation +
        ", " + percent +
        ", " + section +
        ", " + isIt +
        ", \"" + mutableValue + "\"" + 
        ", " + charset +
        ')';
    }

    public static final class Builder {
        private String name;

        private Integer date;

        private List<Long> longList;

        private Map<String, List<Date>> stringDateMap;

        private ConcurrentMap<String, String> concurrentMap;

        private boolean truth;

        private Date activation;

        private double percent;

        private float section;

        private Boolean isIt;

        private String mutableValue;

        private Charset charset;

        private Builder() {
        }

        private Builder(TestCase rhs) {
            name = rhs.name;
            date = rhs.date;
            longList = rhs.longList;
            stringDateMap = rhs.stringDateMap;
            concurrentMap = rhs.concurrentMap;
            truth = rhs.truth;
            activation = rhs.activation;
            percent = rhs.percent;
            section = rhs.section;
            isIt = rhs.isIt;
            mutableValue = rhs.mutableValue;
            charset = rhs.charset;
        }

        public TestCase build() {
            return new TestCase(
                name, 
                date, 
                longList, 
                stringDateMap, 
                concurrentMap, 
                truth, 
                activation, 
                percent, 
                section, 
                isIt, 
                mutableValue, 
                charset
            );
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder date(int date) {
            this.date = date;
            return this;
        }

        public Builder longList(List<Long> longList) {
            this.longList = longList;
            return this;
        }

        public Builder stringDateMap(Map<String, List<Date>> stringDateMap) {
            this.stringDateMap = stringDateMap;
            return this;
        }

        public Builder concurrentMap(ConcurrentMap<String, String> concurrentMap) {
            this.concurrentMap = concurrentMap;
            return this;
        }

        public Builder truth(boolean truth) {
            this.truth = truth;
            return this;
        }

        public Builder activation(Date activation) {
            this.activation = activation;
            return this;
        }

        public Builder percent(double percent) {
            this.percent = percent;
            return this;
        }

        public Builder section(float section) {
            this.section = section;
            return this;
        }

        public Builder isIt(Boolean isIt) {
            this.isIt = isIt;
            return this;
        }

        public Builder mutableValue(String mutableValue) {
            this.mutableValue = mutableValue;
            return this;
        }

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }
    }
}
