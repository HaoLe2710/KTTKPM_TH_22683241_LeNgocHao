// ═══ CONFIG ═══
const API = `http://${window.location.hostname || 'localhost'}:8080`;
let currentUser = null;

// ═══ DOM REFS ═══
const authPage = document.getElementById('authPage');
const toursPage = document.getElementById('toursPage');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const loginError = document.getElementById('loginError');
const registerError = document.getElementById('registerError');
const registerSuccess = document.getElementById('registerSuccess');
const tourGrid = document.getElementById('tourGrid');
const navUserName = document.getElementById('navUserName');
const tourModal = document.getElementById('tourModal');
const modalContent = document.getElementById('modalContent');
const bookingResultModal = document.getElementById('bookingResultModal');
const bookingResultContent = document.getElementById('bookingResultContent');

// ═══ PAGE SWITCHING ═══
function showPage(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  page.classList.add('active');
}

// ═══ AUTH FORM TOGGLE ═══
document.getElementById('showRegister').addEventListener('click', e => {
  e.preventDefault();
  loginForm.hidden = true;
  registerForm.hidden = false;
  hideErrors();
});
document.getElementById('showLogin').addEventListener('click', e => {
  e.preventDefault();
  registerForm.hidden = true;
  loginForm.hidden = false;
  hideErrors();
});

function hideErrors() {
  loginError.hidden = true;
  registerError.hidden = true;
  registerSuccess.hidden = true;
}

// ═══ LOGIN ═══
loginForm.addEventListener('submit', async e => {
  e.preventDefault();
  hideErrors();
  const btn = document.getElementById('loginBtn');
  btn.disabled = true;
  btn.querySelector('span').textContent = 'Đang xử lý...';

  try {
    const res = await fetch(`${API}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: document.getElementById('loginUsername').value,
        password: document.getElementById('loginPassword').value
      })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Login failed');

    currentUser = data.user;
    navUserName.textContent = `Xin chào, ${currentUser.fullName}`;
    showPage(toursPage);
    loadTours();
  } catch (err) {
    loginError.textContent = err.message;
    loginError.hidden = false;
  } finally {
    btn.disabled = false;
    btn.querySelector('span').textContent = 'Đăng nhập';
  }
});

// ═══ REGISTER ═══
registerForm.addEventListener('submit', async e => {
  e.preventDefault();
  hideErrors();
  const btn = document.getElementById('registerBtn');
  btn.disabled = true;
  btn.querySelector('span').textContent = 'Đang xử lý...';

  try {
    const res = await fetch(`${API}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        fullName: document.getElementById('regFullName').value,
        username: document.getElementById('regUsername').value,
        password: document.getElementById('regPassword').value
      })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Registration failed');

    registerSuccess.textContent = 'Đăng ký thành công! Hãy đăng nhập.';
    registerSuccess.hidden = false;
    setTimeout(() => {
      registerForm.hidden = true;
      loginForm.hidden = false;
      hideErrors();
    }, 1500);
  } catch (err) {
    registerError.textContent = err.message;
    registerError.hidden = false;
  } finally {
    btn.disabled = false;
    btn.querySelector('span').textContent = 'Đăng ký';
  }
});

// ═══ LOGOUT ═══
document.getElementById('logoutBtn').addEventListener('click', () => {
  currentUser = null;
  showPage(authPage);
  loginForm.reset();
});

