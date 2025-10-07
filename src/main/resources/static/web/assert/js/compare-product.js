// Compare Product JavaScript
class CompareProductManager {
    constructor() {
        this.compareList = [];
        this.maxCompareItems = 2;
        this.selectedProductId = null;
        this.init();
    }

    init() {
        this.loadCompareCount();
        this.setupEventListeners();
    }

    setupEventListeners() {
        // Event listener cho nút Compare
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('compare-btn')) {
                e.preventDefault();
                const productId = e.target.dataset.productId;
                this.initiateCompare(productId);
            }
        });

        // Event listener cho nút Clear All
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('clear-all-compare-btn')) {
                this.clearAllCompare();
            }
        });

        // Event listener cho nút Show Compare Modal
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('show-compare-modal-btn')) {
                e.preventDefault();
                this.showCompareModal();
            }
        });

        // Event listener cho nút Close Modal
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('close-compare-modal')) {
                this.hideCompareModal();
            }
        });

        // Event listener cho modal chọn sản phẩm
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('select-product-compare-btn')) {
                const productId = e.target.dataset.productId;
                this.selectProductForCompare(productId);
            }
        });

        // Event listener cho tìm kiếm sản phẩm
        document.addEventListener('input', (e) => {
            if (e.target.id === 'productSearchInput') {
                this.searchProducts(e.target.value);
            }
        });
    }

    async initiateCompare(productId) {
        // Kiểm tra authentication
        const isAuthenticated = document.querySelector('[data-authenticated="true"]') !== null;
        
        if (!isAuthenticated) {
            this.showToast('Vui lòng đăng nhập để sử dụng chức năng so sánh sản phẩm.', 'warning');
            // Redirect to login page
            setTimeout(() => {
                window.location.href = '/login';
            }, 2000);
            return;
        }
        
        this.selectedProductId = productId;
        this.showProductSelectionModal();
    }

    async selectProductForCompare(productId) {
        if (!this.selectedProductId) {
            this.showToast('Có lỗi xảy ra. Vui lòng thử lại.', 'error');
            return;
        }

        if (this.selectedProductId === productId) {
            this.showToast('Không thể so sánh sản phẩm với chính nó.', 'warning');
            return;
        }

        console.log('Comparing products:', this.selectedProductId, 'with', productId);

        try {
            const response = await fetch(`/compare/compare/${this.selectedProductId}/${productId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            console.log('Response status:', response.status);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            console.log('Response result:', result);

            if (result.success) {
                this.hideProductSelectionModal();
                this.showCompareModalWithData(result.products);
                this.showToast('So sánh sản phẩm thành công!', 'success');
            } else {
                this.showToast(result.message, 'error');
            }
        } catch (error) {
            console.error('Error comparing products:', error);
            this.showToast('Có lỗi xảy ra khi so sánh sản phẩm: ' + error.message, 'error');
        }
    }

    showProductSelectionModal() {
        const modal = document.getElementById('productSelectionModal');
        
        if (modal) {
            // Reset search input
            const searchInput = document.getElementById('productSearchInput');
            if (searchInput) {
                searchInput.value = '';
            }
            
            // Clear previous results
            this.clearProductSearchResults();
            
            const bootstrapModal = new bootstrap.Modal(modal);
            bootstrapModal.show();
        }
    }

    hideProductSelectionModal() {
        const modal = document.getElementById('productSelectionModal');
        if (modal) {
            const bootstrapModal = bootstrap.Modal.getInstance(modal);
            if (bootstrapModal) {
                bootstrapModal.hide();
            }
        }
    }

    async searchProducts(keyword) {
        if (!keyword || keyword.length < 2) {
            this.clearProductSearchResults();
            return;
        }

        if (!this.selectedProductId) {
            return;
        }

        // Show loading state
        this.showSearchLoading();

        try {
            const response = await fetch(`/compare/search?keyword=${encodeURIComponent(keyword)}&excludeProductId=${this.selectedProductId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            const result = await response.json();

            if (result.success) {
                this.renderProductSearchResults(result.products);
            } else {
                this.showToast(result.message, 'error');
                this.clearProductSearchResults();
            }
        } catch (error) {
            console.error('Error searching products:', error);
            this.showToast('Có lỗi xảy ra khi tìm kiếm sản phẩm.', 'error');
            this.clearProductSearchResults();
        }
    }

    showSearchLoading() {
        const resultsContainer = document.getElementById('productSearchResults');
        if (resultsContainer) {
            resultsContainer.innerHTML = `
                <div class="search-loading">
                    <div class="spinner-border spinner-border-sm text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <span class="ms-2">Đang tìm kiếm...</span>
                </div>
            `;
        }
    }

    renderProductSearchResults(products) {
        const resultsContainer = document.getElementById('productSearchResults');
        if (!resultsContainer) return;

        if (products.length === 0) {
            resultsContainer.innerHTML = `
                <div class="text-center py-3">
                    <p class="text-muted">Không tìm thấy sản phẩm nào.</p>
                </div>
            `;
            return;
        }

        resultsContainer.innerHTML = products.map(product => `
            <div class="product-search-item d-flex align-items-center p-3 border-bottom">
                <div class="product-image me-3">
                    <img src="${product.thumbnail}" alt="${product.name}" class="rounded" style="width: 60px; height: 60px; object-fit: cover;">
                </div>
                <div class="product-info flex-grow-1">
                    <h6 class="mb-1">${product.name}</h6>
                    <p class="text-muted mb-1 small">${product.brandName}</p>
                    <p class="text-primary mb-0 fw-bold">${this.formatPrice(product.price)}</p>
                </div>
                <div class="product-action">
                    <button type="button" class="btn btn-primary btn-sm select-product-compare-btn" 
                            data-product-id="${product.id}">
                        <i class="fas fa-balance-scale"></i> So sánh
                    </button>
                </div>
            </div>
        `).join('');
    }

    clearProductSearchResults() {
        const resultsContainer = document.getElementById('productSearchResults');
        if (resultsContainer) {
            resultsContainer.innerHTML = `
                <div class="text-center py-4">
                    <i class="feather-search text-muted" style="font-size: 2rem;"></i>
                    <p class="text-muted mt-2">Nhập tên sản phẩm để bắt đầu tìm kiếm</p>
                </div>
            `;
        }
    }

    async addToCompare(productId) {
        try {
            const response = await fetch(`/compare/add/${productId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            const result = await response.json();

            if (result.success) {
                this.showToast(result.message, 'success');
                this.updateCompareCount(result.count);
            } else {
                this.showToast(result.message, 'error');
                this.updateCompareCount(result.count || 0);
            }
        } catch (error) {
            console.error('Error adding product to compare:', error);
            this.showToast('Có lỗi xảy ra khi thêm sản phẩm vào danh sách so sánh.', 'error');
        }
    }

    async removeFromCompare(productId) {
        try {
            const response = await fetch(`/compare/remove/${productId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            const result = await response.json();

            if (result.success) {
                this.showToast(result.message, 'success');
                this.updateCompareCount(result.count);
                
                // Nếu không còn sản phẩm nào, đóng modal
                if (result.count === 0) {
                    this.hideCompareModal();
                    this.showToast('Không có sản phẩm để so sánh.', 'info');
                } else if (result.count === 1) {
                    // Nếu chỉ còn 1 sản phẩm, đóng modal so sánh và hiển thị modal tìm kiếm
                    this.hideCompareModal();
                    this.showToast('Chọn sản phẩm thứ hai để so sánh.', 'info');
                    
                    // Lấy sản phẩm còn lại để làm sản phẩm được chọn
                    const remainingResponse = await fetch('/compare/list', {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        }
                    });
                    
                    const remainingResult = await remainingResponse.json();
                    
                    if (remainingResult.success && remainingResult.products.length > 0) {
                        this.selectedProductId = remainingResult.products[0].id;
                        
                        // Thêm delay nhỏ để đảm bảo modal so sánh đã đóng hoàn toàn
                        setTimeout(() => {
                            this.showProductSelectionModal();
                        }, 300);
                    }
                } else {
                    // Nếu còn 2 sản phẩm trở lên, refresh modal so sánh
                    this.refreshCompareModal();
                }
            } else {
                this.showToast(result.message, 'error');
            }
        } catch (error) {
            console.error('Error removing product from compare:', error);
            this.showToast('Có lỗi xảy ra khi xóa sản phẩm khỏi danh sách so sánh.', 'error');
        }
    }

    async clearAllCompare() {
        try {
            const response = await fetch('/compare/clear', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            const result = await response.json();

            if (result.success) {
                this.showToast(result.message, 'success');
                this.updateCompareCount(0);
                this.hideCompareModal();
            } else {
                this.showToast(result.message, 'error');
            }
        } catch (error) {
            console.error('Error clearing compare list:', error);
            this.showToast('Có lỗi xảy ra khi xóa danh sách so sánh.', 'error');
        }
    }

    async showCompareModal() {
        try {
            const response = await fetch('/compare/list', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            const result = await response.json();

            if (result.success) {
                if (result.count < 2) {
                    this.showToast(result.message, 'warning');
                    return;
                }
                this.renderCompareModal(result.products);
                this.showModal();
            } else {
                this.showToast(result.message, 'error');
            }
        } catch (error) {
            console.error('Error loading compare list:', error);
            this.showToast('Có lỗi xảy ra khi tải danh sách so sánh.', 'error');
        }
    }

    async refreshCompareModal() {
        try {
            const response = await fetch('/compare/list', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            const result = await response.json();

            if (result.success) {
                this.renderCompareModal(result.products);
            }
        } catch (error) {
            console.error('Error refreshing compare modal:', error);
        }
    }

    renderCompareModal(products) {
        const modalBody = document.getElementById('compareModalBody');
        if (!modalBody) return;

        if (products.length < 2) {
            modalBody.innerHTML = `
                <div class="text-center py-4">
                    <p>Chọn 2 sản phẩm để tiến hành so sánh.</p>
                </div>
            `;
            return;
        }

        const product1 = products[0];
        const product2 = products[1];

        modalBody.innerHTML = `
            <div class="row">
                <div class="col-md-6">
                    <div class="compare-product-card">
                        <div class="product-image mb-3">
                            <img src="${product1.thumbnail}" alt="${product1.name}" class="img-fluid" style="max-height: 200px;">
                        </div>
                        <h5 class="product-name">${product1.name}</h5>
                        <p class="product-price text-primary h5">${this.formatPrice(product1.price)}</p>
                        <div class="product-specs">
                            <div class="spec-row">
                                <strong>Thương hiệu:</strong> ${product1.brandName}
                            </div>
                            <div class="spec-row">
                                <strong>Hệ điều hành:</strong> ${product1.operatingSystem || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Bộ xử lý:</strong> ${product1.processor || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Camera trước:</strong> ${product1.frontCamera || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Camera sau:</strong> ${product1.rearCamera || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Pin:</strong> ${product1.batteryCapacity || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Kết nối:</strong> ${product1.connectivity || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>SIM:</strong> ${product1.sim || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Bảo hành:</strong> ${product1.warranty || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Tồn kho:</strong> ${product1.stockQuantity} sản phẩm
                            </div>
                            <div class="spec-row">
                                <strong>Đánh giá:</strong> ${this.renderStars(product1.star)} (${product1.numberOfReviews} đánh giá)
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="compare-product-card">
                        <div class="product-image mb-3">
                            <img src="${product2.thumbnail}" alt="${product2.name}" class="img-fluid" style="max-height: 200px;">
                        </div>
                        <h5 class="product-name">${product2.name}</h5>
                        <p class="product-price text-primary h5">${this.formatPrice(product2.price)}</p>
                        <div class="product-specs">
                            <div class="spec-row">
                                <strong>Thương hiệu:</strong> ${product2.brandName}
                            </div>
                            <div class="spec-row">
                                <strong>Hệ điều hành:</strong> ${product2.operatingSystem || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Bộ xử lý:</strong> ${product2.processor || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Camera trước:</strong> ${product2.frontCamera || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Camera sau:</strong> ${product2.rearCamera || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Pin:</strong> ${product2.batteryCapacity || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Kết nối:</strong> ${product2.connectivity || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>SIM:</strong> ${product2.sim || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Bảo hành:</strong> ${product2.warranty || 'N/A'}
                            </div>
                            <div class="spec-row">
                                <strong>Tồn kho:</strong> ${product2.stockQuantity} sản phẩm
                            </div>
                            <div class="spec-row">
                                <strong>Đánh giá:</strong> ${this.renderStars(product2.star)} (${product2.numberOfReviews} đánh giá)
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    renderStars(rating) {
        if (!rating) return 'Chưa có đánh giá';
        
        let stars = '';
        for (let i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars += '<i class="fas fa-star text-warning"></i>';
            } else {
                stars += '<i class="far fa-star text-muted"></i>';
            }
        }
        return stars;
    }

    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    }

    showModal() {
        const modal = document.getElementById('compareModal');
        if (modal) {
            const bootstrapModal = new bootstrap.Modal(modal);
            bootstrapModal.show();
        }
    }

    showCompareModalWithData(products) {
        this.renderCompareModal(products);
        this.showModal();
    }

    hideCompareModal() {
        const modal = document.getElementById('compareModal');
        if (modal) {
            const bootstrapModal = bootstrap.Modal.getInstance(modal);
            if (bootstrapModal) {
                bootstrapModal.hide();
            }
        }
    }

    async loadCompareCount() {
        try {
            const response = await fetch('/compare/count', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            const result = await response.json();
            if (result.success) {
                this.updateCompareCount(result.count);
            }
        } catch (error) {
            console.error('Error loading compare count:', error);
        }
    }

    updateCompareCount(count) {
        const countElements = document.querySelectorAll('.compare-count');
        countElements.forEach(element => {
            element.textContent = count;
            element.style.display = count > 0 ? 'inline' : 'none';
        });

        const compareButtons = document.querySelectorAll('.show-compare-modal-btn');
        compareButtons.forEach(button => {
            if (count < 2) {
                button.classList.add('disabled');
                button.disabled = true;
            } else {
                button.classList.remove('disabled');
                button.disabled = false;
            }
        });
    }

    showToast(message, type = 'info') {
        // Tạo toast notification
        const toastContainer = document.getElementById('toast-container') || this.createToastContainer();
        
        const toastId = 'toast-' + Date.now();
        const toastHtml = `
            <div id="${toastId}" class="toast align-items-center text-white bg-${this.getToastColor(type)} border-0" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body">
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `;
        
        toastContainer.insertAdjacentHTML('beforeend', toastHtml);
        
        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement);
        toast.show();
        
        // Xóa toast sau khi ẩn
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }

    createToastContainer() {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
        return container;
    }

    getToastColor(type) {
        const colors = {
            'success': 'success',
            'error': 'danger',
            'warning': 'warning',
            'info': 'info'
        };
        return colors[type] || 'info';
    }
}

// Khởi tạo CompareProductManager khi DOM được tải
document.addEventListener('DOMContentLoaded', function() {
    window.compareProductManager = new CompareProductManager();
});
