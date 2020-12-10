package com.springproject.beautysaloon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoneResponse {
    private Long id;
    private Boolean value;
}
