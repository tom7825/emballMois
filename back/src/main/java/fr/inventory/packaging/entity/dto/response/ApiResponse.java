package fr.inventory.packaging.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String message;
    private final T data;
    private final Map<String, String> errors;


    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
        this.errors = null;
    }

    public ApiResponse(String message, Map<String, String> errors, T data) {
        this.message = message;
        this.errors = errors;
        this.data = data;
    }

    public ApiResponse(String message) {
        this.message = message;
        this.data = null;
        this.errors = null;
    }

    public ApiResponse(T data){
        this.data = data;
        message = null;
        errors = null;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
