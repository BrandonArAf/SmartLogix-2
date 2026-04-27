# Plan de Branching — Git Flow
## SmartLogix | DSY1106 Desarrollo Fullstack III

---

## 1. Estrategia Seleccionada: Git Flow

Para el desarrollo de SmartLogix se adoptó la estrategia **Git Flow**, propuesta originalmente por Vincent Driessen. Esta estrategia define ramas con propósitos específicos y flujos claros de integración, lo que facilita el trabajo en equipo y mantiene el código de producción siempre estable.

Se eligió Git Flow por sobre GitHub Flow porque el proyecto tiene fases de desarrollo bien definidas (parciales), múltiples funcionalidades en desarrollo simultáneo (inventario, pedidos, BFF, frontend) y la necesidad de mantener versiones estables entre evaluaciones.

---

## 2. Estructura de Ramas

### 2.1 Ramas Permanentes

| Rama | Propósito | ¿Quién hace merge aquí? |
|---|---|---|
| `main` | Código listo para producción; cada commit es una versión estable entregable | Solo `release/*` y `hotfix/*` |
| `develop` | Integración continua; refleja el estado actual del desarrollo | Ramas `feature/*` terminadas |

**Regla crítica:** Nadie hace commits directos a `main` ni a `develop`. Todo cambio entra vía Pull Request.

### 2.2 Ramas Temporales

| Tipo | Nomenclatura | Origen | Destino merge |
|---|---|---|---|
| Funcionalidad | `feature/<nombre>` | `develop` | `develop` |
| Versión lista | `release/<version>` | `develop` | `main` y `develop` |
| Corrección urgente | `hotfix/<descripcion>` | `main` | `main` y `develop` |

---

## 3. Flujo de Trabajo — Ejemplo Real del Proyecto

### 3.1 Inicio del proyecto

```bash
# Repositorio principal
git init
git checkout -b develop
git push origin develop
```

### 3.2 Desarrollo de funcionalidades (en paralelo)

El equipo dividió el trabajo así:

**Integrante A — Microservicio Inventario:**
```bash
git checkout develop
git checkout -b feature/ms-inventario
# Desarrolla: Producto.java, ProductoRepository, InventarioService, InventarioController
git add .
git commit -m "feat(inventario): agregar entidad Producto con JPA"
git commit -m "feat(inventario): implementar Repository Pattern en ProductoRepository"
git commit -m "feat(inventario): agregar endpoint descontar stock"
git push origin feature/ms-inventario
# Crea Pull Request hacia develop → revisión por integrante B → merge
```

**Integrante B — Microservicio Pedidos:**
```bash
git checkout develop
git checkout -b feature/ms-pedidos
# Desarrolla: Pedido.java, PedidoFactory, PedidosService, PedidosController
git add .
git commit -m "feat(pedidos): agregar entidad Pedido y DetallePedido"
git commit -m "feat(pedidos): implementar Factory Method para tipos de pedido"
git commit -m "feat(pedidos): agregar validacion de transicion de estados"
git push origin feature/ms-pedidos
```

**Integrante C — BFF y Frontend:**
```bash
git checkout develop
git checkout -b feature/bff-circuit-breaker
git commit -m "feat(bff): configurar Circuit Breaker Resilience4j"
git commit -m "feat(bff): agregar fallback para inventario y pedidos"
git push origin feature/bff-circuit-breaker

git checkout develop
git checkout -b feature/frontend-react
git commit -m "feat(frontend): crear componente InventarioPanel"
git commit -m "feat(frontend): crear componente PedidosPanel con filtros"
git push origin feature/frontend-react
```

### 3.3 Integración en develop

Cuando cada feature está lista y revisada, se hace merge a `develop`:

```bash
git checkout develop
git merge --no-ff feature/ms-inventario
git commit -m "merge: integrar feature/ms-inventario en develop"
git branch -d feature/ms-inventario
```

El flag `--no-ff` preserva el historial de la rama, manteniendo visibilidad de qué commits pertenecen a cada funcionalidad.

