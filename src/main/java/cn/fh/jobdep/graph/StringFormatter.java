package cn.fh.jobdep.graph;

/**
 * 将DAG格式化成方便人读的字符串
 */
public class StringFormatter implements GraphFormatter<String> {
    @Override
    public String format(AdjTaskGraph graph) {
        StringBuilder sb = new StringBuilder(30);
        for (Matrix.MatrixRow row : graph.adj) {
            JobVertex job = graph.vertexMap.get(row.getIndex());
            if (row.getList().isEmpty()) {
                sb.append(job.getName()).append("(").append(job.getIndex()).append(":").append(job.getStatus()).append(")");
                sb.append("->");
                sb.append("empty").append('\n');
                continue;
            }

            for (Integer id : row) {
                JobVertex toJob = graph.vertexMap.get(id);
                sb.append(job.getName()).append("(").append(job.getIndex()).append(":").append(job.getStatus()).append(")");
                sb.append("->");
                sb.append(toJob.getName()).append("(").append(toJob.getIndex()).append(")");
                sb.append(",");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
