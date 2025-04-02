package com.example.techstore.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@DiscriminatorValue("STAFF")
public class StaffEntity extends UserEntity{
    private String cccd;
    private String relativeName;
    private String relativePhoneNumber;
}
