package com.hyperpass.controller;

import com.hyperpass.domain.HpDepartment;
import com.hyperpass.mapper.HpDepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final HpDepartmentMapper hpDepartmentMapper;

    @GetMapping
    public ResponseEntity<List<HpDepartment>> getAll() {
        return ResponseEntity.ok(hpDepartmentMapper.findAll());
    }
}
