package com.fleman.service.impl;

import com.fleman.dto.AddonDTO;
import com.fleman.entity.Addon;
import com.fleman.repository.AddonRepository;
import com.fleman.service.AddonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddonServiceImpl implements AddonService {

    private final AddonRepository addonRepository;

    public AddonServiceImpl(AddonRepository addonRepository) {
        this.addonRepository = addonRepository;
    }

    @Override
    public List<AddonDTO> getAddons() {
        return addonRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private AddonDTO toDto(Addon a) {
        AddonDTO dto = new AddonDTO();
        dto.setAddonId(a.getAddonId());
        dto.setAddonName(a.getAddonName());
        dto.setDailyRate(a.getDailyRate());
        dto.setRateValidFrom(a.getRateValidFrom());
        dto.setRateValidTo(a.getRateValidTo());
        dto.setDescription(a.getDescription());
        return dto;
    }
}
