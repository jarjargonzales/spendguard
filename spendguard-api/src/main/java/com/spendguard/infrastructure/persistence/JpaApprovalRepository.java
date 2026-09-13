package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.ApprovalRepository;
import com.spendguard.domain.entity.Approval;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaApprovalRepository extends ApprovalRepository, JpaRepository<Approval, Long> {
	@Override
default List<Approval> findByDecisionAndCreatedBefore(String decision, Timestamp threshold) {
        return findAll().stream()
                .filter(a -> decision.equals(a.getDecision())
                        && a.getCreated() != null
                        && a.getCreated().before(threshold))
                .toList();
    }
}