// Event delegation for Add to Cart buttons and cart quantity buttons
document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('click', function(event) {
        if (event.target.closest('.add-to-cart')) {
            event.preventDefault();
            const button = event.target.closest('.add-to-cart');
            addToCart(button);
        }
        
        // Handle quantity increase buttons
        if (event.target.closest('.inc.qtybutton.plus')) {
            event.preventDefault();
            const button = event.target.closest('.inc.qtybutton.plus');
            increaseQuantity(button);
        }
        
        // Handle quantity decrease buttons
        if (event.target.closest('.dec.qtybutton.minus')) {
            event.preventDefault();
            const button = event.target.closest('.dec.qtybutton.minus');
            decreaseQuantity(button);
        }
        
        // Handle delete item buttons
        if (event.target.closest('a.text-danger')) {
            event.preventDefault();
            const button = event.target.closest('a.text-danger');
            deleteItem(button);
        }
    });
});

function addToCart(button) {
    const productId = button.getAttribute('data-product-id');
    console.log('Product ID:', productId); // Debug log
    
    if (!productId) {
        alert('Không tìm thấy ID sản phẩm!');
        return;
    }

    fetch(`/add-cart?productId=${productId}`, {
        method: 'GET'
    })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Thêm thất bại!');
            }
        })
        .then(data => {
            alert(data);
            location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra!');
        });
}

function increaseQuantity(button) {
    console.log("increaseQuantity called");
    const productId = getProductId(button);
    
    if (!productId || productId === 'null') {
        alert('Không tìm thấy ID sản phẩm!');
        return;
    }
    
    fetch(window.location.origin + "/add-cart?productId=" + productId)
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('Tăng số lượng thất bại!');
        })
        .then(data => {
            alert(data);
            location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message);
        });
}

function decreaseQuantity(button) {
    console.log("decreaseQuantity called");
    const productId = getProductId(button);
    
    if (!productId || productId === 'null') {
        alert('Không tìm thấy ID sản phẩm!');
        return;
    }
    
    fetch(window.location.origin + "/decrease-cart?productId=" + productId)
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('Giảm số lượng thất bại!');
        })
        .then(data => {
            alert(data);
            location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message);
        });
}

function deleteItem(button) {
    console.log("deleteItem called");
    const productId = getProductId(button);
    
    if (!productId || productId === 'null') {
        alert('Không tìm thấy ID sản phẩm!');
        return;
    }
    
    fetch(window.location.origin + "/delete-cart?productId=" + productId)
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('Xóa sản phẩm thất bại!');
        })
        .then(data => {
            alert(data);
            location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message);
        });
}

function getProductId(button) {
    // Prefer reading from the clicked button's own data-product-id if present
    const directId = button.getAttribute && button.getAttribute('data-product-id');
    if (directId) {
        console.log('Found productId on button:', directId);
        return directId;
    }

    const cartItem = button.closest('.cart-wrap') || button.closest('.cart-item');
    const productId = cartItem ? cartItem.getAttribute('data-product-id') : null;
    console.log('Found productId:', productId); // Debug log
    return productId;
}
