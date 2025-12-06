package com.example.demo.dto.material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic API response wrapper for material-related endpoints.
 * @param <T> the type of data contained in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialApiResponse<T> {
    private Integer status;
    private String message;
    private T data;
}
