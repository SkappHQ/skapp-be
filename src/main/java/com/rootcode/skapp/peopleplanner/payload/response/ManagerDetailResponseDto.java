package com.rootcode.skapp.peopleplanner.payload.response;

import com.rootcode.skapp.leaveplanner.type.ManagerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDetailResponseDto {

	private ManagerCoreDetailsDto manager;

	private Boolean isPrimaryManager;

	private ManagerType managerType;

}
