# BUILD_LOG.md — iOS-Rosemary

## 2025-11-14 — Sesión: Rediseño visual iOS 27 + REA operativo

### Auditoría Node.js (inicio de sesión)
```bash
$ node --version
v20.20.2
$ npm --version
10.8.2
```
**Resultado**: Node v20.20.2 NO cumple mínimo REA (22.23.4+ / 24.22.1+ / 26.12.1+). Bloqueante.

---

### Paso 1: Instalación nvm
```bash
$ curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
```
**Salida**: nvm clonado a `~/.nvm`, hooks añadidos a `~/.bashrc`. Avisa módulos globales previos (`corepack`, `vercel`) en `~/.local/node/lib`.

---

### Paso 2: Instalación Node.js 22
```bash
$ export NVM_DIR="$HOME/.nvm" && . "$NVM_DIR/nvm.sh" && nvm install 22 && nvm alias default 22
```
**Salida**:
```
Downloading and installing node v22.23.5...
...
Now using node v22.23.5 (npm v10.9.9)
Creating default alias: default -> 22 (-> v22.23.5)
```
**Versión instalada**: v22.23.5 ≥ 22.23.4 ✅

---

### Paso 3: Verificación versiones
```bash
$ export NVM_DIR="$HOME/.nvm" && . "$NVM_DIR/nvm.sh" && node --version && npm --version
```
**Resultado**:
```
v22.23.5
10.9.9
```
✅ Cumple requisito REA.

---

### Paso 4: Actualización AGENTS.md
- Añadida sección "Hardware local" con Node v22.23.5 vía nvm
- Actualizada "Próximas sesiones" con reinicio opencode + fases A2-F
- Estado frontend: "reabierto: rediseño iOS 27 en curso"

---

### Paso 5: Creación BUILD_LOG.md
- Este archivo (registro completo de la sesión)

---

### Próximo paso (requiere reinicio opencode por usuario)
```
REA hereda Node v20 actual → tras reinicio opencode heredará v22.23.5
Luego: rea update && rea --help  (Fase A2)
```

---

### Commit
```bash
$ cd ~/Proyectos/iOS-Rosemary && git add AGENTS.md BUILD_LOG.md && git commit -m "chore: Node 22.23.5 via nvm, AGENTS.md + BUILD_LOG.md updated for iOS 27 redesign session"
```