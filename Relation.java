package org.processmining.plugins.gettingstarted.alphaalgorithm;

//定义四种基本关系：跟随、因果、并行、无关
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
