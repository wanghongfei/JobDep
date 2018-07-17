package cn.fh.jobdep.graph;

import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@ToString
public class Matrix implements Iterable<Integer[]> {
    private Integer[][] mx;

    /**
     * 永远指向y维度的空位置
     */
    private int[] emptySlot;

    public Matrix(int xCap, int yCap) {
        if (xCap < 1) {
            xCap = 10;
        }

        if (yCap < 1) {
            yCap = 10;
        }

        mx = new Integer[xCap][yCap];
        emptySlot = new int[xCap];
    }

    /**
     * 在指定行上追加元素
     * @param elem
     * @param xPos
     */
    public void addY(int xPos, int elem) {
        if (!rangeCheck(xPos)) {
            return;
        }

        ensureCap(xPos);
        mx[xPos][emptySlot[xPos]++] = elem;
    }

    /**
     * 检查某行是否为空
     *
     * @param xPos
     * @return
     */
    public boolean isRowEmpty(int xPos) {
        if (!rangeCheck(xPos)) {
            return true;
        }

        return emptySlot[xPos] == 0;
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

        return Arrays.asList(mx[xPos]);
    }

    @Override
    public Iterator<Integer[]> iterator() {
        return new MatrixIterator(this);
    }

    /**
     * 确保行容量足够
     * @param xPos
     */
    private void ensureCap(int xPos) {
        Integer[] arr = mx[xPos];

        // 行不存在,创建
        if (null == arr) {
            mx[xPos] = new Integer[5];
            return;
        }

        // 行容量不足
        if (emptySlot[xPos] >= arr.length) {
            // 1.5x
            arr = new Integer[arr.length + arr.length / 2];
            System.arraycopy(mx[xPos], 0, arr, 0, arr.length);

            mx[xPos] = arr;
        }
    }

    private boolean rangeCheck(int xPos) {
        return xPos + 1 <= mx.length;
    }

    private static class MatrixIterator implements Iterator<Integer[]> {
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
        public Integer[] next() {
            return matrix.mx[current++];
        }
    }
}
