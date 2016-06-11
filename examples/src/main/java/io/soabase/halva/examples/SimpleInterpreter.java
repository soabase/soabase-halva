package io.soabase.halva.examples;

import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyDeclaration;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.caseclass.CaseClassIgnore;
import io.soabase.halva.caseclass.CaseObject;
import io.soabase.halva.sugar.ConsList;
import io.soabase.halva.tuple.details.Pair;
import java.util.function.Function;

import static io.soabase.halva.any.Any.defineHeadAnyTail;
import static io.soabase.halva.any.AnyDeclaration.anyInt;
import static io.soabase.halva.any.AnyDeclaration.anyString;
import static io.soabase.halva.comprehension.For.For;
import static io.soabase.halva.examples.Add.Add;
import static io.soabase.halva.examples.Add.AddT;
import static io.soabase.halva.examples.App.App;
import static io.soabase.halva.examples.App.AppT;
import static io.soabase.halva.examples.Con.Con;
import static io.soabase.halva.examples.Con.ConT;
import static io.soabase.halva.examples.Environment.Environment;
import static io.soabase.halva.examples.Fun.Fun;
import static io.soabase.halva.examples.Fun.FunT;
import static io.soabase.halva.examples.Lam.Lam;
import static io.soabase.halva.examples.Lam.LamT;
import static io.soabase.halva.examples.M.M;
import static io.soabase.halva.examples.Num.Num;
import static io.soabase.halva.examples.Num.NumT;
import static io.soabase.halva.examples.Var.Var;
import static io.soabase.halva.examples.Var.VarT;
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
        Any<String> y = anyString().define();
        Any<Value> b = anyValue.define();
        Any<Environment> e1 = anyEnvironment.define();

        return match(e).
            caseOf( List(), () -> unitM(Wrong) ).
            caseOf( defineHeadAnyTail(Pair(y, b), e1), () -> x.equals(y.val()) ? unitM(b.val()) : lookup(x, e1.val()) ).
        get();
    }

    static M<Value> add(Value a, Value b)
    {
        Any<Integer> m = anyInt().define();
        Any<Integer> n = anyInt().define();

        return match(Pair(a, b))
            .caseOf( Pair(NumT(m), NumT(n)), () -> unitM(Num(m.val() + n.val())) )
            .caseOf( () -> unitM(Wrong) )
        .get();
    }

    static M<Value> apply(Value a, Value b)
    {
        Any<Function<Value, M<Value>>> k = anyFun.define();

        return match(a).
            caseOf( FunT(k), () -> k.val().apply(b) ).
            caseOf( () -> unitM(Wrong) ).
        get();
    }

    static M<Value> interp(Term term, Environment e)
    {
        Any<String> x = anyString().define();
        Any<Integer> n = anyInt().define();
        Any<Term> l = anyTerm.define();
        Any<Term> r = anyTerm.define();
        Any<Term> t = anyTerm.define();
        Any<Term> f = anyTerm.define();
        Any<M<Value>> a = anyM.define();
        Any<M<Value>> b = anyM.define();
        Any<M<Value>> c = anyM.define();

        return match(term).
            caseOf( VarT(x), () -> lookup( x.val(), e) ).
            caseOf( ConT(n), () -> unitM( Num(n.val())) ).
            caseOf( AddT(l, r), () -> For(a, Iterable(interp(l.val(), e))).
                                      and( b, () -> Iterable(interp(r.val(), e)) ).
                                      and( c, () -> Iterable(add(a.val().value(), b.val().value())) ).
                                      yield1( c::val )
                   ).
            caseOf( LamT(x, t), () -> unitM( Fun(arg -> interp(t.val(), Environment(cons(Pair(x.val(), arg), e))))) ).
            caseOf( AppT(f, t), () -> For( a, Iterable(interp(f.val(), e)) ).
                                       and( b, () -> Iterable(interp(t.val(), e)) ).
                                       and( c, () -> Iterable(apply(a.val().value(), b.val().value())) ).
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

    // any declarations

    private static AnyDeclaration<Environment> anyEnvironment = AnyDeclaration.ofTypeAlias(Environment.TypeAliasType);
    private static AnyDeclaration<Term> anyTerm = AnyDeclaration.of(Term.class);
    private static AnyDeclaration<Value> anyValue = AnyDeclaration.of(new AnyType<Value>(){});
    private static AnyDeclaration<M<Value>> anyM = AnyDeclaration.of(new AnyType<M<Value>>(){});
    private static AnyDeclaration<Function<Value, M<Value>>> anyFun = AnyDeclaration.of(new AnyType<Function<Value, M<Value>>>(){});
    private static AnyDeclaration<Add> anyAdd = AnyDeclaration.of(Add.class);
    private static AnyDeclaration<Con> anyConstant = AnyDeclaration.of(Con.class);
}
