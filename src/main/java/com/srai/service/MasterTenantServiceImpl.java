
package com.srai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.srai.model.MasterTenant;
import com.srai.model.repository.MasterTenantRepository;

@Service
public class MasterTenantServiceImpl implements MasterTenantService {

	@Autowired
	private MasterTenantRepository masterTenantRepo;

	@Override
	public MasterTenant findByTenantId(String tenantId) {
		return masterTenantRepo.findByTenantId(tenantId);
	}
}
