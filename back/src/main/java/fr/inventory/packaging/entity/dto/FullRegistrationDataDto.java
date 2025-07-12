package fr.inventory.packaging.entity.dto;

public class FullRegistrationDataDto {

    private final StockRegistrationDto stockRegistration;
    private final PackagingReferenceDto reference;

    public FullRegistrationDataDto(PackagingReferenceDto packagingReferenceDto, StockRegistrationDto stockRegistrationDto) {
        this.reference = packagingReferenceDto;
        this.stockRegistration = stockRegistrationDto;
    }

    public StockRegistrationDto getStockRegistration() {
        return stockRegistration;
    }

    public PackagingReferenceDto getReference() {
        return reference;
    }
}
