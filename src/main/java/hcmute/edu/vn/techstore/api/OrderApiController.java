package hcmute.edu.vn.techstore.api;


import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final IOrderService orderService;

    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<byte[]> getOrderInvoice(@PathVariable Long orderId) {

        final String email = SecurityUtils.getCurrentUsername();

        ByteArrayOutputStream pdf = orderService.generateInvoice(email, orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("invoice.pdf")
                .build());
        return new ResponseEntity<>(pdf.toByteArray(), headers, HttpStatus.OK);
    }
}
