package com.rev.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SecurityQuestionDTO {

    private Long id;

    @NotBlank(message = "Question is required")
    private String questionText;

    @NotBlank(message = "Answer is required")
    private String answer;
}
