package com.weighbridge.SalesManagement.service.serviceImpl;

import com.weighbridge.SalesManagement.entities.SalesOrder;
import com.weighbridge.SalesManagement.payloads.SalesOrderRequest;
import com.weighbridge.SalesManagement.repositories.SalesOrderRespository;
import com.weighbridge.SalesManagement.service.SalesOrderService;
import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {
    @Autowired
    SalesOrderRespository salesOrderRespository;

    @Autowired
    CustomerMasterRepository customerMasterRepository;

    @Autowired
    MaterialMasterRepository materialMasterRepository;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");


    /**
     *
     * @param salesOrderRequest should contain a unique salesOrderNo for each request
     * @return
     */
    @Override
    public String AddSalesDetails(SalesOrderRequest salesOrderRequest){
        Boolean salesOrder1 = salesOrderRespository.existsBySaleOrderNo(salesOrderRequest.getSaleOrderNo());
        if(salesOrder1){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"this salesNumber already exists");
        }
        SalesOrder salesOrder=new SalesOrder();
        salesOrder.setPurchaseOrderNo(generatePurchaseOrderNo());
        salesOrder.setSaleOrderNo(salesOrderRequest.getSaleOrderNo());
        salesOrder.setPurchaseOrderedDate(salesOrderRequest.getPurchaseOrderedDate());
        salesOrder.setOrderedQuantity(salesOrderRequest.getOrderedQuantity());
        salesOrder.setBrokerName(salesOrderRequest.getBrokerName());

      //Added customer to CustomerMaster if customer doesnot exist
       String address=salesOrderRequest.getCustomerAddress();
        String addressLine1=null;
        String addressLine2=null;
       if (address!=null) {
           String[] parts = address.split(",", 2);
           addressLine1=parts[0].trim();
           addressLine2=parts[1].trim();
       }
        Boolean customer = customerMasterRepository.existsByCustomerNameAndCustomerAddressLine1AndCustomerAddressLine2(salesOrderRequest.getCustomerName(), addressLine1, addressLine2);
        if(!customer){
            CustomerMaster customerMaster=new CustomerMaster();
            customerMaster.setCustomerName(salesOrderRequest.getCustomerName());
            customerMaster.setCustomerContactNo(salesOrderRequest.getCustomerContact());
            customerMaster.setCustomerEmail(salesOrderRequest.getCustomerEmail());
            customerMaster.setCustomerAddressLine1(addressLine1);
            customerMaster.setCustomerAddressLine2(addressLine2);
            customerMasterRepository.save(customerMaster);
        }

        salesOrder.setCustomerName(salesOrderRequest.getCustomerName());
        salesOrder.setCustomerAddress(salesOrderRequest.getCustomerAddress());
        salesOrder.setCustomerContact(salesOrderRequest.getCustomerContact());
        salesOrder.setCustomerEmail(salesOrderRequest.getCustomerEmail());
        salesOrder.setBrokerAddress(salesOrderRequest.getBrokerAddress());


        //Added material to material master if doesnot exist in MaterialMaster
        boolean material = materialMasterRepository.existsByMaterialName(salesOrderRequest.getProductName());
        if(!material){
            MaterialMaster materialMaster=new MaterialMaster();
            materialMaster.setMaterialName(salesOrderRequest.getProductName());
            materialMasterRepository.save(materialMaster);
        }

        salesOrder.setProductName(salesOrderRequest.getProductName());
        salesOrder.setProgressiveQuantity(salesOrderRequest.getProgressiveQuantity());
        salesOrder.setBalanceQuantity(salesOrderRequest.getOrderedQuantity()-salesOrderRequest.getProgressiveQuantity());
        salesOrderRespository.save(salesOrder);
        return "Sales details added";
    }

    public String generatePurchaseOrderNo() {
        LocalDate currentDate = LocalDate.now();
        String formattedDate = dateFormatter.format(currentDate);

        // Assuming you have a method countByPurchaseNo in your repository or service
        Long count=salesOrderRespository.countByPurchaseOrderNoStartingWith(formattedDate);

        // Increment the count for the current day and format it as a 2-digit string
        String incrementedNumber = String.format("%02d", count + 1);

        // Combine the formatted date and incremented number to create the purchase pass number
        String purchaseOrderId = formattedDate + incrementedNumber;

        return purchaseOrderId;
    }
}