package hcmute.edu.vn.techstore.service.mail;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.entity.OrderEntity;


public class EmailNotificationObserver implements OrderObserver {

    private final EmailSender emailSender;

    public EmailNotificationObserver(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void update(OrderEntity order) {
        String to = order.getUser().getAccount().getEmail();
        String subject = "TechStore - Cập nhật trạng thái đơn hàng #" + order.getId();

        String body = "Kính gửi Quý khách hàng " + order.getUser().getFirstName() + ",\n\n" +
                "Cảm ơn Quý khách đã tin tưởng và lựa chọn mua sắm tại TechStore - chuyên bán điện thoại chính hãng.\n" +
                "Đơn hàng của Quý khách với mã số #" + order.getId() +
                " đã được cập nhật trạng thái mới: " +
                formatOrderStatus(order.getOrderStatus()) + ".\n\n" +
                "Nếu Quý khách có bất kỳ thắc mắc hay cần hỗ trợ, vui lòng liên hệ với chúng tôi qua:\n" +
                " - Hotline: 0867673342\n" +
                " - Email: support@techstore.vn\n\n" +
                "TechStore rất mong được phục vụ Quý khách lần tiếp theo!\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ TechStore";

        emailSender.sendEmail(to, subject, body);
    }

    private String formatOrderStatus(EOrderStatus status) {
        return switch (status) {
            case PENDING_CONFIRMATION -> "Đơn hàng đang chờ xác nhận";
            case WAITING_FOR_PICKUP -> "Đơn hàng chờ lấy hàng";
            case SHIPPING -> "Đơn hàng đang giao";
            case DELIVERED_SUCCESSFULLY -> "Đơn hàng đã được giao thành công";
            case CANCELLED -> "Đơn hàng bị hủy";
            case DELIVERY_FAILED -> "Giao hàng thất bại";
        };
    }
}
