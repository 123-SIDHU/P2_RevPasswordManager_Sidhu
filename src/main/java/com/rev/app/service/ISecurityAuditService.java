package com.rev.app.service;

import java.util.ArrayList;
import java.util.List;

public interface ISecurityAuditService {
    AuditReport generateReport(Long userId, int oldPasswordDays);

    // ====== Inner POJOs or DTOs ======
    class AuditReport {
        private int totalEntries;
        private int securityScore = 100;
        private List<AuditItem> weakPasswords = new ArrayList<>();
        private List<AuditItem> reusedPasswords = new ArrayList<>();
        private List<AuditItem> oldPasswords = new ArrayList<>();

        public int getTotalEntries() { return totalEntries; }
        public void setTotalEntries(int t) { this.totalEntries = t; }
        public int getSecurityScore() { return securityScore; }
        public void setSecurityScore(int s) { this.securityScore = Math.max(0, Math.min(100, s)); }
        public List<AuditItem> getWeakPasswords() { return weakPasswords; }
        public List<AuditItem> getReusedPasswords() { return reusedPasswords; }
        public List<AuditItem> getOldPasswords() { return oldPasswords; }
    }

    class AuditItem {
        private Long id;
        private String accountName;
        private String category;
        private int strengthScore;
        private String strengthLabel;
        private String updatedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public int getStrengthScore() { return strengthScore; }
        public void setStrengthScore(int strengthScore) { this.strengthScore = strengthScore; }
        public String getStrengthLabel() { return strengthLabel; }
        public void setStrengthLabel(String strengthLabel) { this.strengthLabel = strengthLabel; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }
}
