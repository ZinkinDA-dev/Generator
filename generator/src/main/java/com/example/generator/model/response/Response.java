package com.example.generator.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertResponse {

    private String message;
    private int result;
}
