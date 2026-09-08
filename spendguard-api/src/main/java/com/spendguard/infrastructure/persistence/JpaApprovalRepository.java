package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.ApprovalRepository;
import com.spendguard.domain.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaApprovalRepository extends ApprovalRepository, JpaRepository<Approval, Long> {
}