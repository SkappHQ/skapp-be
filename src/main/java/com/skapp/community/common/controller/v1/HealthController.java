package com.skapp.community.common.controller.v1;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {

	@GetMapping("")
	public ResponseEntity<ResponseEntityDto> checkHealth() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
