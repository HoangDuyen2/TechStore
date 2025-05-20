package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.service.interfaces.ICartSplitter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("activeInactiveSplitter")
public class ActiveInactiveSplitter implements ICartSplitter {
    @Override
    public Map<String, List<CartDetailEntity>> split(List<CartDetailEntity> details) {
        Map<Boolean, List<CartDetailEntity>> partitioned = details.stream()
                .collect(Collectors.partitioningBy(d -> d.getProduct().isActived()));
        return Map.of(
                "active", partitioned.getOrDefault(true, List.of()),
                "inactive", partitioned.getOrDefault(false, List.of())
        );
    }
}
