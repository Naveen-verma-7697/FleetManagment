package com.fleman.controller;

import com.fleman.dto.AddonDTO;
import com.fleman.service.AddonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/addons")
public class AddonController {

    private final AddonService addonService;

    public AddonController(AddonService addonService) {
        this.addonService = addonService;
    }

    @GetMapping
    public ResponseEntity<List<AddonDTO>> getAddons() {
        return ResponseEntity.ok(addonService.getAddons());
    }
}
