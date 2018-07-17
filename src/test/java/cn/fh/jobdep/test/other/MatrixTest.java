package cn.fh.jobdep.test.other;

import cn.fh.jobdep.graph.Matrix;
import org.junit.Test;

public class MatrixTest {
    @Test
    public void testMatrix() {
        Matrix matrix = new Matrix(5);
        matrix.addY(0, 100);
        matrix.addY(0, 200);
        matrix.addY(1, 500);
        matrix.addY(1, 600);
        System.out.println(matrix);

        for (Matrix.MatrixRow row : matrix) {
            System.out.println(row);
        }
    }
}
