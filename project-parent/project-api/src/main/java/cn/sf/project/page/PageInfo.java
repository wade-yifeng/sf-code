package cn.sf.project.page;

import lombok.ToString;

import java.io.Serializable;

@ToString
public class PageInfo implements Serializable {
    private Integer pageNo;
    private Integer pageSize;
    private Integer offset;
    private Integer limit;
    private int defaultPageNo = 1; /*页码默认从1开始*/
    private int defaultPageSize = 10;

    private PageInfo() {
    }

    public static PageInfo valueOfByPageNo(Integer pageNo, Integer pageSize) {
        return new PageInfo(pageNo, pageSize, 1);
    }
    public static PageInfo valueOfByPageNo(Integer pageNo, Integer pageSize, Integer defaultPageSize) {
        PageInfo pageInfo = new PageInfo(pageNo, pageSize, 1);
        pageInfo.defaultPageSize = defaultPageSize;
        return pageInfo;
    }

    public static PageInfo valueOfByOffset(Integer offset, Integer limit) {
        return new PageInfo(offset, limit, 2);
    }
    public static PageInfo valueOfByOffset(Integer offset, Integer limit, Integer defaultPageSize) {
        PageInfo pageInfo = new PageInfo(offset, limit, 2);
        pageInfo.defaultPageSize = defaultPageSize;
        return pageInfo;
    }

    private PageInfo(Integer _arg1, Integer _arg2, Integer type) {
        if (type == 1) {
            this.pageNo = _arg1;
            this.pageSize = _arg2;
        }
        if (type == 2) {
            this.offset = _arg1;
            this.limit = _arg2;
        }

    }

    public Integer getPageNo() {
        if (null == pageNo || pageNo <= 0) {
            pageNo = defaultPageNo;
        }
        return pageNo;
    }

    public Integer getPageSize() {
        if (null == pageSize || pageSize <= 0) {
            pageSize = defaultPageSize;
        }
        return pageSize;
    }

    public Integer getOffset() {
        if (null == offset || offset <= 0) {
            return Integer.valueOf((getPageNo().intValue() - 1) * getPageSize().intValue());
        }
        return offset;
    }

    public Integer getLimit() {
        if (null == limit || limit <= 0) {
            return getPageSize();
        }
        return limit;
    }
}