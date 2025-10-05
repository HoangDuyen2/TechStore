package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.service.interfaces.IPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements IPdfService {

    private final TemplateEngine templateEngine;

    @Override
    public ByteArrayOutputStream generateInvoicePdf(OrderResponse orderResponse) {
        try {
            Context ctx = new Context();
            ctx.setVariable("order", orderResponse);
            String html = templateEngine.process("invoice", ctx);

            String baseUrl = Objects.requireNonNull(
                    this.getClass().getResource("/")
            ).toExternalForm();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();

            org.springframework.core.io.ClassPathResource fontRes =
                    new org.springframework.core.io.ClassPathResource("fonts/DejaVuSans.ttf");

            java.nio.file.Path tmpFont = java.nio.file.Files.createTempFile("dejavu-sans", ".ttf");
            try (java.io.InputStream in = fontRes.getInputStream()) {
                java.nio.file.Files.copy(in, tmpFont, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            renderer.getFontResolver().addFont(tmpFont.toString(),
                    com.lowagie.text.pdf.BaseFont.IDENTITY_H, true);

            renderer.setDocumentFromString(html, baseUrl);
            renderer.layout();
            renderer.createPDF(out);
            renderer.finishPDF();

            try { java.nio.file.Files.deleteIfExists(tmpFont); } catch (Exception ignore) {}

            return out;
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}
