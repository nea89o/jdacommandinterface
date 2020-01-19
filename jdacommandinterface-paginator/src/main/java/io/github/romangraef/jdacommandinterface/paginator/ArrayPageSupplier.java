package io.github.romangraef.jdacommandinterface.paginator;

public class ArrayPageSupplier<T> implements PageSupplier<T> {
    private T[] array;

    public ArrayPageSupplier(T... array) {
        this.array = array;
    }

    @Override
    public int getFirstIndex() {
        return 0;
    }

    @Override
    public int getLastIndex() {
        return array.length - 1;
    }

    @Override
    public T supplyPage(int index) {
        return array[index];
    }
}
