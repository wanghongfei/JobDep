package cn.fh.jobdep.graph;

import lombok.ToString;

import java.util.*;

@ToString
public class Matrix implements Iterable<Matrix.MatrixRow> {
    private MatrixRow[] mx;

    public Matrix(int xCap) {
        if (xCap < 1) {
            xCap = 10;
        }

        mx = new MatrixRow[xCap];
        for (int ix = 0; ix < xCap; ++ix) {
            mx[ix] = new MatrixRow();
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

        mx[xPos].add(elem);
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

        return mx[xPos];
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

    public static class MatrixRow extends LinkedList<Integer> {

    }
}
