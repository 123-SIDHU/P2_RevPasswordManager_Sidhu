package com.rev.app.service.impl;

import com.rev.app.dto.VaultEntryDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.VaultEntry;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.VaultEntryMapper;
import com.rev.app.repository.IVaultEntryRepository;
import com.rev.app.service.IEncryptionService;
import com.rev.app.service.IVaultService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VaultServiceImpl implements IVaultService {

    private static final Logger logger = LogManager.getLogger(VaultServiceImpl.class);

    private final IVaultEntryRepository vaultRepo;
    private final IEncryptionService encryptionService;
    private final VaultEntryMapper vaultEntryMapper;

    public VaultServiceImpl(IVaultEntryRepository vaultRepo,
            IEncryptionService encryptionService,
            VaultEntryMapper vaultEntryMapper) {
        this.vaultRepo = vaultRepo;
        this.encryptionService = encryptionService;
        this.vaultEntryMapper = vaultEntryMapper;
    }

    @Override
    public VaultEntry addEntry(User user, VaultEntryDTO dto) {
        VaultEntry entry = vaultEntryMapper.toEntity(dto, user);
        VaultEntry saved = vaultRepo.save(entry);
        logger.info("Vault entry added: '{}' for user {}", dto.getAccountName(), user.getUsername());
        return saved;
    }

    @Override
    public VaultEntry updateEntry(Long userId, Long entryId, VaultEntryDTO dto) {
        VaultEntry entry = vaultRepo.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));
        vaultEntryMapper.updateEntityFromDto(entry, dto);
        logger.info("Vault entry updated: id={} for user {}", entryId, userId);
        return vaultRepo.save(entry);
    }

    @Override
    public void deleteEntry(Long userId, Long entryId) {
        VaultEntry entry = vaultRepo.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));
        vaultRepo.delete(entry);
        logger.info("Vault entry deleted: id={} for user {}", entryId, userId);
    }

    @Override
    public void toggleFavorite(Long userId, Long entryId) {
        VaultEntry entry = vaultRepo.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));
        entry.setFavorite(!entry.isFavorite());
        vaultRepo.save(entry);
    }

    @Override
    public List<VaultEntryDTO> getAllEntries(Long userId, String search, String category, String sort) {
        List<VaultEntry> entries;

        if (search != null && !search.isBlank()) {
            entries = vaultRepo.search(userId, search.trim());
        } else if (category != null && !category.isBlank() && !category.equals("ALL")) {
            try {
                VaultEntry.Category cat = VaultEntry.Category.valueOf(category);
                entries = vaultRepo.findByUserIdAndCategory(userId, cat);
            } catch (IllegalArgumentException e) {
                entries = vaultRepo.findByUserIdOrderByAccountNameAsc(userId);
            }
        } else {
            entries = vaultRepo.findByUserIdOrderByAccountNameAsc(userId);
        }

        // Sort
        if ("date_added".equals(sort)) {
            entries.sort(Comparator.comparing(VaultEntry::getCreatedAt).reversed());
        } else if ("date_modified".equals(sort)) {
            entries.sort(Comparator.comparing(VaultEntry::getUpdatedAt).reversed());
        } else {
            entries.sort(Comparator.comparing(VaultEntry::getAccountName, String.CASE_INSENSITIVE_ORDER));
        }

        return entries.stream().map(vaultEntryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public VaultEntryDTO getEntryWithDecryptedPassword(Long userId, Long entryId) {
        VaultEntry entry = vaultRepo.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));
        return vaultEntryMapper.toDecryptedDto(entry);
    }

    @Override
    public VaultEntryDTO getEntryMasked(Long userId, Long entryId) {
        VaultEntry entry = vaultRepo.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));
        return vaultEntryMapper.toDto(entry);
    }

    @Override
    public List<VaultEntryDTO> getFavorites(Long userId) {
        return vaultRepo.findByUserIdAndFavoriteTrueOrderByAccountNameAsc(userId)
                .stream().map(vaultEntryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<VaultEntryDTO> getRecentEntries(Long userId, int limit) {
        return vaultRepo.findRecentByUserId(userId, PageRequest.of(0, limit))
                .stream().map(vaultEntryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public long countByUser(Long userId) {
        return vaultRepo.countByUserId(userId);
    }

    @Override
    public List<VaultEntry> getAllRawEntries(Long userId) {
        return vaultRepo.findByUserIdOrderByAccountNameAsc(userId);
    }

    @Override
    public String decryptPassword(VaultEntry entry) {
        return encryptionService.decrypt(entry.getEncryptedPassword());
    }
}
