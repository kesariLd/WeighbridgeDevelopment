package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.VehicleMasterDto;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.payloads.VehicleGateEntryResponse;
import com.weighbridge.admin.payloads.VehicleRequest;
import com.weighbridge.admin.payloads.VehicleResponse;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.admin.services.VehicleMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleMasterController {

    @Autowired
    private VehicleMasterService vehicleMasterService;

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;
    /**
     * Endpoint for adding a new vehicle.
     * @param vehicleRequest The request body containing vehicle information.
     * @param transporterName The name of the transporter associated with the vehicle.
     * @return ResponseEntity containing a success message and HTTP status CREATED.
     */
    @PostMapping("/{transporterName}")
    public ResponseEntity<String> addVehicle(@RequestBody VehicleRequest vehicleRequest, @PathVariable String transporterName) {
        String response = vehicleMasterService.addVehicle(vehicleRequest, transporterName);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    /**
     * Endpoint for retrieving transporter details by vehicle number.
     * @param vehicleNo The vehicle number.
     * @return ResponseEntity containing transporter details and HTTP status OK.
     */
    @GetMapping("/vehicle/{vehicleNo}")
    public ResponseEntity<VehicleGateEntryResponse> getTransporterNamesByVehicle(@PathVariable String vehicleNo){
       return new ResponseEntity<>(vehicleMasterService.getTransporterDetailByVehicle(vehicleNo),HttpStatus.OK);
    }

    /**
     * Endpoint for retrieving all vehicles.
     * @param page The page number for pagination (default: 0).
     * @param size The size of each page for pagination (default: 10).
     * @param sortField The field to sort by (default: vehicleModifiedDate).
     * @param sortOrder The sort order (default: desc).
     * @return ResponseEntity containing a list of vehicles and HTTP status OK.
     */
    @GetMapping()
    public ResponseEntity<List<VehicleResponse>> getAllVehicles(@RequestParam(defaultValue = "0", required = false) int page,
                                                                @RequestParam(defaultValue = "10", required = false) int size,
                                                                @RequestParam(required = false, defaultValue = "vehicleModifiedDate") String sortField,
                                                                @RequestParam(defaultValue = "desc", required = false) String sortOrder) {

        Pageable pageable;

        if (sortField != null && !sortField.isEmpty()) {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortField);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<VehicleResponse> vehiclePage = vehicleMasterService.vehicles(pageable);
        List<VehicleResponse> vehicleLists = vehiclePage.getContent();
        return ResponseEntity.ok(vehicleLists);
    }
    /**
     * Endpoint for retrieving a vehicle by vehicle number.
     * @param vehicleNo The vehicle number.
     * @return ResponseEntity containing the requested vehicle and HTTP status OK.
     */
    @GetMapping("/{vehicleNo}")
    public ResponseEntity<VehicleResponse> getVehicleByVehicleNo(@PathVariable String vehicleNo) {
        VehicleResponse vehicleResponse = vehicleMasterService.vehicleByNo(vehicleNo);
        return ResponseEntity.ok(vehicleResponse);
    }
    /**
     * Endpoint for updating a vehicle by vehicle number.
     * @param vehicleNo The vehicle number.
     * @param vehicleRequest The request body containing updated vehicle information.
     * @return ResponseEntity containing a success message and HTTP status OK.
     */
    @PutMapping("/update/{vehicleNo}")
    public ResponseEntity<String> updateVehicle(@PathVariable String vehicleNo, @RequestBody VehicleRequest vehicleRequest){
        String response = vehicleMasterService.updateVehicleByVehicleNo(vehicleNo, vehicleRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for deleting a vehicle by vehicle number.
     * @param vehicleNo The vehicle number.
     * @return ResponseEntity containing a success message and HTTP status OK.
     */

    @DeleteMapping("/delete/{vehicleNo}")
    public ResponseEntity<String> deleteVehicle(@PathVariable String vehicleNo){
        String deletedVehicle = vehicleMasterService.deleteVehicleByVehicleNo(vehicleNo);
        return ResponseEntity.ok(deletedVehicle);
    }
}