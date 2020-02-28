package com.mz.finalcommunity.finalcommunity.entity;

public class Page {
    private int current = 1;
    private int limit = 10;
    private int row;
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        if (row >= 0) {
            this.row = row;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset() {
        return (current - 1) * limit;
    }

    public int getTotal() {
        if (row % limit == 0) {
            return row / limit;
        } else {
            return row / limit + 1;
        }
    }

    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    public int getTo() {
        int to = current +2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
