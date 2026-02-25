package com.rev.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rev.app.entity.Credential;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Integer> {
    List<Credential> findByUserId(int userId);

    @Query("SELECT c FROM Credential c WHERE c.userId = :userId AND " +
            "(LOWER(c.accountName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.url) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Credential> searchCredentials(@Param("userId") int userId, @Param("keyword") String keyword);

    List<Credential> findByUserIdOrderByAccountNameAsc(int userId);
    List<Credential> findByUserIdOrderByCreatedAtDesc(int userId);
    List<Credential> findByUserIdOrderByUpdatedAtDesc(int userId);

    List<Credential> findByUserIdAndCategory(int userId, String category);
    List<Credential> findByUserIdAndIsFavoriteTrue(int userId);

    long countByUserId(int userId);
}
