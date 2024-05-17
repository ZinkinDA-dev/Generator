package com.example.generator.generator;

import com.example.generator.model.ColumnInfo;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GeneratorDefault implements Generator {

    @Getter
    private List<ColumnInfo> columnsInfo;


    private final Map<String, Supplier<String>> mapAliasFunction = new HashMap<>(){{
        put("text",() -> addQuotes(generateText(5)));
        put("numeric", () -> generateNumber(4));
        put("uuid",() -> generateUUID());
        put("timestamp without time zone", () -> generateTimestampWithoutTimeZone(65_000_000));
        put("date", () -> generateDate(1990, LocalDate.now().getYear()));
        put("boolean", () -> generateBinary());
        put("bigint", () -> generateNumber(6));
    }};

    /**
     * Arrays
     **/
    private final int[] binary = {1, 2};
    public GeneratorDefault(List<ColumnInfo> columnsInfo) {
        this.columnsInfo = columnsInfo;
    }

    public String getColumns() {
        String columns = getColumnsFromInfo();
        return columns;
    }

    public String generatedValues(int rowCountInBatch) {
        String values = generateValues(rowCountInBatch);
        return values;
    }

    protected List<String> getColumnName() {
        return columnsInfo.stream().map(ColumnInfo::getColumnName).collect(Collectors.toList());
    }


    /**
     * Default methods for any schemas
     **/

    protected String generateText(int length) {
        return new Random().ints(97, 122 + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    protected String generateDouble() {
        return String.valueOf(Math.random() * 10_000 + 10_000);
    }

    protected String generateTimestampWithoutTimeZone(int minusSecondFromToday) {
        long now = (System.currentTimeMillis() / 1000) - ((int) (Math.random() * minusSecondFromToday + 1));
        return "to_timestamp(" + now + ")";
    }

    protected String generateDateInt(int yearMin, int yearMax) {
        return new StringBuilder()
                .append((int) (Math.random() * (yearMax - yearMin) + yearMin))
                .append("-")
                .append((int) (Math.random() * 11 + 1))
                .append("-")
                .append((int) (Math.random() * 27 + 1))
                .toString();
    }

    protected String generateDate(int yearMin, int yearMax) {
        return "to_date('" + generateDateInt(yearMin, yearMax) + "', 'YYYY-MM-DD')";
    }


    /**
     * Methods use arrays
     **/

    protected String generateBinary() {
        return String.valueOf(binary[(int) (Math.random() * binary.length)]);
    }

    /**
     * Methods for special schema
     **/

    protected String addQuotes(String string) {
        return new StringBuilder(string)
                .insert(0, "'")
                .append("'")
                .toString();
    }

    protected String generateNumber(int length) {
        StringBuilder number = new StringBuilder(String.valueOf((int) (Math.random() * 9)));
        while (number.length() < length) {
            number.append(((int) (Math.random() * 9)));
        }
        return number.toString();
    }

    protected String generateUUID(){
        return UUID.randomUUID().toString();
    }

    private String generateValues(int rowCountInBatch) {
        List<String> dataTypes = getDataTypes();
        List<String> values = new ArrayList<>();

        for (int i = 0; i < rowCountInBatch; i++) {
            List<String> value = new ArrayList<>();
            AtomicReference<String> result = new AtomicReference<>();
            for (String datatype : dataTypes) {
                mapAliasFunction.forEach((key,function) -> {
                    if(key.equals(datatype)){
                        result.set(function.get());
                        return;
                    }
                });
                value.add(result.get());
            }
            values.add(value.stream().collect(Collectors.joining(", ", "(", ")")));
        }
        return String.join(", ", values);
    }

    private String getColumnsFromInfo() {
        return columnsInfo != null ? columnsInfo.stream().
                map(ColumnInfo::getColumnName).
                collect(Collectors.joining(", ", "(", ")")) : "";
    }

    private List<String> getDataTypes() {
        return columnsInfo.stream().map(ColumnInfo::getDataType).collect(Collectors.toList());
    }

}

