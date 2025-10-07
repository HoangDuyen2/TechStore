package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.response.CompareProductResponse;

import java.util.List;

public interface ICompareProductService {
    boolean addProductToCompare(Long userId, Long productId);
    boolean removeProductFromCompare(Long userId, Long productId);
    List<CompareProductResponse> getCompareProducts(Long userId);
    boolean clearCompareList(Long userId);
    int getCompareProductCount(Long userId);
    List<CompareProductResponse> compareTwoProducts(Long productId1, Long productId2);
    List<CompareProductResponse> searchProductsForCompare(String keyword, Long excludeProductId);
}