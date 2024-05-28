package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.payloads.MaterialMasterResponse;
import com.weighbridge.admin.payloads.ProductMasterResponse;
import com.weighbridge.admin.entities.ProductMaster;
import com.weighbridge.admin.entities.QualityRangeMaster;
import com.weighbridge.admin.payloads.ProductWithParameters;
import com.weighbridge.admin.repsitories.ProductMasterRepository;
import com.weighbridge.admin.repsitories.QualityRangeMasterRepository;
import com.weighbridge.admin.services.ProductMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductMasterServiceImpl implements ProductMasterService {
    private final HttpServletRequest httpServletRequest;
    private final ProductMasterRepository productMasterRepository;
    private final QualityRangeMasterRepository qualityRangeMasterRepository;
    private final ModelMapper modelMapper;

    public ProductMasterServiceImpl(HttpServletRequest httpServletRequest, ProductMasterRepository productMasterRepository, QualityRangeMasterRepository qualityRangeMasterRepository, ModelMapper modelMapper) {
        this.httpServletRequest = httpServletRequest;
        this.productMasterRepository = productMasterRepository;
        this.qualityRangeMasterRepository = qualityRangeMasterRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public String createProductWithParameterAndRange(ProductWithParameters request) {
        HttpSession session = httpServletRequest.getSession();
        String user = session.getAttribute("userId").toString();
        LocalDateTime currentDateTime = LocalDateTime.now();

        ProductMaster productMaster = productMasterRepository.findByProductName(request.getProductName());
        if (productMaster == null) {
            productMaster = new ProductMaster();
            productMaster.setProductName(request.getProductName());
            if (request.getProductTypeName() != null) {
                productMaster.setProductTypeName(request.getProductTypeName());
            }
            productMaster.setProductCreatedBy(user);
            productMaster.setProductCreatedDate(currentDateTime);
            productMaster.setProductModifiedBy(user);
            productMaster.setProductModifiedDate(currentDateTime);
            productMaster = productMasterRepository.save(productMaster);
        }

        List<QualityRangeMaster> qualityRangeMasters = createQualityRanges(request.getParameters(), productMaster);
        qualityRangeMasterRepository.saveAll(qualityRangeMasters);
        return "Product saved successfully";
    }

    @Override
    public List<ProductMasterResponse> getAllProducts() {
        List<ProductMaster> productMasters = productMasterRepository.findAllByProductStatus("ACTIVE");
        List<ProductMasterResponse> productMasterResponses = new ArrayList<>();

        for (ProductMaster productMaster : productMasters) {
            List<QualityRangeMaster> qualityRangeMasters = qualityRangeMasterRepository.findByProductId(productMaster.getProductId());

            for (QualityRangeMaster qualityRangeMaster : qualityRangeMasters) {
                ProductMasterResponse productMasterResponse = new ProductMasterResponse();
                productMasterResponse.setId(qualityRangeMaster.getQualityRangeId());
                productMasterResponse.setProductName(productMaster.getProductName());
                productMasterResponse.setProductTypeName(productMaster.getProductTypeName());
                productMasterResponse.setParameterName(qualityRangeMaster.getParameterName());
                productMasterResponse.setRangeFrom(qualityRangeMaster.getRangeFrom());
                productMasterResponse.setRangeTo(qualityRangeMaster.getRangeTo());

                productMasterResponses.add(productMasterResponse);

            }
        }
        return productMasterResponses;
    }

    @Override
    public List<String> getAllProductNames() {
        List<String> listOfProductNames = productMasterRepository.findAllProductNameByProductStatus("ACTIVE");
        return listOfProductNames;
    }

    @Override
    public List<String> getTypeWithProduct(String productName) {
        List<String> allProductTypeNames = productMasterRepository.findProductTypeNamesByProductName(productName);
        return allProductTypeNames;
    }

    @Override
    public void deleteProduct(String productName) {
        ProductMaster productMaster = productMasterRepository.findByProductName(productName);
        productMasterRepository.delete(productMaster);
    }

    @Override
    public List<ProductWithParameters> getQualityRangesByProductName(String productName) {
        List<QualityRangeMaster> qualityRangeMasters = qualityRangeMasterRepository.findByProductMasterProductName(productName);
        return mapQualityRangesToProductWithParameters(qualityRangeMasters);
    }

    private List<ProductWithParameters> mapQualityRangesToProductWithParameters(List<QualityRangeMaster> qualityRangeMasters) {
        Map<String, ProductWithParameters> productWithParametersMap = new HashMap<>();
        for (QualityRangeMaster qualityRangeMaster : qualityRangeMasters) {
            String key = qualityRangeMaster.getProductMaster().getProductName();
            ProductWithParameters productWithParameters = productWithParametersMap.get(key);
            if (productWithParameters == null) {
                productWithParameters = new ProductWithParameters();
                productWithParameters.setProductName(qualityRangeMaster.getProductMaster().getProductName());
                productWithParameters.setProductTypeName(null);
                productWithParameters.setParameters(new ArrayList<>());
                productWithParametersMap.put(key, productWithParameters);
            }

            ProductWithParameters.Parameter parameter = new ProductWithParameters.Parameter();
            parameter.setParameterName(qualityRangeMaster.getParameterName());
            parameter.setRangeFrom(qualityRangeMaster.getRangeFrom());
            parameter.setRangeTo(qualityRangeMaster.getRangeTo());

            productWithParameters.getParameters().add(parameter);
        }

        return new ArrayList<>(productWithParametersMap.values());
    }

    private List<QualityRangeMaster> createQualityRanges(List<ProductWithParameters.Parameter> parameters, ProductMaster productMaster) {

        return parameters.stream()
                .map(parameter -> {
                    QualityRangeMaster qualityRangeMaster = new QualityRangeMaster();
                    if (!qualityRangeMasterRepository.existsByParameterNameAndProductMasterProductId(parameter.getParameterName(), productMaster.getProductId())) {
                        qualityRangeMaster.setParameterName(parameter.getParameterName());
                        qualityRangeMaster.setRangeFrom(parameter.getRangeFrom());
                        qualityRangeMaster.setRangeTo(parameter.getRangeTo());
                        qualityRangeMaster.setProductMaster(productMaster);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  parameter.getParameterName()+" is already set");
                    }
                    return qualityRangeMaster;
                })
                .collect(Collectors.toList());
    }
}
