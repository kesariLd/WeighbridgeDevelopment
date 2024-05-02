package com.weighbridge.admin.services;

import java.util.List;

public interface ParameterMasterService {
    long createParameter(String parameterName);

    List<String> getAllParameterNames();

}
