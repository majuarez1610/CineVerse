import getdatos from "./getDatos.js";

// ============================================
// PROTECCIÓN DE RUTA - CINEVERSE
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    const currentUser = localStorage.getItem('cineverse_current_user');

    if (!currentUser) {
        window.location.href = 'login.html';
        return;
    }

    configurarUsuario();
    generaSeries();
});

// ============================================
// CONFIGURACIÓN DE USUARIO
// ============================================

function configurarUsuario() {
    const user = JSON.parse(localStorage.getItem('cineverse_current_user'));
    const iconoUsuario = document.querySelector('.user-actions .material-symbols-outlined:last-child');

    if (!user || !iconoUsuario) return;

    iconoUsuario.style.color = '#4CAF50';
    iconoUsuario.style.cursor = 'pointer';

    const menuUsuario = document.createElement('div');
    menuUsuario.className = 'menu-usuario';
    menuUsuario.style.display = 'none';

    menuUsuario.innerHTML = `
        <div class="menu-usuario-content">
            <p class="usuario-nombre">${user.name}</p>
            <p class="usuario-email">${user.email}</p>
            <hr>
            <button class="btn-logout">Cerrar Sesión</button>
        </div>
    `;

    iconoUsuario.parentElement.style.position = 'relative';
    iconoUsuario.parentElement.appendChild(menuUsuario);

    let abierto = false;

    iconoUsuario.addEventListener('click', () => {
        abierto = !abierto;
        menuUsuario.style.display = abierto ? 'block' : 'none';
    });

    document.addEventListener('click', (e) => {
        if (!iconoUsuario.contains(e.target) && !menuUsuario.contains(e.target)) {
            abierto = false;
            menuUsuario.style.display = 'none';
        }
    });

    menuUsuario.querySelector('.btn-logout').addEventListener('click', () => {
        if (confirm('¿Seguro que deseas cerrar sesión?')) {
            localStorage.removeItem('cineverse_current_user');
            window.location.href = 'login.html';
        }
    });
}

// ============================================
// ELEMENTOS
// ============================================

const elementos = {
    top5: document.querySelector('[data-name="top5"]'),
    lanzamientos: document.querySelector('[data-name="lanzamientos"]'),
    series: document.querySelector('[data-name="series"]')
};

// ============================================
// LISTAS
// ============================================

function crearListaPeliculas(elemento, datos) {
    const ulExistente = elemento.querySelector('ul');
    if (ulExistente) elemento.removeChild(ulExistente);

    const ul = document.createElement('ul');
    ul.className = 'lista';

    ul.innerHTML = datos.map(pelicula => `
        <li>
            <a href="detalles.html?id=${pelicula.id}">
                <img src="${pelicula.poster}" alt="${pelicula.titulo}">
                <div class="overlay">
                    <span class="titulo">${pelicula.titulo}</span>
                </div>
                <span class="material-symbols-outlined play-icon">play_circle</span>
            </a>
        </li>
    `).join('');

    elemento.appendChild(ul);
}

// ============================================
// CATEGORÍAS
// ============================================

const categoriaSelect = document.querySelector('[data-categorias]');
const sections = document.querySelectorAll('.section');

categoriaSelect.addEventListener('change', () => {
    const categoria = document.querySelector('[data-name="categoria"]');
    const seleccion = categoriaSelect.value;

    if (seleccion === 'todos') {
        sections.forEach(s => s.classList.remove('hidden'));
        categoria.classList.add('hidden');
        return;
    }

    sections.forEach(s => s.classList.add('hidden'));
    categoria.classList.remove('hidden');

    getdatos(`/series/categoria/${seleccion}`)
        .then(data => crearListaPeliculas(categoria, data))
        .catch(() => console.error('Error cargando categoría'));
});

// ============================================
// BUSCADOR
// ============================================

function inicializarBuscador() {
    const lupa = document.getElementById('boton-lupa');
    const input = document.getElementById('input-busqueda');
    const box = document.querySelector('.search-box');

    lupa.addEventListener('click', () => {
        box.classList.toggle('active');
        input.focus();
    });

    input.addEventListener('input', e => {
        const texto = e.target.value.toLowerCase();
        document.querySelectorAll('.lista li').forEach(li => {
            const titulo = li.querySelector('.titulo').textContent.toLowerCase();
            li.style.display = titulo.includes(texto) ? 'block' : 'none';
        });
    });
}

// ============================================
// CARGA INICIAL
// ============================================

function generaSeries() {
    Promise.all([
        getdatos('/series/top5'),
        getdatos('/series/lanzamientos'),
        getdatos('/series')
    ]).then(([top5, lanzamientos, series]) => {
        crearListaPeliculas(elementos.top5, top5);
        crearListaPeliculas(elementos.lanzamientos, lanzamientos);
        crearListaPeliculas(elementos.series, series.slice(0, 5));
        inicializarBuscador();
    });
}
