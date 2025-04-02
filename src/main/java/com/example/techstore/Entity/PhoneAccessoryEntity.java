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
@DiscriminatorValue("ACCESSORY")
public class PhoneAccessoryEntity extends ProductEntity{
}
