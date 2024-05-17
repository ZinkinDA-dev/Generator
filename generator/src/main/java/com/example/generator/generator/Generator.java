package com.example.generator.generator;

import com.example.generator.model.ColumnInfo;

import java.util.List;

public interface Generator {

    String getColumns();

    String generatedValues(int rowCountInBatch);

    List<ColumnInfo> getColumnsInfo();
}
