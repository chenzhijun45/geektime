package com.study.week8.controller.response;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 */
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = -5204540340327146707L;

    //当前页 默认1
    private Integer pageNo = 1;
    //每页数据条数 默认10
    private Integer pageSize = 10;
    //总数据条数
    private Integer totalCount;
    //总页数
    private Integer totalPage;
    //是否有下一页 默认否
    private Boolean hasNext = false;
    //分页数据
    private List<T> list;

    /**
     * 分页构造函数
     *
     * @param pageNo     当前页
     * @param pageSize   每页数据条数
     * @param totalCount 总条数
     * @param list       分页返回数据
     */
    public PageResult(Integer pageNo, Integer pageSize, Integer totalCount, List<T> list) {
        super();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.setTotalCount(totalCount);
        this.list = list;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * 设置数据总条数
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        //设置总页数
        this.totalPage = (totalCount + this.pageSize - 1) / this.pageSize;
        this.hasNext = this.totalPage > this.pageNo;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

}
