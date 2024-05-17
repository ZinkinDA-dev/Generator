package com.example.generator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ColumnInfo {

    private String columnName;
    @Setter
    private String dataType;

    public String getColumn() {
        return columnName;
    }

    public void setColumn(String columnName) {
        this.columnName = columnName;
    }

}
