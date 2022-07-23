package org.processmining.plugins.gettingstarted.alphaalgorithm;

//�������ֻ�����ϵ�����桢��������С��޹�
public enum Relation {
    // ->
    PRECEDES('>'),
    // <-
    FOLLOWS('<'),
    // ||
    PARALLEL('|'),
    // #
    NOT_CONNECTED('#');

    Relation(char symbol) {
        this.sym = symbol;
    }

    private final char sym;

    public char symbol() {
        return sym;
    }
}