// ═══ LOAD TOURS ═══
async function loadTours() {
  tourGrid.innerHTML = '<div class="tour-loading"><div class="spinner"></div><p>Đang tải danh sách tour...</p></div>';
  try {
    const res = await fetch(`${API}/tours`);
    const tours = await res.json();
    tourGrid.innerHTML = tours.map(t => `
      <div class="tour-card" data-tour-id="${t.id}" tabindex="0" role="button" aria-label="Xem chi tiết tour ${t.name}">
        <img class="tour-card-img" src="${t.image}" alt="${t.name}" onerror="this.src='https://placehold.co/600x400/1a1a3e/6366f1?text=${encodeURIComponent(t.name)}'">
        <div class="tour-card-body">
          <div class="tour-card-location">📍 ${t.location}</div>
          <div class="tour-card-name">${t.name}</div>
          <div class="tour-card-desc">${t.description || ''}</div>
          <div class="tour-card-meta">
            <span>🕐 ${t.duration}</span>
            <span>👥 Tối đa ${t.maxGuests || 20} khách</span>
          </div>
          <div class="tour-card-footer">
            <div class="tour-card-price">${formatPrice(t.price)} <small>VNĐ/người</small></div>
            <div class="tour-card-rating">⭐ ${t.rating || '4.5'}</div>
          </div>
          <button type="button" class="btn btn-card-detail" data-tour-id="${t.id}">Xem chi tiết</button>
        </div>
      </div>
    `).join('');
  } catch (err) {
    tourGrid.innerHTML = `<div class="tour-loading"><p style="color:var(--error)">Không thể tải tour: ${err.message}</p></div>`;
  }
}

// ═══ TOUR DETAIL MODAL ═══
tourGrid.addEventListener('click', e => {
  const card = e.target.closest('.tour-card');
  if (!card) return;
  openTourDetail(Number(card.dataset.tourId));
});

tourGrid.addEventListener('keydown', e => {
  if (e.key !== 'Enter' && e.key !== ' ') return;
  const card = e.target.closest('.tour-card');
  if (!card) return;
  e.preventDefault();
  openTourDetail(Number(card.dataset.tourId));
});

async function openTourDetail(tourId) {
  tourModal.hidden = false;
  modalContent.innerHTML = '<div style="padding:40px;text-align:center"><div class="spinner"></div></div>';
  try {
    const res = await fetch(`${API}/tours/${tourId}`);
    const t = await res.json();
    modalContent.innerHTML = `
      <img class="modal-tour-img" src="${t.image}" alt="${t.name}" onerror="this.src='https://placehold.co/600x400/1a1a3e/6366f1?text=${encodeURIComponent(t.name)}'">
      <div class="modal-tour-body">
        <div class="modal-tour-location">📍 ${t.location}</div>
        <h3 class="modal-tour-name">${t.name}</h3>
        <p class="modal-tour-desc">${t.description || ''}</p>
        <div class="modal-tour-info">
          <div class="modal-info-item"><div class="modal-info-label">Thời gian</div><div class="modal-info-value">🕐 ${t.duration}</div></div>
          <div class="modal-info-item"><div class="modal-info-label">Giá</div><div class="modal-info-value" style="color:var(--primary)">${formatPrice(t.price)} VNĐ</div></div>
          <div class="modal-info-item"><div class="modal-info-label">Đánh giá</div><div class="modal-info-value" style="color:var(--warning)">⭐ ${t.rating || '4.5'}</div></div>
          <div class="modal-info-item"><div class="modal-info-label">Số khách</div><div class="modal-info-value">👥 ${t.maxGuests || 20}</div></div>
        </div>
        <button class="btn btn-book" onclick="bookTour(${t.id}, '${t.name.replace(/'/g,"\\'")}', ${t.price})">
          🎫 Đặt Tour Ngay
        </button>
      </div>`;
  } catch (err) {
    modalContent.innerHTML = `<div style="padding:40px;text-align:center;color:var(--error)">Lỗi: ${err.message}</div>`;
  }
}

document.getElementById('modalCloseBtn').addEventListener('click', () => tourModal.hidden = true);
tourModal.addEventListener('click', e => { if (e.target === tourModal) tourModal.hidden = true; });

