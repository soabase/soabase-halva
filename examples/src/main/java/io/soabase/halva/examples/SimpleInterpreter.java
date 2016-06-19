package io.soabase.halva.examples;

import io.soabase.halva.any.AnyType;
import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.any.Any;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.caseclass.CaseClassIgnore;
import io.soabase.halva.caseclass.CaseObject;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.sugar.ConsList;
import io.soabase.halva.tuple.Pair;
import java.util.function.Function;

import static io.soabase.halva.any.Any.headAnyTail;
import static io.soabase.halva.comprehension.For.forComp;
import static io.soabase.halva.examples.Add.Add;
import static io.soabase.halva.examples.Add.AddTu;
import static io.soabase.halva.examples.App.App;
import static io.soabase.halva.examples.App.AppTu;
import static io.soabase.halva.examples.Con.Con;
import static io.soabase.halva.examples.Con.ConTu;
import static io.soabase.halva.examples.Environment.Environment;
import static io.soabase.halva.examples.Fun.Fun;
import static io.soabase.halva.examples.Fun.FunTu;
import static io.soabase.halva.examples.Lam.Lam;
import static io.soabase.halva.examples.Lam.LamTu;
import static io.soabase.halva.examples.M.M;
import static io.soabase.halva.examples.Num.Num;
import static io.soabase.halva.examples.Num.NumTu;
import static io.soabase.halva.examples.Var.Var;
import static io.soabase.halva.examples.Var.VarTu;
import static io.soabase.halva.examples.Wrong.Wrong;
import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.sugar.Sugar.Iterable;
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.sugar.Sugar.cons;
import static io.soabase.halva.tuple.Tuple.Pair;

class SimpleInterpreter
{
    @CaseClass interface M_<A>{ A value();
        default <B> M_<B> bind(Function<A, M_<B>> k){ return k.apply(value()); }
        default <B> M_<B> map(Function<A, B> f){ return bind(x -> unitM(f.apply(x))); }
        default <B> M_<B> flatMap(Function<A, M_<B>> f){ return bind(f); }
    }

    static <A> M<A> unitM(A a){ return M(a); }

    static String showM(M<Value> m){ return m.value().value(); }

    interface Term{}

    @CaseClass interface Var_ extends Term{String name();}
    @CaseClass interface Con_ extends Term{int n();}
    @CaseClass interface Add_ extends Term{Term l(); Term r();}
    @CaseClass interface Lam_ extends Term{String name(); Term body();}
    @CaseClass interface App_ extends Term{Term fun(); Term arg();}

    interface Value{String value();}
    @CaseObject interface Wrong_ extends Value {
        default String value() { return "wrong"; }
    }
    @CaseClass interface Num_ extends Value{int n();
        @CaseClassIgnore default String value() { return String.valueOf(n()); }
    }
    @CaseClass interface Fun_ extends Value{Function<Value, M_<Value>> f();
        @CaseClassIgnore default String value() { return "<function>"; }
    }

    @TypeAlias interface Environment_ extends ConsList<Pair<String, Value>>{}

    static M<Value> lookup(String x, Environment e) {
        Any<String> y = new AnyType<String>(){};
        Any<Value> b = new AnyType<Value>(){};
        Any<Environment> e1 = Any.typeAlias(Environment.TypeAliasType);

        return match(e).
            caseOf( List(), () -> unitM(Wrong) ).
            caseOf( Any.headAnyTail(Pair(y, b), e1), () -> x.equals(y.val()) ? unitM(b.val()) : lookup(x, e1.val()) ).
        get();
    }

    static M<Value> add(Value a, Value b)
    {
        Any<Integer> m = new AnyType<Integer>(){};
        Any<Integer> n = new AnyType<Integer>(){};

        return match(Pair(a, b)).
            caseOf( Pair(NumTu(m), NumTu(n)), () -> unitM(Num(m.val() + n.val())) ).
            caseOf( () -> unitM(Wrong) ).
        get();
    }

    static M<Value> apply(Value a, Value b)
    {
        Any<Function<Value, M<Value>>> k = new AnyType<Function<Value, M<Value>>>(){};

        return match(a).
            caseOf( FunTu(k), () -> k.val().apply(b) ).
            caseOf( () -> unitM(Wrong) ).
        get();
    }

    static M<Value> interp(Term term, Environment e)
    {
        Any<String> x = new AnyType<String>(){};
        Any<Integer> n = new AnyType<Integer>(){};
        Any<Term> l = new AnyType<Term>(){};
        Any<Term> r = new AnyType<Term>(){};
        Any<Term> t = new AnyType<Term>(){};
        Any<Term> f = new AnyType<Term>(){};

        AnyVal<M<Value>> a = Any.make();
        AnyVal<M<Value>> b = Any.make();
        AnyVal<M<Value>> c = Any.make();

        return match(term).
            caseOf( VarTu(x), () -> lookup( x.val(), e) ).
            caseOf( ConTu(n), () -> unitM( Num(n.val())) ).
            caseOf( AddTu(l, r), () -> forComp (a, Iterable(interp(l.val(), e)) ).
                                      forComp( b, () -> Iterable(interp(r.val(), e)) ).
                                      forComp( c, () -> Iterable(add(a.val().value(), b.val().value())) ).
                                      yield1( c::val )
                   ).
            caseOf( LamTu(x, t), () -> unitM( Fun(arg -> interp(t.val(), Environment(cons(Pair(x.val(), arg), e))))) ).
            caseOf( AppTu(f, t), () -> forComp( a, Iterable(interp(f.val(), e)) ).
                                      forComp( b, () -> Iterable(interp(t.val(), e)) ).
                                      forComp( c, () -> Iterable(apply(a.val().value(), b.val().value())) ).
                                      yield1( c::val )
                   ).
            get();
    }

    static String test(Term t)
    {
        return showM(interp(t, Environment(List())));
    }

    static final App term0 = App(Lam("x", Add(Var("x"), Var("x"))), Add(Con(10), Con(11)));
    static final App term1 = App(Con(1), Con(2));

    public static void main(String[] args)
    {
        System.out.println(test(term0));
        System.out.println(test(term1));
    }
}
