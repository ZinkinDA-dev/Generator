package com.example.generator.repository;

import com.example.generator.model.ColumnInfo;

import java.util.List;

public interface InformationSchemaRepository {


    List<ColumnInfo> getColumn(String tableSchema, String tableName);
    int insert(String tableSchema,String tableName, String columns,String values,Integer count);

}
