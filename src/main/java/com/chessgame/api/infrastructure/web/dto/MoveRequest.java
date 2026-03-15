package com.chessgame.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MoveRequest(
    @NotBlank @Pattern(regexp = "[a-h][1-8]") String source,
    @NotBlank @Pattern(regexp = "[a-h][1-8]") String target
) {}