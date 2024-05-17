package com.example.generator.factory;

import com.example.generator.generator.Generator;
import com.example.generator.generator.GeneratorDefault;
import com.example.generator.model.request.Request;
import com.example.generator.repository.InformationSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("generatorFactory")
public class GeneratorFactory{

    private final InformationSchemaRepository repository;

    public Generator get(Request request) {
        var columnsInfo = repository.getColumn(request.getTableSchema(),request.getTableName());
        //Используем switch чтобы в последствии избежать if-else ветвления.
        return new GeneratorDefault(
                columnsInfo
        );
    }

}
