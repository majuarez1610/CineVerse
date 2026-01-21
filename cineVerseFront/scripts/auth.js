// ============================================
// SISTEMA DE AUTENTICACIÓN - CINEVERSE
// ============================================

// Clase para manejar usuarios
class AuthManager {
    constructor() {
        this.storageKey = 'cineverse_users';
        this.currentUserKey = 'cineverse_current_user';
    }

    // Obtener todos los usuarios
    getUsers() {
        const users = localStorage.getItem(this.storageKey);
        return users ? JSON.parse(users) : [];
    }

    // Guardar usuarios
    saveUsers(users) {
        localStorage.setItem(this.storageKey, JSON.stringify(users));
    }

    // Registro
    signup(name, email, password) {
        const users = this.getUsers();

        if (users.find(user => user.email === email)) {
            return { success: false, message: 'Este email ya está registrado' };
        }

        const newUser = {
            id: Date.now(),
            name,
            email,
            password,
            createdAt: new Date().toISOString()
        };

        users.push(newUser);
        this.saveUsers(users);

        return {
            success: true,
            message: 'Usuario registrado correctamente',
            user: { id: newUser.id, name: newUser.name, email: newUser.email }
        };
    }

    // Login
    login(email, password) {
        const users = this.getUsers();
        const user = users.find(
            u => u.email === email && u.password === password
        );

        if (!user) {
            return { success: false, message: 'Email o contraseña incorrectos' };
        }

        const sessionUser = {
            id: user.id,
            name: user.name,
            email: user.email,
            loginTime: new Date().toISOString()
        };

        localStorage.setItem(this.currentUserKey, JSON.stringify(sessionUser));

        return { success: true, user: sessionUser };
    }

    logout() {
        localStorage.removeItem(this.currentUserKey);
    }

    getCurrentUser() {
        const user = localStorage.getItem(this.currentUserKey);
        return user ? JSON.parse(user) : null;
    }

    isLoggedIn() {
        return this.getCurrentUser() !== null;
    }
}

// Instancia global
const auth = new AuthManager();

// ============================================
// MANEJO DE FORMULARIOS
// ============================================

document.addEventListener('DOMContentLoaded', () => {

    // 🔐 Si ya hay sesión, redirigir
    if (auth.isLoggedIn()) {
        window.location.href = 'index.html';
        return;
    }

    // LOGIN
    const loginForm = document.getElementById('login-form');
    loginForm.addEventListener('submit', e => {
        e.preventDefault();

        const email = document.getElementById('login-email-input').value.trim();
        const password = document.getElementById('login-pass-input').value;

        const result = auth.login(email, password);

        if (result.success) {
            mostrarMensaje(`¡Bienvenido, ${result.user.name}!`, 'success');
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1200);
        } else {
            mostrarMensaje(result.message, 'error');
        }
    });

    // SIGNUP
    const signupForm = document.getElementById('signup-form');
    signupForm.addEventListener('submit', e => {
        e.preventDefault();

        const name = document.getElementById('signup-name').value.trim();
        const email = document.getElementById('signup-email').value.trim();
        const password = document.getElementById('signup-pass').value;

        if (password.length < 6) {
            mostrarMensaje('La contraseña debe tener al menos 6 caracteres', 'error');
            return;
        }

        const result = auth.signup(name, email, password);

        if (result.success) {
            mostrarMensaje('Registro exitoso, ahora inicia sesión', 'success');
            signupForm.reset();
            document.getElementById('reg-log').checked = false;
        } else {
            mostrarMensaje(result.message, 'error');
        }
    });
});

// ============================================
// MENSAJES
// ============================================

function mostrarMensaje(mensaje, tipo) {
    const notificacion = document.createElement('div');
    notificacion.textContent = mensaje;

    notificacion.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 14px 22px;
        border-radius: 6px;
        color: white;
        font-weight: 600;
        z-index: 9999;
        background-color: ${tipo === 'success' ? '#4CAF50' : '#f44336'};
        animation: slideIn 0.3s ease-out;
    `;

    document.body.appendChild(notificacion);

    setTimeout(() => {
        notificacion.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => notificacion.remove(), 300);
    }, 3000);
}

// Animaciones
const style = document.createElement('style');
style.textContent = `
@keyframes slideIn {
    from { transform: translateX(300px); opacity: 0; }
    to { transform: translateX(0); opacity: 1; }
}
@keyframes slideOut {
    from { transform: translateX(0); opacity: 1; }
    to { transform: translateX(300px); opacity: 0; }
}`;
document.head.appendChild(style);