// ═══ BOOK TOUR ═══
async function bookTour(tourId, tourName, price) {
  tourModal.hidden = true;
  bookingResultModal.hidden = false;
  bookingResultContent.innerHTML = `
    <div class="booking-result">
      <div class="booking-result-icon">⏳</div>
      <h3>Đang xử lý đặt tour...</h3>
      <p>Orchestrator đang điều phối các service</p>
      <div class="booking-steps">
        <div class="booking-step" id="step1"><div class="step-icon"><div class="spinner" style="width:16px;height:16px;border-width:2px;margin:0"></div></div>Bước 1: Xác thực người dùng (User Service)</div>
        <div class="booking-step" id="step2"><div class="step-icon">⏳</div>Bước 2: Lấy thông tin tour (Tour Service)</div>
        <div class="booking-step" id="step3"><div class="step-icon">⏳</div>Bước 3: Tạo booking (Booking Service)</div>
        <div class="booking-step" id="step4"><div class="step-icon">⏳</div>Bước 4: Thanh toán (Payment Service)</div>
      </div>
    </div>`;

  // Animate steps
  await delay(500);
  setStep('step1', true); setStepLoading('step2');
  await delay(400);
  setStep('step2', true); setStepLoading('step3');
  await delay(400);
  setStep('step3', true); setStepLoading('step4');

  try {
    const res = await fetch(`${API}/book-tour`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: currentUser.id, tourId })
    });
    const data = await res.json();

    await delay(300);
    const isSuccess = data.payment && data.payment.status === 'SUCCESS';
    setStep('step4', isSuccess);

    await delay(500);
    if (isSuccess) {
      bookingResultContent.innerHTML = `
        <div class="booking-result result-success">
          <div class="booking-result-icon">🎉</div>
          <h3 style="color:var(--success)">Đặt Tour Thành Công!</h3>
          <p>${data.message}</p>
          <div class="booking-detail">
            <div class="booking-detail-row"><span class="label">Mã booking</span><span class="value">#${data.booking.id}</span></div>
            <div class="booking-detail-row"><span class="label">Khách hàng</span><span class="value">${data.user.fullName}</span></div>
            <div class="booking-detail-row"><span class="label">Tour</span><span class="value">${data.tour.name}</span></div>
            <div class="booking-detail-row"><span class="label">Thanh toán</span><span class="value" style="color:var(--success)">✅ ${data.payment.status}</span></div>
            <div class="booking-detail-row total"><span class="label">Tổng tiền</span><span class="value">${formatPrice(data.tour.price)} VNĐ</span></div>
          </div>
          <button class="btn btn-primary" onclick="bookingResultModal.hidden=true" style="width:100%;justify-content:center">Đóng</button>
        </div>`;
    } else {
      bookingResultContent.innerHTML = `
        <div class="booking-result result-failed">
          <div class="booking-result-icon">❌</div>
          <h3>Thanh Toán Thất Bại</h3>
          <p>${data.message || 'Thanh toán không thành công. Vui lòng thử lại.'}</p>
          <button class="btn btn-primary" onclick="bookTour(${tourId}, '${tourName.replace(/'/g,"\\'")}', ${price})" style="width:100%;justify-content:center;margin-bottom:8px">🔄 Thử lại</button>
          <button class="btn btn-ghost" onclick="bookingResultModal.hidden=true" style="width:100%;justify-content:center">Đóng</button>
        </div>`;
    }
  } catch (err) {
    setStep('step4', false);
    await delay(300);
    bookingResultContent.innerHTML = `
      <div class="booking-result result-failed">
        <div class="booking-result-icon">⚠️</div>
        <h3>Lỗi Kết Nối</h3>
        <p>${err.message}</p>
        <button class="btn btn-ghost" onclick="bookingResultModal.hidden=true" style="width:100%;justify-content:center">Đóng</button>
      </div>`;
  }
}

// ═══ HELPERS ═══
function formatPrice(n) { return new Intl.NumberFormat('vi-VN').format(n); }
function delay(ms) { return new Promise(r => setTimeout(r, ms)); }
function setStep(id, success) {
  const el = document.getElementById(id);
  if (!el) return;
  el.classList.add(success ? 'step-done' : 'step-fail');
  el.querySelector('.step-icon').innerHTML = success ? '✅' : '❌';
}
function setStepLoading(id) {
  const el = document.getElementById(id);
  if (!el) return;
  el.querySelector('.step-icon').innerHTML = '<div class="spinner" style="width:16px;height:16px;border-width:2px;margin:0"></div>';
}

document.getElementById('bookingResultCloseBtn').addEventListener('click', () => bookingResultModal.hidden = true);
bookingResultModal.addEventListener('click', e => { if (e.target === bookingResultModal) bookingResultModal.hidden = true; });
