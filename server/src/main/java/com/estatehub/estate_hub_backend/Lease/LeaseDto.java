package com.estatehub.estate_hub_backend.Lease;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.estatehub.estate_hub_backend.Property.PropertyDto;
import com.estatehub.estate_hub_backend.Tenant.TenantDto;

public record LeaseDto(
    Long id,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal monthlyRent,
    BigDecimal securityDeposit,
    String leasestatus,
    PropertyDto property,
    TenantDto tenant,
    String leaseDocumentUrl
) {}

