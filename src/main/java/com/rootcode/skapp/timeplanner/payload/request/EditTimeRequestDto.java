package com.rootcode.skapp.timeplanner.payload.request;

import com.rootcode.skapp.peopleplanner.type.RequestType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditTimeRequestDto extends TimeRequestDto {

	public EditTimeRequestDto() {
		super.setRequestType(RequestType.EDIT_RECORD_REQUEST);
	}

}
