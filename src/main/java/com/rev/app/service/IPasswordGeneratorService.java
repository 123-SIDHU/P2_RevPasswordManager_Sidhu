package com.rev.app.service;

import com.rev.app.dto.PasswordGeneratorConfigDTO;

import java.util.List;

public interface IPasswordGeneratorService {
    List<String> generate(PasswordGeneratorConfigDTO config);
    int strengthScore(String password);
    String strengthLabel(int score);
}
