package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.ProductMasterDto;
import com.weighbridge.admin.entities.*;
import com.weighbridge.admin.payloads.ProductWithParameters;
import com.weighbridge.admin.repsitories.ProductMasterRepository;
import com.weighbridge.admin.repsitories.ProductTypeMasterRepository;
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
    private final ProductTypeMasterRepository productTypeMasterRepository;
    private final QualityRangeMasterRepository qualityRangeMasterRepository;
    private final ModelMapper modelMapper;

    public ProductMasterServiceImpl(HttpServletRequest httpServletRequest, ProductMasterRepository productMasterRepository, ProductTypeMasterRepository productTypeMasterRepository, QualityRangeMasterRepository qualityRangeMasterRepository, ModelMapper modelMapper) {
        this.httpServletRequest = httpServletRequest;
        this.productMasterRepository = productMasterRepository;
        this.productTypeMasterRepository = productTypeMasterRepository;
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
            productMaster.setProductCreatedBy(user);
            productMaster.setProductCreatedDate(currentDateTime);
            productMaster.setProductModifiedBy(user);
            productMaster.setProductModifiedDate(currentDateTime);
            productMaster = productMasterRepository.save(productMaster);
        }

        ProductTypeMaster productTypeMaster = productTypeMasterRepository.findByProductTypeName(request.getProductTypeName());
        if (request.getProductTypeName() != null && productTypeMaster == null) {
            productTypeMaster = new ProductTypeMaster();
            productTypeMaster.setProductTypeName(request.getProductTypeName());
            productTypeMaster.setProductMaster(productMaster);
            productTypeMasterRepository.save(productTypeMaster);
        }

        List<QualityRangeMaster> qualityRangeMasters = createQualityRanges(request.getParameters(), productMaster);
        qualityRangeMasterRepository.saveAll(qualityRangeMasters);
        return "Product saved successfully";
    }

    @Override
    public List<ProductMasterDto> getAllProducts() {
        // Fetch all products
        List<ProductMaster> listOfProducts = productMasterRepository.findAll();

        return listOfProducts.stream()
                .map(productMaster -> modelMapper.map(productMaster, ProductMasterDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllProductNames() {
        List<String> listOfProductNames = productMasterRepository.findAllProductNameByProductStatus("ACTIVE");
        return listOfProductNames;
    }

    @Override
    public List<String> getTypeWithProduct(String productName) {
        List<String> allProductTypeNames = productTypeMasterRepository.findByProductMasterProductName(productName);
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
