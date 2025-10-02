package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.dto.response.CompareProductResponse;
import hcmute.edu.vn.techstore.service.interfaces.ICompareProductService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/compare")
@RequiredArgsConstructor
public class CompareProductController {
    
    private final ICompareProductService compareProductService;
    private final IUserService userService;
    
    @PostMapping("/add/{productId}")
    public ResponseEntity<Map<String, Object>> addProductToCompare(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để sử dụng chức năng so sánh sản phẩm.");
                return ResponseEntity.badRequest().body(response);
            }
            
            String email = SecurityUtils.getCurrentUsername();
            Long userId = userService.getUserByEmail(email).getUserId();
            
            boolean result = compareProductService.addProductToCompare(userId, productId);
            int count = compareProductService.getCompareProductCount(userId);
            
            if (result) {
                response.put("success", true);
                response.put("message", "Đã thêm sản phẩm vào danh sách so sánh.");
                response.put("count", count);
            } else {
                response.put("success", false);
                response.put("message", "Sản phẩm đã có trong danh sách so sánh.");
                response.put("count", count);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi thêm sản phẩm vào danh sách so sánh.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeProductFromCompare(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để sử dụng chức năng so sánh sản phẩm.");
                return ResponseEntity.badRequest().body(response);
            }
            
            String email = SecurityUtils.getCurrentUsername();
            Long userId = userService.getUserByEmail(email).getUserId();
            
            boolean result = compareProductService.removeProductFromCompare(userId, productId);
            int count = compareProductService.getCompareProductCount(userId);
            
            response.put("success", result);
            response.put("message", result ? "Đã xóa sản phẩm khỏi danh sách so sánh." : "Không tìm thấy sản phẩm trong danh sách so sánh.");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi xóa sản phẩm khỏi danh sách so sánh.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getCompareProducts(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để xem danh sách so sánh.");
                return ResponseEntity.badRequest().body(response);
            }
            
            String email = SecurityUtils.getCurrentUsername();
            Long userId = userService.getUserByEmail(email).getUserId();
            
            List<CompareProductResponse> products = compareProductService.getCompareProducts(userId);
            int count = products.size();
            
            response.put("success", true);
            response.put("products", products);
            response.put("count", count);
            
            if (count < 2) {
                response.put("message", "Chọn 2 sản phẩm để tiến hành so sánh.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi lấy danh sách so sánh.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCompareList(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để sử dụng chức năng so sánh sản phẩm.");
                return ResponseEntity.badRequest().body(response);
            }
            
            String email = SecurityUtils.getCurrentUsername();
            Long userId = userService.getUserByEmail(email).getUserId();
            
            boolean result = compareProductService.clearCompareList(userId);
            
            response.put("success", result);
            response.put("message", result ? "Đã xóa tất cả sản phẩm khỏi danh sách so sánh." : "Không có sản phẩm nào trong danh sách so sánh.");
            response.put("count", 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi xóa danh sách so sánh.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCompareProductCount(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("count", 0);
                return ResponseEntity.ok(response);
            }
            
            String email = SecurityUtils.getCurrentUsername();
            Long userId = userService.getUserByEmail(email).getUserId();
            
            int count = compareProductService.getCompareProductCount(userId);
            
            response.put("success", true);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("count", 0);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/compare/{productId1}/{productId2}")
    public ResponseEntity<Map<String, Object>> compareTwoProducts(
            @PathVariable Long productId1,
            @PathVariable Long productId2,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để sử dụng chức năng so sánh sản phẩm.");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<CompareProductResponse> products = compareProductService.compareTwoProducts(productId1, productId2);
            
            response.put("success", true);
            response.put("products", products);
            response.put("message", "So sánh sản phẩm thành công.");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi so sánh sản phẩm.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProductsForCompare(
            @RequestParam String keyword,
            @RequestParam Long excludeProductId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để sử dụng chức năng so sánh sản phẩm.");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<CompareProductResponse> products = compareProductService.searchProductsForCompare(keyword, excludeProductId);
            
            response.put("success", true);
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi tìm kiếm sản phẩm.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
