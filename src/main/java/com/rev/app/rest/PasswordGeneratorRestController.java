package com.rev.app.rest;

import com.rev.app.dto.PasswordGeneratorConfigDTO;
import com.rev.app.dto.PasswordResultDTO;
import com.rev.app.service.IPasswordGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/generator")
public class PasswordGeneratorRestController {

    private final IPasswordGeneratorService generatorService;

    public PasswordGeneratorRestController(IPasswordGeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<List<PasswordResultDTO>> generatePasswords(@RequestBody PasswordGeneratorConfigDTO config) {
        // Enforce limits to prevent abuse
        if (config.getLength() < 8)
            config.setLength(8);
        if (config.getLength() > 128)
            config.setLength(128);
        if (config.getCount() < 1)
            config.setCount(1);
        if (config.getCount() > 50)
            config.setCount(50);

        List<String> generated = generatorService.generate(config);
        List<PasswordResultDTO> results = generated.stream()
                .map(pwd -> new PasswordResultDTO(
                        pwd,
                        generatorService.strengthScore(pwd),
                        generatorService.strengthLabel(generatorService.strengthScore(pwd))))
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
}
