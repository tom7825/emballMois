package fr.inventory.packaging.entity.dto;

import fr.inventory.packaging.entity.dto.validation.LineValidationDto;

public record AreaReferenceDto(String areaName, LineValidationDto reference) {
}
