const $ = id => document.getElementById(id);

function showPage(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelector('[data-page="' + name + '"]').classList.add('active');
  window.scrollTo({ top: 0 });
}

function toast(msg, type) {
  const el = $('toast');
  el.textContent = msg;
  el.className = 'show ' + (type || 'success');
  setTimeout(() => el.className = '', 2800);
}

function esc(str) {
  if (!str) return '';
  return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

async function api(path, opts) {
  const res = await fetch(path, Object.assign({ credentials: 'include', headers: { 'Content-Type': 'application/json' } }, opts || {}));
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Something went wrong');
  return data;
}

function makeCover(coverUrl, title, cls) {
  const letter = (title || '?')[0].toUpperCase();
  if (coverUrl) {
    const img = document.createElement('img');
    img.className = cls;
    img.alt = title;
    img.src = coverUrl;
    img.onerror = function() {
      const ph = document.createElement('div');
      ph.className = cls + '-placeholder';
      ph.textContent = letter;
      this.replaceWith(ph);
    };
    return img;
  }
  const ph = document.createElement('div');
  ph.className = cls + '-placeholder';
  ph.textContent = letter;
  return ph;
}

async function refreshCartBadge() {
  try {
    const cart = await api('/cart');
    $('cart-count').textContent = cart.count || 0;
  } catch(e) {}
}

function goHome() {
  showPage('home');
  loadBooks();
}

async function loadBooks(search) {
  const grid = $('books-grid');
  const titleEl = $('section-title');
  grid.innerHTML = '<div class="loader"><div class="spinner"></div></div>';
  try {
    const url = search ? '/books?search=' + encodeURIComponent(search) : '/books';
    const books = await api(url);
    titleEl.textContent = search ? 'Results for "' + search + '" (' + books.length + ')' : 'All Books';
    grid.innerHTML = '';
    if (!books.length) {
      grid.innerHTML = '<p style="color:var(--muted);text-align:center;padding:40px;grid-column:1/-1">No books found.</p>';
      return;
    }
    books.forEach(function(b) {
      const card = document.createElement('div');
      card.className = 'book-card';
      card.onclick = function() { goDetail(b.id); };
      card.appendChild(makeCover(b.coverUrl, b.title, 'book-cover'));
      const info = document.createElement('div');
      info.className = 'book-info';
      info.innerHTML = '<div class="book-title">' + esc(b.title) + '</div><div class="book-author">' + esc(b.author) + '</div><div class="book-price">$' + Number(b.price).toFixed(2) + '</div>';
      card.appendChild(info);
      grid.appendChild(card);
    });
  } catch(e) {
    grid.innerHTML = '<p style="color:var(--muted);text-align:center;padding:40px">Failed to load books.</p>';
  }
}

function doSearch() {
  loadBooks($('search-input').value.trim());
}

async function goDetail(id) {
  showPage('detail');
  const content = $('detail-content');
  content.innerHTML = '<div class="loader"><div class="spinner"></div></div>';
  try {
    const b = await api('/books/' + id);
    content.innerHTML = '';
    const layout = document.createElement('div');
    layout.className = 'detail-layout';
    const imgWrap = document.createElement('div');
    imgWrap.appendChild(makeCover(b.coverUrl, b.title, 'detail-cover'));
    layout.appendChild(imgWrap);
    const info = document.createElement('div');
    info.innerHTML = '<h1 class="detail-title">' + esc(b.title) + '</h1><p class="detail-author">by ' + esc(b.author) + '</p><div class="detail-price">$' + Number(b.price).toFixed(2) + '</div><p class="detail-desc">' + esc(b.description || '') + '</p><button class="btn btn-primary" id="add-btn">Add to Cart</button>';
    layout.appendChild(info);
    content.appendChild(layout);
    $('add-btn').onclick = function() { addToCart(b.id); };
  } catch(e) {
    content.innerHTML = '<p style="color:var(--muted)">Book not found.</p>';
  }
}

async function addToCart(bookId) {
  try {
    await api('/cart', { method: 'POST', body: JSON.stringify({ bookId: bookId, quantity: 1 }) });
    await refreshCartBadge();
    toast('Added to cart!');
  } catch(e) { toast(e.message, 'error'); }
}

async function goCart() {
  showPage('cart');
  const el = $('cart-content');
  el.innerHTML = '<div class="loader"><div class="spinner"></div></div>';
  try {
    const cart = await api('/cart');
    renderCart(cart, el);
  } catch(e) {
    el.innerHTML = '<p style="color:var(--muted)">Failed to load cart.</p>';
  }
}

function renderCart(cart, el) {
  el.innerHTML = '';
  const items = cart.items;
  if (!items || items.length === 0) {
    el.innerHTML = '<div class="cart-empty"><div class="icon">cart</div><h3>Your cart is empty</h3><p>Add some books to get started.</p><br><button class="btn btn-primary" onclick="goHome()">Browse Books</button></div>';
    return;
  }
  const subtotal = items.reduce(function(s, i) { return s + i.bookPrice * i.quantity; }, 0);
  const layout = document.createElement('div');
  layout.className = 'cart-layout';
  const itemsDiv = document.createElement('div');
  items.forEach(function(item) {
    const row = document.createElement('div');
    row.className = 'cart-item';
    row.appendChild(makeCover(item.bookCoverUrl, item.bookTitle, 'cart-thumb'));
    const info = document.createElement('div');
    info.className = 'cart-info';
    info.innerHTML = '<div class="cart-title">' + esc(item.bookTitle) + '</div><div class="cart-author">' + esc(item.bookAuthor) + '</div><div class="cart-subtotal">$' + Number(item.bookPrice).toFixed(2) + ' x ' + item.quantity + ' = $' + (item.bookPrice * item.quantity).toFixed(2) + '</div>';
    row.appendChild(info);
    const btn = document.createElement('button');
    btn.className = 'btn btn-danger';
    btn.textContent = 'Remove';
    btn.onclick = function() { removeItem(item.id); };
    row.appendChild(btn);
    itemsDiv.appendChild(row);
  });
  layout.appendChild(itemsDiv);
  const summary = document.createElement('div');
  summary.className = 'cart-summary';
  summary.innerHTML = '<h3>Order Summary</h3><div class="summary-row"><span>Subtotal</span><span>$' + subtotal.toFixed(2) + '</span></div><div class="summary-row"><span>Shipping</span><span style="color:var(--green)">Free</span></div><div class="summary-row total"><span>Total</span><span>$' + subtotal.toFixed(2) + '</span></div><button class="btn btn-primary" id="checkout-btn">Checkout</button><button class="btn btn-outline" onclick="goHome()">Continue Shopping</button>';
  layout.appendChild(summary);
  el.appendChild(layout);
  $('checkout-btn').onclick = doCheckout;
}

async function removeItem(itemId) {
  try {
    await api('/cart/' + itemId, { method: 'DELETE' });
    toast('Item removed');
    goCart();
    refreshCartBadge();
  } catch(e) { toast(e.message, 'error'); }
}

async function doCheckout() {
  try {
    const order = await api('/checkout', { method: 'POST' });
    showPage('confirm');
    $('confirm-content').innerHTML = '<div class="confirm-card"><div class="confirm-icon">✅</div><h2>Order Confirmed!</h2><p>Thank you for your purchase.</p><div class="order-meta"><p><strong>Order ID:</strong> #' + order.orderId + '</p><p><strong>Total:</strong> $' + Number(order.total).toFixed(2) + '</p><p><strong>Status:</strong> ' + order.status + '</p></div><button class="btn btn-primary" onclick="goHome()">Back to Home</button></div>';
    $('cart-count').textContent = '0';
  } catch(e) { toast(e.message, 'error'); }
}

document.addEventListener('DOMContentLoaded', function() {
  $('search-input').addEventListener('keydown', function(e) { if (e.key === 'Enter') doSearch(); });
  refreshCartBadge();
  loadBooks();
});