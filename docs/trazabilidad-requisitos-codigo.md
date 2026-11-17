# Trazabilidad de requisitos funcionales hacia el código

Relaciona cada requisito funcional del informe final (RF-01 a RF-35) con las partes del código que lo cumplen.

- **Backend** (Spring Boot): rutas relativas a `Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/`.
- **Frontend** (Angular): rutas relativas a `Frontend/src/app/`.
- Los números después de `:` son la línea donde empieza el método o la regla.
- Cada endpoint se protege por rol en `security/SecurityConfig.java` (RF-02).

---

## Acceso, catálogos, parámetros y grupos (RF-01 a RF-14)

### RF-01. Autenticación con usuario y contraseña
| Capa | Dónde |
|---|---|
| Endpoint | `POST /auth/login` en [controller/AuthController.java:22](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/AuthController.java#L22) |
| Lógica | `iniciarSesion` en [service/AutenticacionService.java:47](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AutenticacionService.java#L47): valida las credenciales, bloquea 5 minutos tras 5 intentos fallidos |
| Token | [security/JwtUtil.java](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/security/JwtUtil.java) (`generateToken`, `validateToken`) y `security/JwtAuthenticationFilter.java` |
| Contraseñas | BCrypt en `passwordEncoder()` de [security/SecurityConfig.java:86](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/security/SecurityConfig.java#L86) |
| Frontend | `components/login/`, `guards/auth-guard.ts`, `services/interceptor.service.ts` (añade el token y maneja 401/429) |

### RF-02. Roles Administrador, Docente y Estudiante
| Capa | Dónde |
|---|---|
| Reglas de acceso | `securityFilterChain` en [security/SecurityConfig.java:51](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/security/SecurityConfig.java#L51): escribir solo ADMINISTRADOR, `/preselecciones/**` solo ESTUDIANTE, `/horarios/*/demanda` solo ADMINISTRADOR |
| Rol del usuario | `enums/RolUsuario.java`; coherencia rol–docente en `validarCampos` de [service/UsuarioService.java:127](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/UsuarioService.java#L127) |
| Datos del docente | [service/AccesoDocenteService.java](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AccesoDocenteService.java): un docente ve el detalle solo de sí mismo |
| Gestión de usuarios | [controller/UsuarioController.java](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/UsuarioController.java), `components/usuarios/` |
| Frontend | `guards/rol-guard.ts` (`rolGuard`, `docentePropioGuard`), `app.routes.ts`, menú por rol en `components/layout/navegacion.ts` |

### RF-03. Registrar asignatura
| Capa | Dónde |
|---|---|
| Endpoint | `POST /asignaturas` en [controller/AsignaturaController.java:27](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/AsignaturaController.java#L27) |
| Lógica | `create` y `validarCampos` en [service/AsignaturaService.java:40](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AsignaturaService.java#L40) y [:115](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AsignaturaService.java#L115): obligatorios, sesiones 1 a 4, código único sin distinguir mayúsculas |
| Modelo | [entity/Asignatura.java:23](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/entity/Asignatura.java#L23): código 20, nombre 120, tipo de aula (`enums/TipoAula.java`) |
| Frontend | `components/asignaturas/` |

### RF-04. Editar y eliminar asignatura
| Capa | Dónde |
|---|---|
| Endpoints | `PUT` y `DELETE /asignaturas/{id}` en [controller/AsignaturaController.java:43](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/AsignaturaController.java#L43) |
| Lógica | `updateById` en [service/AsignaturaService.java:75](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AsignaturaService.java#L75); `deleteById` en [:63](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AsignaturaService.java#L63) rechaza si hay grupos (`existsByAsignaturaId`) |
| Frontend | `components/asignaturas/` |

### RF-05. Registrar docente
| Capa | Dónde |
|---|---|
| Endpoint | `POST /docentes` en [controller/DocenteController.java:31](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/DocenteController.java#L31) |
| Lógica | `create` y `validarCampos` en [service/DocenteService.java:49](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/DocenteService.java#L49) y [:122](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/DocenteService.java#L122) |
| Modelo | [entity/Docente.java:24](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/entity/Docente.java#L24): nombre 120, vinculación (`enums/TipoVinculacion.java`) |
| Frontend | `components/docentes/` |

### RF-06. Restricciones de horario del docente
| Capa | Dónde |
|---|---|
| Endpoints | [controller/RestriccionHorariaDocenteController.java](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/RestriccionHorariaDocenteController.java) (`/restricciones-horarias-docente`) |
| Lógica | `create` y `validarCampos` en [service/RestriccionHorariaDocenteService.java:32](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/RestriccionHorariaDocenteService.java#L32) y [:92](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/RestriccionHorariaDocenteService.java#L92): día lunes a sábado, fin posterior al inicio |
| Base de datos | `CK_RESTRICCION_RANGO` en [configuration/EsquemaBaseDatos.java:28](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/configuration/EsquemaBaseDatos.java#L28) |
| Frontend | `components/docente-detalle/` |

### RF-07. Asignaturas que puede dictar un docente
| Capa | Dónde |
|---|---|
| Endpoints | [controller/DocenteAsignaturaController.java](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/DocenteAsignaturaController.java) (`/docente-asignaturas`) |
| Lógica | `asociar` y `retirar` en [service/DocenteAsignaturaService.java:40](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/DocenteAsignaturaService.java#L40) y [:55](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/DocenteAsignaturaService.java#L55) |
| Modelo | `entity/DocenteAsignatura.java` (tabla `docente_asignatura`) |
| Frontend | `components/docente-detalle/` |

### RF-08. Editar y eliminar docente
| Capa | Dónde |
|---|---|
| Endpoints | `PUT` y `DELETE /docentes/{id}` en [controller/DocenteController.java:47](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/DocenteController.java#L47) |
| Lógica | `updateById` en [service/DocenteService.java:97](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/DocenteService.java#L97); `deleteById` en [:82](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/DocenteService.java#L82) rechaza si tiene grupos (`existsByDocenteId`) |
| Frontend | `components/docentes/`, `components/docente-detalle/` |

### RF-09. Registrar aula
| Capa | Dónde |
|---|---|
| Endpoint | `POST /aulas` en [controller/AulaController.java:27](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/AulaController.java#L27) |
| Lógica | `create` y `validarCampos` en [service/AulaService.java:33](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AulaService.java#L33) y [:96](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AulaService.java#L96) |
| Modelo | [entity/Aula.java:22](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/entity/Aula.java#L22): código único de 20, capacidad, tipo |
| Frontend | `components/aulas/` |

### RF-10. Editar y eliminar aula
| Capa | Dónde |
|---|---|
| Endpoints | `PUT` y `DELETE /aulas/{id}` en [controller/AulaController.java:43](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/AulaController.java#L43) |
| Lógica | `updateById` en [service/AulaService.java:66](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AulaService.java#L66); `deleteById` en [:56](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/AulaService.java#L56) rechaza si tiene sesiones (`existsByAulaId`) |
| Frontend | `components/aulas/` |

### RF-11. Parámetros del sistema
| Capa | Dónde |
|---|---|
| Endpoints | `GET` y `PUT /parametros-sistema` en [controller/ParametroSistemaController.java](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/ParametroSistemaController.java) |
| Lógica | `actualizar` y `validar` en [service/ParametroSistemaService.java:43](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ParametroSistemaService.java#L43) y [:58](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ParametroSistemaService.java#L58): fin posterior al inicio, duración y cupo positivos, pausa dentro de la franja |
| Lectura de parámetros | [service/ReglaHorarioService.java](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ReglaHorarioService.java) |
| Valores iniciales | `configuration/LoadDatabase.java` |
| Frontend | `components/parametros/` |

### RF-12. Crear horario por semestre
| Capa | Dónde |
|---|---|
| Endpoint | `POST /horarios` en [controller/HorarioController.java:59](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L59) |
| Lógica | `create` en [service/HorarioService.java:76](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L76) (un solo horario por semestre) y `validarSemestre` en [:288](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L288) (formato AAAA-1 o AAAA-2) |
| Base de datos | `CK_HORARIO_SEMESTRE` en [configuration/EsquemaBaseDatos.java:29](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/configuration/EsquemaBaseDatos.java#L29) y `semestre` único en `entity/Horario.java` |
| Frontend | `components/horarios/` |

### RF-13. Registrar grupo
| Capa | Dónde |
|---|---|
| Endpoint | `POST /grupos` en [controller/GrupoController.java:27](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/GrupoController.java#L27) |
| Lógica | `create` y `validarCampos` en [service/GrupoService.java:49](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/GrupoService.java#L49) y [:165](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/GrupoService.java#L165): número único por asignatura y horario, cupo positivo, docente opcional |
| Frontend | `components/grupos-horario/grupos-horario.ts` (pestaña Grupos del horario) |

### RF-14. Sesiones sin ubicar al crear un grupo
| Capa | Dónde |
|---|---|
| Lógica | Dentro de `create` en [service/GrupoService.java:68](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/GrupoService.java#L68): crea una `SesionClase` por cada sesión semanal de la asignatura |
| Modelo | `entity/SesionClase.java` (sin día, hora ni aula mientras no está ubicada) |
| Base de datos | `CK_SESION_UBICACION` en [configuration/EsquemaBaseDatos.java:26](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/configuration/EsquemaBaseDatos.java#L26) |

---

## Generación y ajuste del horario (RF-15 a RF-19)

### RF-15. Generar la propuesta
| Capa | Dónde |
|---|---|
| Endpoint | `POST /horarios/{id}/generar` en [controller/HorarioController.java:97](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L97) |
| Lógica | `generarPropuesta` en [service/HorarioService.java:136](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L136) |
| Algoritmo | `ubicarSesionesPendientes` en [service/GeneradorHorario.java:34](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/GeneradorHorario.java#L34): prueba día, hora y aula y solo deja la ubicación si los validadores no dan ERROR; respeta el límite de tiempo `app.generacion.limite-segundos` (RNF-05) |
| Frontend | botón **Generar** (`generar()`) en `components/horario-detalle/` |

### RF-16. Sesiones que no se pudieron ubicar, con su causa
| Capa | Dónde |
|---|---|
| Lógica | [service/GeneradorHorario.java:46](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/GeneradorHorario.java#L46) (`causasSinUbicar`); se devuelve en `dto/ResultadoGeneracionHorarioDTO.java` |
| Frontend | bandeja **Sin ubicar** de `components/cuadricula-horario/` y el aviso de resultado en `components/horario-detalle/` |

### RF-17. Cuadrícula semanal y bandeja
| Capa | Dónde |
|---|---|
| Endpoint | `GET /horarios/{id}/cuadricula` en [controller/HorarioController.java:107](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L107) |
| Lógica | `obtenerCuadricula` en [service/HorarioService.java:166](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L166) (sesiones ubicadas, sin ubicar y horas de inicio válidas) |
| Frontend | [components/cuadricula-horario/cuadricula-horario.ts](../Frontend/src/app/components/cuadricula-horario/cuadricula-horario.ts) (`columnas`: lunes a sábado, horas, pausa) y su `.html` (bandeja) |

### RF-18. Mover una sesión arrastrándola y devolverla a la bandeja
| Capa | Dónde |
|---|---|
| Endpoints | `PUT /sesiones-clase/{id}/mover` y `PUT /sesiones-clase/{id}/devolver-a-bandeja` en [controller/SesionClaseController.java:39](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/SesionClaseController.java#L39) |
| Lógica | `moverSesion` y `devolverSesionABandeja` en [service/HorarioService.java:217](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L217) y [:248](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L248) |
| Frontend | arrastrar y soltar nativo de HTML5 en [components/cuadricula-horario/cuadricula-horario.ts](../Frontend/src/app/components/cuadricula-horario/cuadricula-horario.ts): `iniciarArrastre`, `soltarEnRanura`, `soltarEnBandeja`, `confirmarMovimiento` |

### RF-19. Validar cada movimiento antes de guardarlo
| Capa | Dónde |
|---|---|
| Lógica | `moverSesion` en [service/HorarioService.java:217](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L217): valida con `validarSesiones` de [service/ValidacionService.java:28](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidacionService.java#L28); si hay ERROR lanza `IncidenciasHorarioException` y la transacción se revierte (la sesión vuelve a su posición); si solo hay advertencias guarda y las devuelve en `ResultadoMovimientoDTO` |
| Respaldo en la base | disparadores y UNIQUE de ocupación en [configuration/EsquemaBaseDatos.java:40](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/configuration/EsquemaBaseDatos.java#L40); `construirChoquePorOcupacion` en [service/HorarioService.java:326](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L326) |
| Frontend | `confirmarMovimiento` en `components/cuadricula-horario/` muestra el error con sus códigos o el aviso de advertencias |

---

## Validación de reglas (RF-20 a RF-27)

Todos los validadores implementan la interfaz `service/Validador.java` y los reúne [service/ValidacionService.java:20](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidacionService.java#L20). El panel de validación usa `GET /horarios/{id}/validar` ([controller/HorarioController.java:102](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L102)).

| RF | Código | Dónde |
|---|---|---|
| RF-20. Choque de docente | `CHQ-DOC` | [service/ValidadorChoques.java:24](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidadorChoques.java#L24); en la base, `TRG_SESION_DOCENTE`, `TRG_GRUPO_DOCENTE` y UNIQUE de `ocupacion_docente` ([configuration/EsquemaBaseDatos.java:40](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/configuration/EsquemaBaseDatos.java#L40)) |
| RF-21. Choque de aula | `CHQ-AUL` | [service/ValidadorChoques.java:24](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidadorChoques.java#L24); en la base, `TRG_SESION_OCUPACION` y UNIQUE de `ocupacion_aula` ([configuration/EsquemaBaseDatos.java:54](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/configuration/EsquemaBaseDatos.java#L54)) |
| RF-22. Franja y pausa | `FRA-INI`, `FRA-PAU` | [service/ValidadorFranja.java:26](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidadorFranja.java#L26); horas válidas en `minutosInicioValidos` de [service/ReglaHorarioService.java:80](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ReglaHorarioService.java#L80) |
| RF-23. Restricción del docente | `DOC-RES` | [service/ValidadorDocente.java:22](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidadorDocente.java#L22) |
| RF-24. Docente no habilitado | `DOC-HAB` | [service/ValidadorDocente.java:22](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidadorDocente.java#L22); además, `rechazarSiIncumpleReglas` en [service/GrupoService.java:183](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/GrupoService.java#L183) al crear o editar un grupo |
| RF-25. Cupo excedido | `CUP-EXC` | [service/ValidadorCupo.java:24](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidadorCupo.java#L24); tope en `cupoMaximoConTolerancia` de [service/ReglaHorarioService.java:76](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ReglaHorarioService.java#L76) |
| RF-26. Tipo y capacidad del aula | `AUL-TIP`, `AUL-CAP` | [service/ValidadorAula.java:20](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidadorAula.java#L20) |
| RF-27. Días consecutivos (advertencia) | `CON-DIA` | [service/ValidadorConsecutividad.java:22](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ValidadorConsecutividad.java#L22) |

Frontend: panel de validación en `components/horario-detalle/` (`validar()`), nombres de los códigos en `models/incidencia.ts`.

---

## Aprobación, consulta, exportación y preselección (RF-28 a RF-35)

### RF-28. Aprobar y proteger el horario aprobado
| Capa | Dónde |
|---|---|
| Endpoints | `POST /horarios/{id}/aprobar` y `/devolver-a-borrador` en [controller/HorarioController.java:117](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L117) |
| Aprobar | `aprobar` en [service/HorarioService.java:256](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L256): rechaza si hay ERROR o sesiones sin ubicar |
| Bloqueo del horario aprobado | `exigirBorrador` en [service/HorarioService.java:299](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L299) y [service/GrupoService.java:158](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/GrupoService.java#L158) (mover, generar, grupos) |
| Cambios de catálogo | [service/ProteccionHorarioAprobadoService.java:40](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ProteccionHorarioAprobadoService.java#L40): rechaza editar aulas, asignaturas, restricciones, habilitaciones o parámetros si dañarían un horario aprobado |
| Frontend | botones Aprobar y Devolver a borrador en `components/horario-detalle/` |

### RF-29. Consultar por docente, aula o asignatura
| Capa | Dónde |
|---|---|
| Endpoints | `GET /horarios/{id}/docente/{docenteId}`, `/aula/{aulaId}` y `/asignatura/{asignaturaId}` en [controller/HorarioController.java:127](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L127) |
| Lógica | `consultarPorDocente`, `consultarPorAula` y `consultarPorAsignatura` en [service/HorarioService.java:188](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/HorarioService.java#L188) |
| Frontend | `components/consultas/consultas.ts` (usa la misma cuadrícula semanal) |

### RF-30. Exportar a CSV
| Capa | Dónde |
|---|---|
| Endpoints | `GET /horarios/{id}/exportar` y las variantes por docente, aula y asignatura en [controller/HorarioController.java:144](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L144) |
| Lógica | `exportarCsv` en [service/ExportacionService.java:17](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/ExportacionService.java#L17): UTF-8 con BOM, 7 columnas, una fila por sesión ubicada, neutraliza fórmulas de Excel |
| Preselección | `GET /preselecciones/horario/{id}/exportar` en [controller/PreseleccionController.java:61](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/PreseleccionController.java#L61) (CSV de la preselección del estudiante, mismo formato) |
| Frontend | `exportar()` en `components/consultas/`, `components/horario-detalle/` y `components/preseleccion/`; descarga en `utils/descarga.ts` |

### RF-31. Abrir y cerrar la preselección
| Capa | Dónde |
|---|---|
| Endpoints | `POST /horarios/{id}/preseleccion/abrir` y `/cerrar` en [controller/HorarioController.java:44](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L44) |
| Lógica | `abrir` (rechaza si hay sesiones sin ubicar) y `cerrar` en [service/PreseleccionService.java:71](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/PreseleccionService.java#L71); `confirmar` en [:199](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/PreseleccionService.java#L199) rechaza con la preselección cerrada |
| Modelo | `preseleccion_abierta` (S/N) en `entity/Horario.java` con `entity/ConvertidorSiNo.java`; `CK_HORARIO_PRESELECCION` en [configuration/EsquemaBaseDatos.java:30](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/configuration/EsquemaBaseDatos.java#L30) |
| Frontend | `alternarPreseleccion()` en `components/horario-detalle/` |

### RF-32. Consultar grupos con sus sesiones y docente
| Capa | Dónde |
|---|---|
| Endpoints | `GET /preselecciones/horarios` y `GET /preselecciones/horario/{id}/grupos` en [controller/PreseleccionController.java:38](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/PreseleccionController.java#L38) |
| Lógica | `horariosVisibles` y `gruposDisponibles` en [service/PreseleccionService.java:121](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/PreseleccionService.java#L121) y [:139](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/PreseleccionService.java#L139): con la preselección abierta, todos los grupos; cerrada, solo los confirmados en solo lectura. No expone vinculación ni restricciones (`dto/GrupoPreseleccionDTO.java`) |
| Frontend | [components/preseleccion/preseleccion.ts](../Frontend/src/app/components/preseleccion/preseleccion.ts) |

### RF-33. Borrador con un grupo por asignatura y choques señalados
| Capa | Dónde |
|---|---|
| Endpoint | `POST /preselecciones/horario/{id}/borrador` en [controller/PreseleccionController.java:49](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/PreseleccionController.java#L49) |
| Lógica | `revisarBorrador` en [service/PreseleccionService.java:167](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/PreseleccionService.java#L167), con `gruposDelBorrador` (un grupo por asignatura) y `choques` (código `PRE-CHQ`) en el mismo archivo |
| Frontend | `elegir()`, `revisarChoques()` y la mini cuadrícula `bloquesPorDia` en `components/preseleccion/` |

### RF-34. Confirmar sin choques, modificable y sin descontar cupo
| Capa | Dónde |
|---|---|
| Endpoint | `PUT /preselecciones/horario/{id}` en [controller/PreseleccionController.java:70](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/PreseleccionController.java#L70) |
| Lógica | `confirmar` en [service/PreseleccionService.java:199](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/PreseleccionService.java#L199): rechaza con choques, reemplaza la preselección anterior y no toca el cupo del grupo |
| Modelo | `entity/Preseleccion.java`, `entity/PreseleccionId.java` (llave compuesta usuario + grupo), `repository/PreseleccionRepository.java` |
| Frontend | `confirmar()` y `descartar()` en `components/preseleccion/` |

### RF-35. Demanda por grupo frente al cupo
| Capa | Dónde |
|---|---|
| Endpoint | `GET /horarios/{id}/demanda` en [controller/HorarioController.java:54](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/controller/HorarioController.java#L54) |
| Lógica | `demanda` en [service/PreseleccionService.java:100](../Backend/gestorhorarios/src/main/java/co/edu/unbosque/gestorhorarios/service/PreseleccionService.java#L100) (`contarPorGrupoEnHorario`). `aprobar` no consulta las preselecciones, y mover una sesión no las toca porque se guardan por grupo, no por sesión |
| Frontend | pestaña **Demanda** (`verDemanda()`, `porcentajeDemanda()`) en `components/horario-detalle/` |
