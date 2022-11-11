package co.edu.udea.iot.backend.controller;

import co.edu.udea.iot.backend.model.Pet;
import co.edu.udea.iot.backend.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Operation(description = "View a list of available pets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping()
    public List<Pet> getAllDevices() {
        return petService.findAllPets();
    }

    @Operation(description = "Send a message to a specific device in a specific home")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PostMapping("/{petName}/{deviceName}/{message}")
    public void sendMessage(@PathVariable String pethName, @PathVariable String deviceName, @PathVariable String message) {
        petService.sendMessage(pethName, deviceName, message);
    }

    @Operation(description = "Send a test message to a pet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully message sent"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PostMapping("/messages")
    public void sendMessageToHome(@RequestParam String message) {
        petService.sendMessage(message);
    }

}
