package com.example.AuthService.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;


@Data
public class DownstreamErrorResponse {

    private boolean success;

    private int status;

    private String code;

    private String message;

    private JsonNode data;

    private JsonNode timestamp;
}
