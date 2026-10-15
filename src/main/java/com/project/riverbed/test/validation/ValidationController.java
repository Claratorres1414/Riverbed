package com.project.riverbed.test.validation;

import com.project.riverbed.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/test"))
public class ValidationController {

    @PostMapping("/validation")
    public ResponseEntity<ApiResponse<ValidationRequest>> test(
            @Valid @RequestBody ValidationRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(request, "Validation successful")
        );
    }

    @GetMapping("/test-error")
    public void testError() {
        throw new RuntimeException("Informação interna que não deve ser exposta");
    }
}
