package com.codewithbill;

import java.io.Serializable;

public class Tile implements Serializable {

    public Shape Shape;
    public Color Color;
    public State state;
    public int row,col;

    public Tile(Color color, Shape shape) {
        this.Color = color;
        this.Shape = shape;
    }

    public enum Shape {
        square(1), circle(2), star(3), diamond(4), cross(5), club(6);

        public int code;

        Shape(int i) {
            this.code = i;
        }
    }

    public enum Color {
        blue(1), green(2), red(3), yellow(4), purple(5), orange(6);

        public int code;

        Color(int i) {
            this.code = i;
        }
    }

    public enum State {
        swapping(1), placing(2), inBag(3), inHand(4),onBoard(5);

        public int code;

        State(int i) {
            this.code = i;
        }
    }

    public Shape getShape() {
        return Shape;
    }

    public Color getColor() {
        return Color;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        String toReturn = "";
        toReturn += this.getColor().name() + "" + this.getShape().name();
        for (int i = toReturn.length(); i < 14; i++) {
            toReturn += "";
        }
        return toReturn;
    }
}
