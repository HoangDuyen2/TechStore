package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;


public interface IPdfService {
    ByteArrayOutputStream generateInvoicePdf(OrderResponse orderResponse);

}
