const toggleBtn = document.getElementById('toggleTheme');
const themeIcon = document.getElementById('themeIcon');

// Load dari localStorage
if (localStorage.getItem('theme') === 'dark') {
  document.body.classList.add('dark-mode');
  themeIcon.classList.replace('fa-sun', 'fa-moon');
}

toggleBtn.addEventListener('click', () => {
  const isDark = document.body.classList.toggle('dark-mode');

  if (isDark) {
    themeIcon.classList.replace('fa-sun', 'fa-moon');
    localStorage.setItem('theme', 'dark');
  } else {
    themeIcon.classList.replace('fa-moon', 'fa-sun');
    localStorage.setItem('theme', 'light');
  }
});

// Zoom image effect
const containers = document.querySelectorAll('.poke-box');
containers.forEach(container => {
  const image = container.querySelector('.zoom-image');
  container.addEventListener('mousemove', (e) => {
    const rect = container.getBoundingClientRect();
    const x = ((e.clientX - rect.left) / rect.width - 0.5) * 20;
    const y = ((e.clientY - rect.top) / rect.height - 0.5) * 20;
    image.style.transform = `scale(1.1) translate(${x}px, ${y}px)`;
  });
  container.addEventListener('mouseleave', () => {
    image.style.transform = 'scale(1)';
  });
});

// Modal logic
const modal = document.querySelector('.modal-show');
const content = document.querySelector('.modal-container'); // pastikan ini class yang benar
const poke = document.querySelectorAll('.poke-box');
const closeBtn = document.querySelector('.fa-xmark');

let mouseScrollActive = false;

// Tampilkan modal saat box diklik
poke.forEach(box => {
  box.onclick = () => {
    modal.style.display = "block";
    mouseScrollActive = false;
    modal.style.cursor = 'pointer';
  };
});

// Sembunyikan modal saat tombol close diklik
closeBtn.onclick = () => {
  modal.style.display = "none";
};

// Toggle scroll dengan klik pada modal
modal.addEventListener('click', function () {
  mouseScrollActive = !mouseScrollActive;
  modal.style.cursor = mouseScrollActive ? 'ns-resize' : 'pointer';
});

// Scroll isi modal dengan gerakan mouse jika aktif
content.addEventListener('mousemove', function (e) {
  if (!mouseScrollActive) return;

  const bounds = content.getBoundingClientRect();
  const y = e.clientY - bounds.top;
  const height = bounds.height;

  const percentage = y / height;
  const scrollHeight = content.scrollHeight - content.clientHeight;

  content.scrollTop = percentage * scrollHeight;
});