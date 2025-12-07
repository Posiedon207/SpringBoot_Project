package com.adityagosain.rideshare.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateRideRequest {
    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotBlank(message = "Drop location is required")
    private String dropLocation;
}
