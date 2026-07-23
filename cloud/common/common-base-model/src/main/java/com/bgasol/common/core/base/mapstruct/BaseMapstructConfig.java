package com.bgasol.common.core.base.mapstruct;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BaseMapstructConfig {
}
