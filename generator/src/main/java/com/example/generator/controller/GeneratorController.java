package com.example.generator.controller;

import com.example.generator.model.request.InsertRequest;
import com.example.generator.model.response.InsertResponse;
import com.example.generator.service.InsertService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/generator")
public class GeneratorController {

    private final InsertService insertService;

    @PostMapping("/insert")
    public Mono<InsertResponse> insertData(@RequestBody InsertRequest request) {
        return insertService.insert(request);
    }


}
