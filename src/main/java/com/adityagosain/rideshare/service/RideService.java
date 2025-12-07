package com.adityagosain.rideshare.service;

import com.adityagosain.rideshare.dto.CreateRideRequest;
import com.adityagosain.rideshare.dto.RideResponse;
import com.adityagosain.rideshare.exception.BadRequestException;
import com.adityagosain.rideshare.exception.NotFoundException;
import com.adityagosain.rideshare.model.Ride;
import com.adityagosain.rideshare.model.User;
import com.adityagosain.rideshare.repository.RideRepository;
import com.adityagosain.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    public RideResponse createRide(CreateRideRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Ride ride = new Ride();
        ride.setUserId(user.getId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropLocation(request.getDropLocation());
        ride.setStatus("REQUESTED");

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    public List<RideResponse> getMyRides() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return rideRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RideResponse> getAvailableRides() {
        return rideRepository.findByStatus("REQUESTED").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RideResponse acceptRide(String rideId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        Ride ride = rideRepository.findById(Objects.requireNonNull(rideId))
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!"REQUESTED".equals(ride.getStatus())) {
            throw new BadRequestException("Ride is not available");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus("ACCEPTED");
        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(Objects.requireNonNull(rideId))
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!"ACCEPTED".equals(ride.getStatus())) {
            throw new BadRequestException("Ride is not in ACCEPTED status");
        }

        ride.setStatus("COMPLETED");
        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    private RideResponse mapToResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getUserId(),
                ride.getDriverId(),
                ride.getPickupLocation(),
                ride.getDropLocation(),
                ride.getStatus(),
                ride.getCreatedAt());
    }
}
