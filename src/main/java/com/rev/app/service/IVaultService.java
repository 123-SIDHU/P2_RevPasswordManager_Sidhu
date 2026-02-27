package com.rev.app.service;

import com.rev.app.dto.VaultEntryDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.VaultEntry;

import java.util.List;

public interface IVaultService {
    VaultEntry addEntry(User user, VaultEntryDTO dto);
    VaultEntry updateEntry(Long userId, Long entryId, VaultEntryDTO dto);
    void deleteEntry(Long userId, Long entryId);
    void toggleFavorite(Long userId, Long entryId);
    List<VaultEntryDTO> getAllEntries(Long userId, String search, String category, String sort);
    VaultEntryDTO getEntryWithDecryptedPassword(Long userId, Long entryId);
    VaultEntryDTO getEntryMasked(Long userId, Long entryId);
    List<VaultEntryDTO> getFavorites(Long userId);
    List<VaultEntryDTO> getRecentEntries(Long userId, int limit);
    long countByUser(Long userId);
    List<VaultEntry> getAllRawEntries(Long userId);
    String decryptPassword(VaultEntry entry);
}
