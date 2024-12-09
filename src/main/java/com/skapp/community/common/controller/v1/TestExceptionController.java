package com.skapp.community.common.controller.v1;

import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.exception.EntityNotFoundException;
import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.exception.ValidationException;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.BindException;

@RestController
@RequestMapping("/v1/test")
@Tag(name = "Test Controller", description = "Test controller for testing exception handling")
public class TestExceptionController {

	@GetMapping()
	public ResponseEntityDto triggerException(@RequestParam String exceptionType) throws Exception {
		return switch (exceptionType.toLowerCase()) {
			case "bind" -> throw new BindException("Simulated BindException for testing purposes");
			case "module" -> throw new ModuleException(CommonMessageConstant.COMMON_ERROR_MODULE_EXCEPTION);
			case "notfound" -> throw new EntityNotFoundException(CommonMessageConstant.COMMON_ERROR_ENTITY_NOT_FOUND);
			case "validation" -> throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ERROR);
			case "generic" -> throw new Exception("Simulated generic Exception for testing purposes");
			case "accessdenied" -> throw new AccessDeniedException("Simulated AccessDeniedException for testing");
			case "badcredentials" -> throw new BadCredentialsException("Simulated BadCredentialsException for testing");
			case "insufficientauth" -> throw new InsufficientAuthenticationException(
					"Simulated InsufficientAuthenticationException for testing");
			case "internalauth" -> throw new InternalAuthenticationServiceException(
					"Simulated InternalAuthenticationServiceException for testing");
			default -> new ResponseEntityDto(true, "No exception triggered.");
		};
	}

}
