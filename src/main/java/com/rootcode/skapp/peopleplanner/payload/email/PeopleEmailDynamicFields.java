package com.rootcode.skapp.peopleplanner.payload.email;

import com.rootcode.skapp.common.payload.email.CommonEmailDynamicFields;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeopleEmailDynamicFields extends CommonEmailDynamicFields {

	private String temporaryPassword;

	private String terminationDate;

	private String holidayDate;

	private String holidayName;

	private String revisedDuration;

	private String requestDateTime;

}
