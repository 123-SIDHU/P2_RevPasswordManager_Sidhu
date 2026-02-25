package com.rev.app.mapper;

import com.rev.app.dto.CredentialDTO;
import com.rev.app.entity.Credential;

public class CredentialMapper {
    public static CredentialDTO toDTO(Credential cred) {
        if (cred == null) return null;
        CredentialDTO dto = new CredentialDTO();
        dto.setId(cred.getId());
        dto.setAccountName(cred.getAccountName());
        dto.setUsername(cred.getUsername());
        dto.setUrl(cred.getUrl());
        dto.setNotes(cred.getNotes());
        dto.setCategory(cred.getCategory());
        dto.setFavorite(cred.isFavorite());
        return dto;
    }
}
