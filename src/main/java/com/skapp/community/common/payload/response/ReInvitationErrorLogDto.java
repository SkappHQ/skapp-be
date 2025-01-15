package com.skapp.community.common.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReInvitationErrorLogDto {

	private String email;

	private String message;

}
