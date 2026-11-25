package com.example.lostfound.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lostfound.model.LossReport;
import com.example.lostfound.service.LossReportService;

@RestController
@RequestMapping("/api/loss-reports")
public class LossReportController {
    private final LossReportService service;

    public LossReportController(LossReportService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<LossReport>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LossReport> getById(@PathVariable Long id) {
        LossReport report = service.getById(id);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<LossReport> create(@RequestBody LossReport report) {
        return new ResponseEntity<>(service.create(report), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @lossReportService.getById(#id).applicant.username == authentication.name")
    public ResponseEntity<LossReport> update(@PathVariable Long id, @RequestBody LossReport report) {
        LossReport updated = service.update(id, report);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @lossReportService.getById(#id).applicant.username == authentication.name")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
