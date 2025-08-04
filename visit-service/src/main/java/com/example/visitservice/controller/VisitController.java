package com.example.visitservice.controller;

import com.example.visitservice.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visit")
public class VisitController {

    private final VisitService visitService;


}
