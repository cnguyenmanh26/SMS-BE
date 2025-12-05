package com.sms.smsbackend.repository;

import com.sms.smsbackend.entity.ApiCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiCredentialRepository extends JpaRepository<ApiCredential, Long> {
    
    Optional<ApiCredential> findByAppId(String appId);
    
    Boolean existsByAppId(String appId);
}
