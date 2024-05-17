package com.example.generator.model.request;

import lombok.Data;

@Data
public class InsertRequest {

    private String tableSchema;
    private String tableName;
    private int rowCount;

}
