# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

- Administradores que gestionan usuarios y parámetros del sistema.
- Planificadores académicos que mantienen la información base de la operación.
- Estudiantes que construyen, generan y ajustan su propuesta de horario.

## Product Purpose

Gestion-de-horarios permite organizar horarios académicos semestrales, preparar propuestas de manera semiautomática, editarlas manualmente y consultar o exportar los resultados.

## Positioning

El sistema combina la gestión de asignaturas, docentes, aulas, recursos, grupos y restricciones con reglas de programación académica para construir horarios operativos dentro de un único flujo.

## Operating Context

La aplicación se usa en un entorno académico de escritorio y móvil. El trabajo habitual inicia sesión, prepara catálogos y restricciones, genera o ajusta una propuesta semanal y finalmente la consulta o exporta por docente, aula o asignatura.

## Capabilities and Constraints

- Frontend Angular standalone existente; backend Spring Boot con API en `http://localhost:8081` durante desarrollo.
- Autenticación JWT por correo y contraseña; los roles disponibles son `ADMINISTRADOR`, `PLANIFICADOR` y `ESTUDIANTE`. `CONSULTA` se conserva únicamente para compatibilidad con cuentas antiguas y se presenta como estudiante en la interfaz.
- El backend ya aplica autorización, validaciones de horarios y CORS para `http://localhost:4200`.
- La interfaz debe ser responsive y mantener una navegación clara entre administración, programación y consulta.

## Brand Commitments

- Dirección visual solicitada: una estética natural, elegante, moderna y cálida.
- Paleta vinculante: verde oliva `#6B705C`, arena `#F5EDE3`, terracota `#CB997E` y tonos crema.
- Referencias visuales proporcionadas por el usuario: composiciones editoriales naturales y un dashboard organizado de tonos suaves.

## Evidence on Hand

- Requisitos funcionales y backend implementado en este repositorio.
- Referencias visuales adjuntas en la conversación; se usan como inspiración de composición, materialidad y tono, no como recursos para copiar.

## Product Principles

- Hacer visible el estado del horario y las decisiones de programación.
- Reducir la complejidad operativa mediante jerarquía y navegación predecible.
- Mantener los datos académicos legibles en interfaces densas.
- Dar retroalimentación clara sobre acciones, validaciones y permisos.
