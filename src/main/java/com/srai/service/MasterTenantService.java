
package com.srai.service;

import org.springframework.data.repository.query.Param;

import com.srai.model.MasterTenant;


public interface MasterTenantService {
    
    MasterTenant findByTenantId(@Param("tenantId") String tenantId);
}
