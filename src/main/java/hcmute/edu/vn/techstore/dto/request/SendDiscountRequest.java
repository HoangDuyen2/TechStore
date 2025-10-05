package hcmute.edu.vn.techstore.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SendDiscountRequest {
    private Long discountId;
    private List<Long> userIds;
    private List<Long> groupIds;
}
