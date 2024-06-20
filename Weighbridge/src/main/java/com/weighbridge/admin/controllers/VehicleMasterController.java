package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.TransporterDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> addVehicle(@RequestBody VehicleRequest vehicleRequest, @PathVariable String transporterName,@RequestParam String userId) {
        String response = vehicleMasterService.addVehicle(vehicleRequest, transporterName,userId);
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
    public ResponseEntity<String> updateVehicle(@PathVariable String vehicleNo, @RequestBody VehicleRequest vehicleRequest,@RequestParam String userId){
        String response = vehicleMasterService.updateVehicleByVehicleNo(vehicleNo, vehicleRequest,userId);
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

//    @GetMapping("/{vehicleId}")
//    public ResponseEntity<VehicleMasterDto> getVehicleById(@PathVariable Long vehicleId) {
//        VehicleMasterDto vehicleMasterDto = vehicleMasterService.getVehicleById(vehicleId);
//        return ResponseEntity.ok(vehicleMasterDto);
//    }

    /**
     * Endpoint to update a vehicle by its ID.
     *
     * @param vehicleId  The unique identifier of the vehicle.
     * @param vehicleDto The vehicle details to be updated.
     * @return ResponseEntity containing a success message with HTTP status OK.
     */
    @PutMapping("/{vehicleId}")
    public ResponseEntity<String> updateVehicleById(@PathVariable Long vehicleId, @RequestBody VehicleMasterDto vehicleDto,@RequestParam String userId) {
        String response = vehicleMasterService.updateVehicleById(vehicleId, vehicleDto,userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{vehicleId}/deactivate")
    public ResponseEntity<Void> deleteTransporterById(@PathVariable Long vehicleId) {
        boolean deactivated = vehicleMasterService.deactivateVehicleById(vehicleId);
        if (deactivated) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{vehicleId}/activate")
    public ResponseEntity<Void> activateTransporterById(@PathVariable Long vehicleId) {
        boolean activated = vehicleMasterService.activateVehicleById(vehicleId);
        if (activated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}