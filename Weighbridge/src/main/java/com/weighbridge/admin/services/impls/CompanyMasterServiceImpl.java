package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.CompanyMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.dtos.CompanyDto;
import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.services.CompanyMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyMasterServiceImpl implements CompanyMasterService {


    public final CompanyMasterRepository companyMasterRepository;
    // Model Mapper for convert Dto to Entity
    /*
        DTO classes to encapsulate data and pass it around your application without exposing the internal details of your entities
     */
    public final ModelMapper modelMapper;
    @Autowired
    private HttpServletRequest request;

    /**
     * createCompany method for createCompany it takes the argument of CompanyMasterDto
     * @param companyDto
     * @return
     */
    @Override
    public String createCompany(CompanyDto companyDto,String userId) {
        try {
            // Check if the company name already exists
            CompanyMaster existingCompany = companyMasterRepository.findByCompanyName(companyDto.getCompanyName());
            if (existingCompany != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Company name already exists");
            }
            if(userId==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please Provide UserId");
            }

            // Set user session details
            /*HttpSession session = request.getSession();
            String userId;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
            }
            else {
                throw new SessionExpiredException( "Session Expired, Login again !");
            }*/

            CompanyMaster newCompany = new CompanyMaster();
            newCompany.setCompanyId(generateCompanyId(companyDto.getCompanyName()));
            newCompany.setCompanyName(companyDto.getCompanyName());
            newCompany.setCompanyEmail(companyDto.getCompanyEmail());
            newCompany.setCompanyContactNo(companyDto.getCompanyContactNo());
            newCompany.setCompanyAddress(companyDto.getCompanyAddress());
            newCompany.setCompanyCreatedBy(userId);
            newCompany.setCompanyCreatedDate(LocalDateTime.now());

            newCompany.setCompanyModifiedBy(userId);
            newCompany.setCompanyModifiedDate(LocalDateTime.now());

            CompanyMaster savedCompany = companyMasterRepository.save(newCompany);

            return "Company created successfully";
        }
        catch (SessionExpiredException se){
            throw se;
        }
        catch (ResponseStatusException e) {
            // If the company name already exists, rethrow the exception
            throw e;
        }
        catch (Exception e) {
            // If any other unexpected error occurs, handle it and provide a generic error message
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }


    private String generateCompanyId(String companyName) {
        String companyAbbreviation = "";
        if (companyName.length() >= 3) {
            companyAbbreviation = companyName.substring(0, 1).toUpperCase() + companyName.substring(2, 3).toUpperCase();
        } else if (companyName.length() == 2) {
            companyAbbreviation = companyName.substring(0, 1).toUpperCase() + companyName.substring(1, 2).toUpperCase();
        } else {
            // If the company name has only one letter, just take the first letter
            companyAbbreviation = companyName.substring(0, 1).toUpperCase();
        }
        // Concatenate the abbreviation and unique identifier
        long siteCount = companyMasterRepository.countByCompanyIdStartingWith(companyAbbreviation);
        System.out.println("site count "+siteCount);
        System.out.println("abbrevation "+companyAbbreviation);

        // Generate the site ID based on the count
        String companyId;
        if (siteCount > 0) {
            // If other sites with the same abbreviation exist, append a numeric suffix
            companyId = String.format("%s%02d", companyAbbreviation, siteCount + 1);
        } else {
            // Otherwise, use the abbreviation without a suffix
            companyId = companyAbbreviation + "01";
        }

        return companyId;
    }

    @Override
    public List<CompanyDto> getAllCompany() {
        List<CompanyMaster> companies = companyMasterRepository.findByCompanyIdNot("all");
        return companies.stream().map(company -> modelMapper.map(company, CompanyDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCompanyNameOnly() {

        try {
            List<String> allCompanyListName = companyMasterRepository.findAllCompanyListName();
            return allCompanyListName;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to retrieve roles");
        }

    }

    @Override
    public boolean deleteCompanyByName(String companyName) {
        // Check Company exits with companyName
        CompanyMaster companyMaster = companyMasterRepository.findByCompanyName(companyName);

        if (companyMaster != null && companyMaster.getCompanyStatus().equals("ACTIVE")) {
            companyMaster.setCompanyStatus("INACTIVE");
            companyMasterRepository.save(companyMaster);
            return true;
        } else {
            return false;
        }

    }
}
