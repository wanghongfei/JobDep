package cn.fh.jobdep.graph;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@ToString
@Getter
@Setter
public class Matrix implements Iterable<Matrix.MatrixRow> {
    private MatrixRow[] mx;

    public Matrix() {

    }

    public Matrix(int xCap) {
        if (xCap < 1) {
            xCap = 10;
        }

        mx = new MatrixRow[xCap];
        for (int ix = 0; ix < xCap; ++ix) {
            mx[ix] = new MatrixRow(ix);
        }
    }

    /**
     * 在指定行上追加元素
     * @param elem
     * @param xPos
     */
    public void addY(int xPos, Integer elem) {
        if (!rangeCheck(xPos)) {
            return;
        }

        mx[xPos].getList().add(elem);
    }


    /**
     * 获取整行元素
     *
     * @param xPos
     * @return
     */
    public List<Integer> getRows(int xPos) {
        if (!rangeCheck(xPos)) {
            return Collections.emptyList();
        }

        return mx[xPos].getList();
    }

    @Override
    public Iterator<MatrixRow> iterator() {
        return new MatrixIterator(this);
    }


    private boolean rangeCheck(int xPos) {
        return xPos + 1 <= mx.length;
    }

    private static class MatrixIterator implements Iterator<MatrixRow> {
        private Matrix matrix;
        private int current = 0;

        public MatrixIterator(Matrix matrix) {
            this.matrix = matrix;
        }

        @Override
        public boolean hasNext() {
            return this.current < matrix.mx.length;
        }

        @Override
        public MatrixRow next() {
            return matrix.mx[current++];
        }
    }

    @Data
    public static class MatrixRow implements Iterable<Integer> {
        private int index;

        private LinkedList<Integer> list = new LinkedList<>();

        public MatrixRow() {
        }

        public MatrixRow(int index) {
            this.index = index;
        }

        @Override
        public Iterator<Integer> iterator() {
            return this.list.iterator();
        }
    }
}