### 3.4 Preparación de entrega (release)

Al completar todas las features para la entrega:

```bash
git checkout develop
git checkout -b release/1.0.0
# Solo correcciones menores, NO nuevas features
git commit -m "chore: ajustar versiones en pom.xml a 1.0.0"
git commit -m "docs: actualizar README con instrucciones de ejecución"

# Merge a main
git checkout main
git merge --no-ff release/1.0.0
git tag -a v1.0.0 -m "Entrega Parcial N°2 - SmartLogix"

# Merge de vuelta a develop para incluir los cambios de la release
git checkout develop
git merge --no-ff release/1.0.0
git branch -d release/1.0.0
```

### 3.5 Corrección urgente (hotfix)

Si se detecta un bug en `main` después de la entrega:

```bash
git checkout main
git checkout -b hotfix/fix-circuit-breaker-timeout
git commit -m "fix(bff): corregir timeout del circuit breaker de 5s a 10s"

# Merge a main y develop
git checkout main
git merge --no-ff hotfix/fix-circuit-breaker-timeout
git tag -a v1.0.1 -m "Hotfix: timeout circuit breaker"

git checkout develop
git merge --no-ff hotfix/fix-circuit-breaker-timeout
git branch -d hotfix/fix-circuit-breaker-timeout
```

---

## 4. Convención de Commits (Conventional Commits)

Para mantener un historial legible se usó la convención **Conventional Commits**:

```
<tipo>(<scope>): <descripcion breve>
```

| Tipo | Uso |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de bug |
| `docs` | Solo documentación |
| `chore` | Tareas de mantenimiento (versiones, config) |
| `test` | Agregar o modificar tests |
| `refactor` | Refactorización sin cambio de comportamiento |

**Ejemplos del proyecto:**
```
feat(inventario): implementar endpoint de stock bajo
fix(pedidos): corregir validacion de transicion CANCELADO → CONFIRMADO
test(bff): agregar tests unitarios para BffController
docs(frontend): actualizar README con variables de entorno
chore(bff): actualizar resilience4j a version 2.1.0
```

---

## 5. Gestión de Conflictos

Durante el desarrollo se presentó un conflicto al integrar `feature/ms-pedidos` y `feature/bff-circuit-breaker` en `develop`, porque ambas ramas modificaron `application.properties` del BFF.

**Resolución:**
```bash
# Git marcó el conflicto:
# <<<<<<< HEAD (develop)
# ms.inventario.url=http://localhost:8081
# =======
# ms.pedidos.url=http://localhost:8082
# >>>>>>> feature/bff-circuit-breaker

# Solución: mantener AMBAS líneas (no son excluyentes)
ms.inventario.url=http://localhost:8081
ms.pedidos.url=http://localhost:8082

git add src/main/resources/application.properties
git commit -m "merge: resolver conflicto en application.properties del BFF"
```

**Lección aprendida:** Comunicar al equipo qué archivos se están modificando antes de hacer commits, para anticipar conflictos en archivos de configuración compartidos.

---

## 6. Diagrama Resumen del Flujo

```
main ──────────────────────────────────────────────────● v1.0.0
                                                       ↑
develop ──────────●────────────●────────────●─────────●
                  ↑            ↑            ↑
feature/ms-inv ───●            │            │
feature/ms-ped ────────────────●            │
feature/bff-cb ─────────────────────────────●
feature/frontend (merge en develop antes de release)
```

---

## 7. Repositorios del Proyecto

| Componente | Repositorio | Descripción |
|---|---|---|
| Principal | `github.com/equipo/smartlogix` | Repositorio raíz con documentación |
| BFF | `github.com/equipo/smartlogix-bff` | Backend For Frontend, puerto 8080 |
| MS Inventario | `github.com/equipo/ms-inventario` | Microservicio inventario, puerto 8081 |
| MS Pedidos | `github.com/equipo/ms-pedidos` | Microservicio pedidos, puerto 8082 |
| Frontend | `github.com/equipo/smartlogix-frontend` | App React, módulo NPM |
