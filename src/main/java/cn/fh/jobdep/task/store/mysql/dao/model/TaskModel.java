package cn.fh.jobdep.task.store.mysql.dao.model;

import java.util.Date;

public class TaskModel {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.graph
     *
     * @mbg.generated
     */
    private String graph;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.status
     *
     * @mbg.generated
     */
    private Integer status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.message
     *
     * @mbg.generated
     */
    private String message;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.id
     *
     * @return the value of task.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.id
     *
     * @param id the value for task.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.create_time
     *
     * @return the value of task.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.create_time
     *
     * @param createTime the value for task.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.update_time
     *
     * @return the value of task.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.update_time
     *
     * @param updateTime the value for task.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.graph
     *
     * @return the value of task.graph
     *
     * @mbg.generated
     */
    public String getGraph() {
        return graph;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.graph
     *
     * @param graph the value for task.graph
     *
     * @mbg.generated
     */
    public void setGraph(String graph) {
        this.graph = graph == null ? null : graph.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.status
     *
     * @return the value of task.status
     *
     * @mbg.generated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.status
     *
     * @param status the value for task.status
     *
     * @mbg.generated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.message
     *
     * @return the value of task.message
     *
     * @mbg.generated
     */
    public String getMessage() {
        return message;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.message
     *
     * @param message the value for task.message
     *
     * @mbg.generated
     */
    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }
}