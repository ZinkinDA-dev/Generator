package com.example.generator.repository;

import com.example.generator.model.ColumnInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InformationSchemaRepositoryImpl implements InformationSchemaRepository{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ColumnInfo> getColumn(String tableSchema, String tableName) {
        var query = String.format("select column_name,data_type from information_schema.columns where information_schema.columns.table_schema = %s and information_schema.columns.table_name = %s","'"+ tableSchema +"'","'" + tableName + "'");
        System.out.println(query);
        return jdbcTemplate.query(
                query,
                (resultSet) -> {
                    List<ColumnInfo> columnInfos = new ArrayList<>();
                    while (resultSet.next()){
                        ColumnInfo columnInfo = new ColumnInfo();

                        columnInfo.setColumn(resultSet.getString("column_name"));
                        columnInfo.setDataType(resultSet.getString("data_type"));
                        columnInfos.add(columnInfo);
                    }
                    return columnInfos;
                });
    }

    @Override
    @Transactional
    public int insert(String tableSchema, String tableName, String columns, String values,Integer count) {
        return jdbcTemplate.update(
                String.format("insert into %s.%s %s values %s", tableSchema, tableName, columns, values)
                );
    }
}
