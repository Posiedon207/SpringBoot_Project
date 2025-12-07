package com.adityagosain.rideshare.controller;

import com.adityagosain.rideshare.dto.RideResponse;

import com.adityagosain.rideshare.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/rides")
public class UserController {

    @Autowired
    private RideService rideService;

    @GetMapping
    public ResponseEntity<List<RideResponse>> myRides() {
        return ResponseEntity.ok(rideService.getMyRides());
    }
}
