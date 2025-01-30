package de.se.cashregistersystem.dto;

public class CompleteOrderResponseDTO {

    private final String message;
    public CompleteOrderResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
