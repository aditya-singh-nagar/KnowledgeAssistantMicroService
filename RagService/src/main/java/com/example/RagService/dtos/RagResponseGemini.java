package com.example.RagService.dtos;

import java.util.List;

public record RagResponseGemini(String answer ,
                                boolean answerFound ,
                                List<SourceResponse> source
                                 ) {



}
