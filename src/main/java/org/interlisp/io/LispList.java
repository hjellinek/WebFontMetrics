/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static org.interlisp.io.Litatom.atom;

public class LispList implements SExpression {

    private final List<SExpression> list;

    /**
     * Create a new empty list.
     */
    public LispList() {
        list = new LinkedList<>();
    }

    /**
     * Create a new list containing the provided {@link SExpression}s.
     *
     * @param sexps the {@link SExpression}s
     */
    public LispList(SExpression... sexps) {
        this();
        Arrays.stream(sexps).forEach(this::add);
    }

    /**
     * Shorthand to create a new {@link LispList}.
     *
     * @param sexps the members of the list
     * @return the new {@link LispList}
     */
    public static LispList list(SExpression... sexps) {
        return new LispList(sexps);
    }

    /**
     * Create and return a property list.
     *
     * @param propertyName0 the property name
     * @param value0        the value
     * @return the list
     */
    public static LispList pList(String propertyName0, SExpression value0) {
        return list(atom(propertyName0), value0);
    }

    /**
     * Create and return a property list.
     *
     * @param propertyName0 first property name
     * @param value0        first value
     * @param propertyName1 second property name
     * @param value1        second value
     * @return the list
     */
    public static LispList pList(String propertyName0, SExpression value0,
                                 String propertyName1, SExpression value1) {
        return list(atom(propertyName0), value0, atom(propertyName1), value1);
    }

    /**
     * Create and return a property list.
     *
     * @param propertyName0 first property name
     * @param value0        first value
     * @param propertyName1 second property name
     * @param value1        second value
     * @param propertyName2 third property name
     * @param value2        third value
     * @return the list
     */
    public static LispList pList(String propertyName0, SExpression value0,
                                 String propertyName1, SExpression value1,
                                 String propertyName2, SExpression value2) {
        return list(atom(propertyName0), value0, atom(propertyName1), value1,
                atom(propertyName2), value2);
    }

    /**
     * Create and return a property list.
     *
     * @param propertyName0 first property name
     * @param value0        first value
     * @param propertyName1 second property name
     * @param value1        second value
     * @param propertyName2 third property name
     * @param value2        third value
     * @param propertyName3 fourth property name
     * @param value3        fourth value
     * @param propertyName4 fifth property name
     * @param value4        fifth value
     * @param propertyName5 sixth property name
     * @param value5        sixth value
     * @param propertyName6 seventh property name
     * @param value6        seventh value
     * @param propertyName7 eighth property name
     * @param value7        eighth value
     * @return the list
     */
    public static LispList pList(String propertyName0, SExpression value0,
                                 String propertyName1, SExpression value1,
                                 String propertyName2, SExpression value2,
                                 String propertyName3, SExpression value3,
                                 String propertyName4, SExpression value4,
                                 String propertyName5, SExpression value5,
                                 String propertyName6, SExpression value6,
                                 String propertyName7, SExpression value7) {
        return list(atom(propertyName0), value0, atom(propertyName1), value1,
                atom(propertyName2), value2, atom(propertyName3), value3,
                atom(propertyName4), value4, atom(propertyName5), value5,
                atom(propertyName6), value6, atom(propertyName7), value7);
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write("(");
        for (SExpression sx : list) {
            sx.write(w);
            w.write(" ");
        }
        w.write(")");
    }

    public LispList add(SExpression s) {
        list.add(Objects.requireNonNullElseGet(s, LispNil::new));
        return this;
    }

    public LispList add(String s) {
        list.add(new LispString(s));
        return this;
    }

    public LispList add(Number n) {
        list.add(new LispNumber(n));
        return this;
    }

    public LispList addAll(Collection<SExpression> all) {
        list.addAll(all);
        return this;
    }

    public int size() {
        return list.size();
    }

    @Override
    public String toString() {
        return "LispList{" +
                "list=" + list +
                '}';
    }
}
