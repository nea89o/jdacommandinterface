package io.github.romangraef.jdacommandinterface.paginator;

public interface PageSupplier<T> {

    default boolean isLast(int index) {
        return index == getLastIndex();
    }

    default boolean isFirst(int index) {
        return index == getFirstIndex();
    }

    int getFirstIndex();

    int getLastIndex();

    T supplyPage(int index);
}
