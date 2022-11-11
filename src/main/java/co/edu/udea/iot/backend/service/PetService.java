package co.edu.udea.iot.backend.service;

import co.edu.udea.iot.backend.broker.PetBroker;
import co.edu.udea.iot.backend.model.Device;
import co.edu.udea.iot.backend.model.Pet;
import co.edu.udea.iot.backend.model.Message;
import co.edu.udea.iot.backend.repository.DeviceRepository;
import co.edu.udea.iot.backend.repository.PetRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private PetRepository petRepository;

    private DeviceRepository deviceRepository;

    private final PetBroker broker;


    public PetService(PetRepository petRepository, DeviceRepository deviceRepository, PetBroker broker) {
        this.petRepository = petRepository;
        this.deviceRepository = deviceRepository;
        this.broker = broker;
    }

    public List<Pet> findAllPets() {
        return petRepository.findAll();
    }

    public void sendMessage(String petName, List<Message> messages) {
        //TODO query petrepository to verify whether or not the pet exists
        Optional<Pet> petOptional = petRepository.findByName(petName);

        if (!petOptional.isPresent()) {
            System.err.println("A MESSAGE TO AN UNKNOWN PEt HAS BEEN RECEIVED {" + petName + "}");
            return;
        }
        Pet pet = petOptional.get();

        if (Pet.Status.OFFLINE.equals(pet.getStatus())) {
            System.err.println("PET IS OFFLINE (CANNOT RECEIVE MESSAGES) {" + petName + "}");
            return;
        }

        //TODO query devicerepository to verify devices existence

        StringBuilder sb = new StringBuilder();
        messages.forEach(message -> sb.append(message.getDeviceName()).append(",").append(message.getPayload()));
    }


    /**
     * @param message with the structure: pet_name; {device_name, device_status}
     */
    public void processMessage(String message) {
        // splitting the received message
        String[] tokens = message.split(";");
        // getting pet name
        String petName = tokens[0];
        // searching for the pet in the db
        Optional<Pet> petOptional = petRepository.findByName(petName);
        if (!petOptional.isPresent()) {
            System.err.println("A MESSAGE FROM AN UNKNOWN PET HAS BEEN RECEIVED {" + petName + "}");
            return;
        }
        Pet pet = petOptional.get();
        // checking whether the pet status is OFFLINE and updating it to ONLINE
        if (Pet.Status.OFFLINE.equals(pet.getStatus())) {
            pet.setStatus(Pet.Status.ONLINE);
            petRepository.save(pet);
        }
        // parsing every device notification
        for (int i = 1; i < tokens.length; i++) {
            String[] subtoken = tokens[i].split(",");
            String deviceName = subtoken[0];
            String deviceStatus = subtoken[1];
            Optional<Device> deviceOptional = deviceRepository.findByName(deviceName);
            if (!deviceOptional.isPresent()) {
                System.err.println("A MESSAGE FROM A UNKNOWN DEVICE HAS BEEN RECEIVED {" + deviceName + "}");
                return;
            }
            Device device = deviceOptional.get();
            Device.Status status;
            try {
                status = Device.Status.valueOf(deviceStatus);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                return;
            }
            device.setStatus(status);
            device.setLastUpdated(LocalDateTime.now());
            deviceRepository.save(device);
        }
    }

    public List<Device> findAllDevices() {
        return deviceRepository.findAll();
    }

    public void sendMessage(String petName, String deviceName, String payload) {
        Message message = new Message(deviceName, payload);
        this.sendMessage(petName, Arrays.asList(message));
    }

    public void sendMessage(String message) {
        try {
            this.broker.publish(message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
