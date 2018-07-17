package cn.fh.jobdep.test.other;

import cn.fh.jobdep.graph.Matrix;
import org.junit.Test;

import java.util.Arrays;

public class MatrixTest {
    @Test
    public void testMatrix() {
        Matrix matrix = new Matrix(5, 10);
        matrix.addY(0, 100);
        matrix.addY(0, 200);
        matrix.addY(1, 500);
        matrix.addY(1, 600);
        System.out.println(matrix);

        for (Integer[] row : matrix) {
            System.out.println(Arrays.asList(row));
        }
    }
}
