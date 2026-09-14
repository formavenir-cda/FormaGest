package com.eni.formagest.controllers.training;

import com.eni.formagest.bll.training.SectorService;
import com.eni.formagest.dto.training.SectorDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/sectors")
public class SectorController {

    private final SectorService sectorService;

    public SectorController(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    @GetMapping
    public List<SectorDto> findAll() {
        return sectorService.findAll();
    }
}