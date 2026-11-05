package com.example.RagService.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RagResponse {

    String answer ;
    List<SourceResponse> sources ;

}
