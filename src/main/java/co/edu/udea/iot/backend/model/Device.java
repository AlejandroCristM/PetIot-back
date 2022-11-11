package co.edu.udea.iot.backend.model;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    private String name;
    private String status;
    private LocalDateTime lastUpdated;

    @Column(name = "fk_pet")
    private String petName;

    //relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_pet", insertable = false, updatable = false)
    private Pet pet;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return Status.valueOf(status);
    }

    public void setStatus(Status status) {
        this.status = status.name();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(Integer petCode) {
        this.petName = petName;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public enum Status {
        OFFLINE, ON, OFF;
    }
}
