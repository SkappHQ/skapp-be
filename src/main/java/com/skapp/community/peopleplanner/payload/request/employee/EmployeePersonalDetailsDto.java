package com.skapp.community.peopleplanner.payload.request.employee;

import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalContactDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalEducationalDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalFamilyDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalGeneralDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalHealthAndOtherDetailsDto;
import com.skapp.community.peopleplanner.payload.request.employee.personal.EmployeePersonalSocialMediaDetailsDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeePersonalDetailsDto {

	private EmployeePersonalGeneralDetailsDto general;

	private EmployeePersonalContactDetailsDto contact;

	private List<EmployeePersonalFamilyDetailsDto> family;

	private List<EmployeePersonalEducationalDetailsDto> educational;

	private EmployeePersonalSocialMediaDetailsDto socialMedia;

	private EmployeePersonalHealthAndOtherDetailsDto healthAndOther;

}
