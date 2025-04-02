package com.example.techstore.Enum;

public enum EOrderStatus {
    PENDING_CONFIRMATION,// Đơn hàng đang chờ xác nhận
    WAITING_FOR_PICKUP,// Chờ lấy hàng
    SHIPPING,   // Đơn hàng đang giao
    DELIVERED_SUCCESSFULLY,  // Đơn hàng đã được giao thành công
    CANCELLED,   // Đơn hàng bị hủy
    DELIVERY_FAILED// Giao hàng thất bại
}