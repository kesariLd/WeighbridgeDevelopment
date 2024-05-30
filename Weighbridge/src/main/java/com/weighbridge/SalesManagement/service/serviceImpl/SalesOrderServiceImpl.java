package com.weighbridge.SalesManagement.service.serviceImpl;

import com.weighbridge.SalesManagement.entities.SalesOrder;
import com.weighbridge.SalesManagement.entities.SalesProcess;
import com.weighbridge.SalesManagement.payloads.SalesDashboardResponse;
import com.weighbridge.SalesManagement.payloads.SalesDetailResponse;
import com.weighbridge.SalesManagement.payloads.SalesOrderRequest;
import com.weighbridge.SalesManagement.payloads.VehicleAndTransporterDetail;
import com.weighbridge.SalesManagement.repositories.SalesOrderRespository;
import com.weighbridge.SalesManagement.repositories.SalesProcessRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {
    @Autowired
    SalesOrderRespository salesOrderRespository;

    @Autowired
    CustomerMasterRepository customerMasterRepository;

    @Autowired
    MaterialMasterRepository materialMasterRepository;

    @Autowired
    SalesProcessRepository salesProcessRepository;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");


    /**
     *
     * @param salesOrderRequest should contain a unique salesOrderNo for each request
     * @return
     */
    @Override
    public String AddSalesDetails(SalesOrderRequest salesOrderRequest){
      /*  Boolean salesOrder1 = salesOrderRespository.existsBySaleOrderNo(salesOrderRequest.getSaleOrderNo());
        if(salesOrder1){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"this salesNumber already exists");
        }*/
        SalesOrder salesOrder=new SalesOrder();
        salesOrder.setPurchaseOrderNo(salesOrderRequest.getPurchaseOrderNo());
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
       /*
        Boolean customer = customerMasterRepository.existsByCustomerNameAndCustomerAddressLine1AndCustomerAddressLine2(salesOrderRequest.getCustomerName(), addressLine1, addressLine2);
        if(!customer){
            CustomerMaster customerMaster=new CustomerMaster();
            customerMaster.setCustomerName(salesOrderRequest.getCustomerName());
            customerMaster.setCustomerContactNo(salesOrderRequest.getCustomerContact());
            customerMaster.setCustomerEmail(salesOrderRequest.getCustomerEmail());
            customerMaster.setCustomerAddressLine1(addressLine1);
            customerMaster.setCustomerAddressLine2(addressLine2);
            customerMasterRepository.save(customerMaster);
        }*/
        System.out.println("printCustomer "+salesOrderRequest.getCustomerName()+","+salesOrderRequest.getCustomerAddress());
        Long customerIdByCustomerNameAndAddressLines = customerMasterRepository.findCustomerIdByCustomerNameAndAddressLines(salesOrderRequest.getCustomerName(), addressLine1, addressLine2);
        salesOrder.setCustomerId(customerIdByCustomerNameAndAddressLines);
        salesOrder.setBrokerAddress(salesOrderRequest.getBrokerAddress());


        //Added material to material master if doesnot exist in MaterialMaster
     /*   boolean material = materialMasterRepository.existsByMaterialName(salesOrderRequest.getProductName());
        if(!material){
            MaterialMaster materialMaster=new MaterialMaster();
            materialMaster.setMaterialName(salesOrderRequest.getProductName());
            materialMasterRepository.save(materialMaster);
        }
*/
        salesOrder.setProductName(salesOrderRequest.getProductName());
       // salesOrder.setProgressiveQuantity(salesOrderRequest.getProgressiveQuantity());
        salesOrder.setBalanceQuantity(salesOrderRequest.getOrderedQuantity());
        salesOrderRespository.save(salesOrder);
        return "Sales details added";
    }

    @Override
    public List<SalesDashboardResponse> getAllSalesDetails() {
        List<SalesOrder> allSales = salesOrderRespository.findAll();
        List<SalesDashboardResponse> list = new ArrayList<>();
        for(SalesOrder salesOrder : allSales) {
            SalesDashboardResponse salesDashboardResponse = new SalesDashboardResponse();
            salesDashboardResponse.setPurchaseOrderNo(salesOrder.getPurchaseOrderNo());
            salesDashboardResponse.setOrderedQty(salesOrder.getOrderedQuantity());
            CustomerMaster byId = customerMasterRepository.findById(salesOrder.getCustomerId()).get();
            salesDashboardResponse.setCustomerName(byId.getCustomerName());
            salesDashboardResponse.setSaleOrderNo(salesOrder.getSaleOrderNo());
            salesDashboardResponse.setProductName(salesOrder.getProductName());
            salesDashboardResponse.setBrokerName(salesOrder.getBrokerName());
            salesDashboardResponse.setProgressiveQty(salesOrder.getProgressiveQuantity());
            salesDashboardResponse.setBalanceQty(salesOrder.getBalanceQuantity());
            // Assuming getPurchasePassNo() is a method of SalesProcess, not List<SalesProcess>
            list.add(salesDashboardResponse);
        }
        return list;
    }

    /**
     * @return
     */
    @Override
    public SalesDetailResponse getSalesDetails(String saleOrderNo) {
        SalesOrder byPurchaseOrderNo = salesOrderRespository.findBySaleOrderNo(saleOrderNo);
        SalesDetailResponse salesDetailResponse=new SalesDetailResponse();
        salesDetailResponse.setProductName(byPurchaseOrderNo.getProductName());
        salesDetailResponse.setSaleOrderNo(byPurchaseOrderNo.getSaleOrderNo());
        salesDetailResponse.setBalanceWeight(byPurchaseOrderNo.getBalanceQuantity());
        return salesDetailResponse;
    }


    public List<VehicleAndTransporterDetail> getVehiclesAndTransporterDetails(){
        List<SalesProcess> allVehiclesDetails= salesProcessRepository.findAllByStatus(true);
        List<VehicleAndTransporterDetail> listOfVehicle=new ArrayList<>();
        for (SalesProcess salesProcess:allVehiclesDetails) {
            VehicleAndTransporterDetail vehicleAndTransporterDetail = new VehicleAndTransporterDetail();
            vehicleAndTransporterDetail.setSalePassNo(salesProcess.getSalePassNo());
            vehicleAndTransporterDetail.setTransporterName(salesProcess.getTransporterName());
            vehicleAndTransporterDetail.setVehicleNo(salesProcess.getVehicleNo());
            vehicleAndTransporterDetail.setProductName(salesProcess.getProductName());
            vehicleAndTransporterDetail.setProductType(salesProcess.getProductType());
            vehicleAndTransporterDetail.setConsignmentWeight(salesProcess.getConsignmentWeight());
            Object[] customerNameAndAddressBycustomerId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(salesProcess.getPurchaseSale().getCustomerId());
            Object[] customerData = (Object[]) customerNameAndAddressBycustomerId[0];
            if (customerData != null && customerData.length >= 2) {
                String customerName = (String) customerData[0];
                String customerAddress1 = (String) customerData[1];
                vehicleAndTransporterDetail.setCustomerName(customerName);
                vehicleAndTransporterDetail.setCustomerAddress(customerAddress1);
            }
            //System.out.println(customerNameAndAddressBycustomerId[1]);
           // vehicleAndTransporterDetail.setCustomerAddress();
            vehicleAndTransporterDetail.setSaleOrderNo(salesProcess.getPurchaseSale().getSaleOrderNo());
            vehicleAndTransporterDetail.setPurchaseOrderNo(salesProcess.getPurchaseSale().getPurchaseOrderNo());
            vehicleAndTransporterDetail.setSaleOrderDate(salesProcess.getPurchaseSale().getPurchaseOrderedDate());
            listOfVehicle.add(vehicleAndTransporterDetail);
        }
        return listOfVehicle;
    }


    public VehicleAndTransporterDetail getBySalePassNo(String salePassNo){
        SalesProcess bySalePassNo = salesProcessRepository.findBySalePassNo(salePassNo);
        VehicleAndTransporterDetail vehicleAndTransporterDetail = new VehicleAndTransporterDetail();
        vehicleAndTransporterDetail.setSalePassNo(bySalePassNo.getSalePassNo());
        vehicleAndTransporterDetail.setTransporterName(bySalePassNo.getTransporterName());
        vehicleAndTransporterDetail.setVehicleNo(bySalePassNo.getVehicleNo());
        vehicleAndTransporterDetail.setProductName(bySalePassNo.getProductName());
        vehicleAndTransporterDetail.setProductType(bySalePassNo.getProductType());
        vehicleAndTransporterDetail.setConsignmentWeight(bySalePassNo.getConsignmentWeight());
        Object[] customerNameAndAddressBycustomerId = customerMasterRepository.findCustomerNameAndAddress1andAddress2ByCustomerId(bySalePassNo.getPurchaseSale().getCustomerId());
        Object[] customerData = (Object[]) customerNameAndAddressBycustomerId[0];
        if (customerData != null && customerData.length >= 2) {
            String customerName = (String) customerData[0];
            String customerAddress1 = (String) customerData[1];
            String customerAddress2 = (String) customerData[2];
            String customerAddress = customerAddress1 + "," + customerAddress2;
            vehicleAndTransporterDetail.setCustomerName(customerName);
            vehicleAndTransporterDetail.setCustomerAddress(customerAddress);
            vehicleAndTransporterDetail.setCustomerName(customerName);
            vehicleAndTransporterDetail.setCustomerAddress(customerAddress);
        }
        //System.out.println(customerNameAndAddressBycustomerId[1]);
        // vehicleAndTransporterDetail.setCustomerAddress();
        vehicleAndTransporterDetail.setSaleOrderNo(bySalePassNo.getPurchaseSale().getSaleOrderNo());
        vehicleAndTransporterDetail.setPurchaseOrderNo(bySalePassNo.getPurchaseSale().getPurchaseOrderNo());
        vehicleAndTransporterDetail.setSaleOrderDate(bySalePassNo.getPurchaseSale().getPurchaseOrderedDate());
        System.out.println(bySalePassNo.getPurchaseSale().getPurchaseOrderedDate());
        return vehicleAndTransporterDetail;
    }

}