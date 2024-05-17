package com.example.generator.service;

import com.example.generator.factory.GeneratorFactory;
import com.example.generator.generator.Generator;
import com.example.generator.model.request.Request;
import com.example.generator.model.response.Response;
import com.example.generator.repository.InformationSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsertService {

    private int batchSize = 100;

    private final InformationSchemaRepository repository;
    private final GeneratorFactory generatorConfigFactory;

    public Mono<Response> insert(Request request) {

        int rowCount = request.getRowCount();

        if (rowCount < 1) {
            return Mono.just(new Response("the number of rows is less than 1", 0));
        }

        Generator generator = generatorConfigFactory.get(request);



        if (generator.getColumnsInfo().isEmpty()) {
            return Mono.just(new Response("schema or table not found", 0));
        }

        return Mono.fromCallable(() -> {
            AtomicInteger insertedRows = new AtomicInteger();
            AtomicInteger insertedBatch = new AtomicInteger();

            try {


                var inserterList = new ArrayList<Mono<Integer>>();
                var batchCount = rowCount / batchSize;
                var lastBatch = rowCount % batchSize;

                for (int i = 0; i < batchCount; i++) {
                    inserterList.add(createBatch(request, batchSize, generator));
                }
                if (lastBatch > 0) {
                    inserterList.add(createBatch(request, lastBatch, generator));
                }


                StopWatch timer = new StopWatch();
                timer.start();
                Flux.fromIterable(inserterList)
                        .parallel()
                        .runOn(Schedulers.parallel())
                        .flatMap(integerMono -> integerMono)
                        .doOnNext(count -> {
                            insertedRows.addAndGet(count);
                            insertedBatch.incrementAndGet();
                            log.info("{}.{} | Inserted {} rows out of {}. Batch: {}/{}. {}%",
                                    request.getTableSchema(), request.getTableName(),
                                    insertedRows.get(), rowCount,
                                    insertedBatch.get(), inserterList.size(),
                                    ((insertedBatch.get() * 100) / inserterList.size()));
                        })
                        .reduce(Integer::sum)
                        .switchIfEmpty(Mono.just(0))
                        .doOnSuccess(integer -> {
                            timer.stop();
                            log.info("Задача выполнилась за {} секунд.", timer.getTotalTimeSeconds());
                        })
                        .block();

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (insertedRows.get() > 0) {
                return new Response("insert success", insertedRows.get());
            }
            return new Response("insert failed", 0);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Integer> createBatch(Request request, int batchSize, Generator generator) {
        return Mono.just(batchSize)
                .map(generator::generatedValues)
                .flatMap(values -> Mono.create(monoSink -> {
                            try {
                                monoSink.success(repository.insert(request.getTableSchema(),request.getTableName(), generator.getColumns(), values,request.getRowCount()));
                            } catch (DuplicateKeyException e) {
                                log.error("Error insert batch: {}", e.getMessage());
                                monoSink.success(0);
                            }
                        }));
    }

}
