let thisPage = 1; // Trang hiện tại
let limit = 10;   // Số mục hiển thị mỗi trang

// Hàm tải mục người dùng cho mỗi trang
function loadItem() {
    const list = document.querySelectorAll('.list .user-item');
    let beginGet = limit * (thisPage - 1);
    let endGet = limit * thisPage - 1;

    // Lặp qua danh sách người dùng và ẩn/hiện tùy thuộc vào trang
    list.forEach((item, key) => {
        item.style.display = (key >= beginGet && key <= endGet) ? 'flex' : 'none';
    });

    // Cập nhật phân trang
    listPage(list.length);
}

// Hàm cập nhật phân trang
function listPage(totalItems) {
    const count = Math.ceil(totalItems / limit);
    const paginationContainer = document.querySelector('.listPage');

    // Xóa nội dung cũ trong phân trang
    paginationContainer.innerHTML = '';

    // Nút Previous: chỉ hiển thị khi không phải trang đầu tiên
    if (thisPage > 1) {
        const prev = document.createElement('li');
        prev.innerHTML = '<a href="#"><i class="icon-chevron-left"></i></a>';
        prev.onclick = () => changePage(thisPage - 1);
        paginationContainer.appendChild(prev);
    }

    // Các số trang: tạo một nút cho mỗi trang
    for (let i = 1; i <= count; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = (i === thisPage) ? 'active' : '';
        pageItem.innerHTML = `<a href="#">${i}</a>`;
        pageItem.onclick = () => changePage(i);
        paginationContainer.appendChild(pageItem);
    }

    // Nút Next: chỉ hiển thị khi không phải trang cuối cùng
    if (thisPage < count) {
        const next = document.createElement('li');
        next.innerHTML = '<a href="#"><i class="icon-chevron-right"></i></a>';
        next.onclick = () => changePage(thisPage + 1);
        paginationContainer.appendChild(next);
    }
}

// Hàm thay đổi trang
function changePage(page) {
    thisPage = page; // Cập nhật trang hiện tại
    loadItem(); // Tải lại các mục người dùng
}

// Gọi hàm ban đầu khi trang được tải
window.addEventListener('DOMContentLoaded', () => {
    loadItem();
});
