# Guía de estudio para la sustentación

Sistema de gestión y generación de horarios de clases · Proyecto Núcleo 1 · Universidad El Bosque · 8 sep – 17 nov 2026

Resumen del informe final con lo que hay que saber de memoria. Al final está lo que cambió en la aplicación después del informe y las preguntas más probables del profesor.

---

## 1. El proyecto en 30 segundos

**Problema:** el Programa de Ingeniería de Sistemas arma los horarios a mano, con herramientas de oficina desconectadas entre sí. Los choques de docente o aula, las violaciones de franja, de restricción del docente o de cupo se descubren **tarde**, cuando el horario ya está armado, y corregirlos obliga a rehacer trabajo.

**Solución:** una aplicación web, para **un programa y un semestre a la vez**, que:

- registra docentes, asignaturas, aulas y grupos;
- **genera de forma semiautomática** una propuesta de horario;
- permite **ajustarla con arrastrar y soltar**;
- **valida cada movimiento** contra las reglas del Programa;
- permite **aprobar, consultar y exportar** el horario;
- permite que los estudiantes **preseleccionen grupos** y que el Administrador vea la demanda de cada grupo.

**Frase clave:** *el sistema propone y valida, y la persona decide.* Por eso es «semiautomática».

**Por qué semiautomática:** el problema de horarios es **NP-completo** (Even et al., 1976). Ningún algoritmo conocido garantiza la solución óptima en tiempo razonable. Se busca una propuesta **válida** que la persona revise, no la óptima.

**Dos niveles de tiempo:** la estructura de las clases es **semanal** (cada sesión se repite cada semana), pero cada horario vale para **un semestre** (AAAA-1 o AAAA-2). Esta distinción surgió de una observación del profesor.

---

## 2. Objetivos

**Objetivo general:** desarrollar un sistema web de gestión y generación semiautomática de horarios, para un programa y un semestre, con arquitectura MVC sobre Java, Spring Boot y Angular. Debe producir propuestas sin choques de docente ni de aula y validar en cada ajuste manual la franja, las restricciones del docente, el cupo y la consecutividad.

**Objetivos específicos:**

- **Especificar** los requisitos según **ISO/IEC/IEEE 29148**: requisitos atómicos, no ambiguos y verificables, con historias de usuario y matrices de trazabilidad.
- **Diseñar** la arquitectura en capas y el modelo de datos con UML y un modelo entidad-relación que impida en la BD el solape de docente o aula. Justificar con análisis comparativo el lenguaje, el framework y el gestor de BD.
- **Implementar y verificar** el motor de generación, los **seis validadores** y la interfaz de arrastrar y soltar, con pruebas.

**Marco metodológico:**

- **CDIO** (Concebir, Diseñar, Implementar, Operar): ordena el proceso formativo, pero no dice cómo se hace el software.
- Para el software se usan: ISO/IEC/IEEE 29148 (requisitos), UML 2.5.1 (diseño), ISO/IEC 25010 (calidad) y prototipos de fidelidad creciente.
- Además: teoría general de sistemas (Bertalanffy) y enfoque biopsicosocial y cultural (López-Cruz, Engel).
- El informe sigue la norma **NTC 1486** y las referencias van en estilo **MLA**.

---

## 3. Reglas del negocio (Tabla 1). Hay que sabérselas

| Regla | Valor | Cómo la trata el sistema |
|---|---|---|
| Franja horaria | L–V de 7:00 a 22:00; sábado de 7:00 a 13:00 | Parametrizable. ERROR de franja (RF-22) |
| Pausa del mediodía | Nunca hay clases de 12:00 a 13:00 | Parametrizable. ERROR (RF-22) |
| Vinculación docente | Tiempo completo, tres cuartos, medio tiempo, cuarto de tiempo | Catálogo fijo, dato obligatorio (RF-05) |
| Restricciones del docente | P. ej. «solo después de las 4 p. m.» | ERROR (RF-23) |
| Habilitación | No todo docente puede dictar todo curso | ERROR (RF-24) |
| Cupo | 40 estudiantes + 10 % de tolerancia = **44** | Parametrizable. ERROR (RF-25) |
| Cierre de grupos | Un grupo con menos de 10 estudiantes se cierra | **Fuera del alcance** (no hay matrícula real) |
| Sesiones | 2 horas cada una; de 1 a 4 por semana | RF-03, RF-14 |
| Consecutividad | Con 2 o 3 sesiones, mejor que no caigan en días seguidos | **ADVERTENCIA**, no bloquea (RF-27) |
| Aulas especiales | Con computadores o sillas móviles, y son limitadas | ERROR de tipo y capacidad (RF-26) |
| Choques | Un docente o un aula no pueden tener dos sesiones a la vez | ERROR (RF-20, RF-21) |

**Restricción dura vs. blanda** (Schaerf; Carter y Laporte):

- Una restricción **dura** no se puede violar. El sistema la trata como **ERROR**, que impide guardar el cambio y aprobar el horario.
- Una restricción **blanda** es una preferencia. El sistema la trata como **ADVERTENCIA**, que informa sin bloquear. La única blanda es la **consecutividad**.

**Cómo se registra «solo después de las 4 p. m.»:** como una restricción de 7:00 a 16:00 en los días que correspondan. Una **restricción** es un intervalo en el que el docente **no puede** dictar. No es una disponibilidad.

---

## 4. Alcance (capítulo 2)

**Macrosistema:** la gestión académica del Programa (contratación, oferta, espacios, inscripción, horarios).

**Subsistema abordado:** la planeación y generación del horario semestral, desde el registro de los datos hasta la aprobación, la consulta y la exportación.

**Dentro de la frontera:**

- registro de catálogos y parámetros;
- generación de la propuesta;
- ajuste con arrastrar y soltar;
- validación;
- aprobación;
- consulta y exportación;
- preselección de grupos.

**Fuera de la frontera:**

- inscripción formal y pago de matrícula (la preselección **no descuenta cupo**);
- cierre y reasignación de grupos por baja matrícula;
- varios programas o facultades;
- contratación y carga laboral docente;
- administración de los espacios físicos.

**Patrón «Problema» (Tabla 2):**

- **Nombre:** «Cuadre manual y tardío de los horarios de clases».
- **Causas:** datos dispersos, ninguna validación automática al mover una sesión, reglas que solo conoce quien revisa y ninguna propuesta inicial.
- **Criterio de éxito:** todos los RF obligatorios cumplen su criterio de verificación, y una propuesta generada con datos de prueba no tiene incidencias ERROR entre las sesiones ubicadas.

**Diagrama de contexto (Figura 1):** el sistema es un círculo (caja negra) y los actores son rectángulos. No hay ningún sistema externo conectado.

- **Administrador** (Director del Programa y Comité Táctico):
  - envía datos, parámetros, ajustes y la aprobación;
  - recibe la propuesta, las incidencias, el horario exportado y la demanda de cada grupo.
- **Docente:** envía credenciales y consultas, y recibe su horario.
- **Estudiante:**
  - envía credenciales, consultas y su preselección;
  - recibe el horario por asignatura y la confirmación de su preselección.

---

## 5. Sistema de información y enfoque biopsicosocial

**Sistema de información** (López-Cruz): procesos organizacionales, personas, datos e información, software e infraestructura que operan juntos para apoyar una actividad de la organización. **El software es solo uno de los cinco componentes.**

| Componente | En este sistema |
|---|---|
| Procesos organizacionales | Registro de catálogos, planeación y cuadre, aprobación, publicación, consulta |
| Personas | Administrador (Director y Comité), docentes, estudiantes, equipo de desarrollo |
| Datos e información | Asignaturas, docentes y restricciones, aulas, grupos, sesiones, horarios, parámetros, usuarios |
| Software | Angular + API REST Spring Boot (servicios, generador, validadores) + BD relacional |
| Infraestructura | Servidor de aplicación, servidor de BD, navegadores |

**Enfoque biopsicosocial y cultural** (Engel, 1977; adoptado por la Universidad El Bosque). Un horario define cuándo descansan, comen, se desplazan y trabajan las personas.

| Dimensión | Decisión de diseño |
|---|---|
| Biológica | Pausa del mediodía y franja diaria como reglas que bloquean (RF-22) |
| Psicológica | Validación inmediata con el motivo del rechazo (RF-19); interfaz usable sin formación técnica (RNF-02) |
| Social | Roles diferenciados (RF-02); consulta por docente, aula o asignatura (RF-29) |
| Cultural | Vocabulario del Programa (vinculación, grupo, cupo, semestre); reglas editables sin tocar el código (RNF-01) |

**Grupos de interés (Tabla 5):**

| Grupo | Poder | Interés |
|---|---|---|
| Director del Programa | Alto | Alto |
| Profesor del curso | Alto | Alto |
| Equipo de desarrollo | Alto | Alto |
| Comité Táctico | Medio | Alto |
| Docentes | Bajo | Alto |
| Estudiantes | Bajo | Medio |

**Situación inicial vs. final (Tabla 6):**

| Aspecto | Antes | Después |
|---|---|---|
| Datos | Archivos dispersos | Registro único |
| Propuesta | Se arma desde cero | Generada por el sistema |
| Corrección | Movimiento manual | Arrastrar y soltar |
| Verificación | Visual y al final | Automática en cada movimiento |
| Integridad ante choques | Depende de la atención de quien revisa | La garantiza también la BD (RNF-08) |
| Aprobación | Informal | Solo con 0 errores y 0 sesiones sin ubicar |
| Estudiante | Recibe el horario ya publicado | Preselecciona grupos y el Administrador ve la demanda |

---

## 6. Ingeniería de requisitos (capítulo 3)

**Requisito vs. requerimiento:**

- **Requisito** (*requirement*): lo que el sistema debe cumplir.
- **Requerimiento** (*request*): lo que pide un interesado. Se registran como solicitudes S-01 a S-20.

**Calidad según ISO/IEC/IEEE 29148:** cada requisito debe ser necesario, no ambiguo, atómico, completo, consistente, factible, verificable y trazable.

**Criterio de verificación vs. criterio de aceptación.** Esta distinción la señaló el profesor:

- El **criterio de verificación** acompaña a cada requisito. Es medible y objetivo, y dice cómo se comprueba que el sistema lo cumple. Lo usa el equipo.
- El **criterio de aceptación** pertenece al cliente y mide si el producto le sirve. Va en las historias de usuario, con la forma *Dado / Cuando / Entonces*.

**Las cifras que hay que saber:**

- 20 solicitudes: 19 de los interesados y la S-20, que es la observación del profesor sobre la falta de innovación y que originó la preselección.
- **35 RF** y **9 RNF**.
- **13 historias de usuario.**
- **14 casos de uso.**
- 30 especificaciones funcionales (EF) y 9 no funcionales (ENF).
- Casos de prueba **P-01 a P-39**.

**De dónde salieron:** la primera versión tenía **7 RF y 4 RNF**, en total 11 requisitos iniciales. Tenían defectos: eran compuestos, ambiguos y no verificables. La Tabla 17 muestra la reescritura.

- **Ejemplo que analizó el profesor:** «registrar docentes con su tipo de vinculación y horarios».
  - No daba los valores de vinculación.
  - No aclaraba si los horarios eran disponibilidad o restricción.
  - No tenía criterio de verificación.
  - Se convirtió en RF-05 a RF-08.

**Supuestos operacionales:**

- Sesiones de 2 horas que empiezan en hora en punto, de lunes a sábado.
- Un docente sin restricciones puede dictar en toda la franja.
- Un grupo tiene como máximo un docente.
- Solo el Administrador modifica datos.

### Requisitos funcionales por módulo

| Bloque | RF | Qué cubre |
|---|---|---|
| Acceso | RF-01, RF-02 | Login con usuario y contraseña; 3 roles (Administrador, Docente, Estudiante); HTTP 403 si no tiene permiso |
| Asignaturas | RF-03, RF-04 | Código único de hasta 20 caracteres, nombre de hasta 120, 1 a 4 sesiones, tipo de aula; se elimina solo si no tiene grupos |
| Docentes | RF-05 a RF-08 | Nombre y vinculación; restricciones (día, inicio, fin, con fin mayor que inicio); habilitaciones; se elimina solo si no tiene grupos |
| Aulas | RF-09, RF-10 | Código único, capacidad mayor que 0, tipo; se elimina solo si no tiene sesiones |
| Parámetros | RF-11 | Franja, pausa, duración, cupo y tolerancia, editables sin recompilar |
| Horario y grupos | RF-12 a RF-14 | Un horario por semestre (AAAA-1/2); grupo con número único, cupo y docente opcional; al crear el grupo se crean sus sesiones sin ubicar |
| Generación | RF-15, RF-16 | Propuesta sin ERROR entre las sesiones ubicadas; las que no se ubican van con su causa |
| Ajuste | RF-17 a RF-19 | Cuadrícula L–S con bandeja; arrastrar a otra celda o aula y devolver a la bandeja; validar antes de guardar |
| Validación | RF-20 a RF-27 | Una regla por requisito (sección 9) |
| Aprobación | RF-28 | Solo con 0 ERROR y 0 sesiones sin ubicar; un horario aprobado no se modifica hasta devolverlo a borrador |
| Consulta y exportación | RF-29, RF-30 | Por docente, aula o asignatura; CSV en UTF-8 con 7 columnas (asignatura, grupo, docente, aula, día, inicio, fin) |
| Preselección | RF-31 a RF-35 | Abrir y cerrar (solo sin sesiones sin ubicar); ver grupos sin datos sensibles del docente; borrador con 1 grupo por asignatura y choques señalados; confirmar solo sin choques, sin descontar cupo; demanda por grupo frente a su cupo |

**RF-19 en una frase:** si el movimiento produce al menos un ERROR, se rechaza, la sesión vuelve a su posición y se muestra el motivo. Si solo produce advertencias, se guarda y se muestran.

**Fórmula del cupo:** cupo con tolerancia = parte entera de cupo × (1 + tolerancia/100). Con 40 y 10 %, el límite es 44: 44 pasa y 45 es error. En el código se usa aritmética entera, `(cupo × (100 + tol)) / 100`, para evitar errores de redondeo de punto flotante.

### Requisitos no funcionales (ISO/IEC 25010)

| RNF | Qué exige | Característica |
|---|---|---|
| RNF-01 | Parámetros editables desde la interfaz, sin recompilar ni redesplegar | Mantenibilidad |
| RNF-02 | Una persona sin formación técnica registra un docente, mueve una sesión y exporta sin ayuda (prueba con 3 personas) | Usabilidad |
| RNF-03 | Capas Controlador → Servicio → Repositorio; cada validador implementa la interfaz `Validador` y no depende de otro | Mantenibilidad, modularidad |
| RNF-04 | Mover una sesión responde en menos de **2 s en el percentil 95** con 100 grupos | Desempeño |
| RNF-05 | La generación con 100 grupos tarda **60 s o menos**; al llegar al límite devuelve lo que ya ubicó | Desempeño |
| RNF-06 | Contraseñas con hash adaptativo con sal (BCrypt), nunca en texto plano | Seguridad |
| RNF-07 | Del docente solo se guardan nombre y vinculación; al estudiante solo se le muestra el nombre (Ley 1581 de 2012) | Seguridad |
| RNF-08 | La **BD** impide dos sesiones solapadas del mismo docente o aula, incluso con solicitudes simultáneas | Fiabilidad, integridad |
| RNF-09 | Funciona en las 2 últimas versiones de Chrome, Edge y Firefox | Compatibilidad |

**De dónde salen los 2 s y los 60 s:** son **metas de diseño** que fija el equipo. Los 2 s se apoyan en Nielsen: es el orden de magnitud en el que una persona sigue sintiendo que controla la interacción. En la aplicación, el límite de generación es configurable con `app.generacion.limite-segundos`.

### Historias de usuario (13)

| Historia | Título |
|---|---|
| HU-01 | Iniciar sesión |
| HU-02 | Gestionar asignaturas |
| HU-03 | Gestionar docentes |
| HU-04 | Gestionar aulas |
| HU-05 | Configurar parámetros |
| HU-06 | Preparar el horario del semestre |
| HU-07 | Generar la propuesta |
| HU-08 | Ajustar el horario |
| HU-09 | Validar y aprobar |
| HU-10 | Consultar el horario |
| HU-11 | Exportar el horario |
| HU-12 | Preseleccionar grupos (Estudiante) |
| HU-13 | Revisar la demanda de los grupos |

Forma de una historia: *Como… quiero… de manera que…*. Los criterios de aceptación van con *Dado / Cuando / Entonces* (Cohn; North).

### Levantamiento (sección 3.2)

**Instrumentos:**

- análisis documental del enunciado;
- entrevista semiestructurada con el profesor, en el rol de cliente;
- revisión de calidad con la norma 29148;
- historias de usuario;
- diagramas de contexto y de casos de uso;
- matrices de necesidades.

**Tres resultados de la entrevista con el profesor:**

- La distinción entre estructura semanal y validez semestral.
- La reescritura de los requisitos (verificación ≠ aceptación).
- La exigencia de sustentar la tecnología comparándola con alternativas.

**Limitación declarada:** las historias de usuario **no** se validaron ni se firmaron con el Director ni con el Comité. Sus necesidades se infirieron del enunciado.

### Restricciones del entorno (RES)

| Código | Restricción |
|---|---|
| RES-01 | Plazo del 8 de septiembre al 17 de noviembre de 2026 |
| RES-02 | Java + Spring Boot y Angular |
| RES-03 | BD relacional, sustentada con un análisis comparativo |
| RES-04 | Un programa y un semestre a la vez |
| RES-05 | Sin inscripción, sin pago y sin cierre ni reasignación de grupos |
| RES-06 | Los datos del docente son datos personales (Ley 1581) |
| RES-07 | Equipo de 3 personas |
| RES-08 | Figma en plan gratuito, sin conexiones de navegación |
| RES-09 | Solo navegador de escritorio (no hay app móvil) |

**Diferencia clave entre regla y restricción:**

- Las **reglas del negocio** las verifica el **sistema** (franja, cupo, choques…).
- Las **restricciones del entorno (RES)** limitan al **equipo** (plazo, tecnología, alcance).

**Procesos no abordados (Tabla 23):**

- inscripción;
- cierre de grupos;
- varios programas;
- **control de la carga horaria por vinculación**, porque el enunciado no fija un máximo de horas;
- integración con otros sistemas: no hay ninguno identificado y el intercambio se hace por CSV.

---

## 7. Plan: EDT y cronograma (capítulo 4)

**EDT** (6 paquetes, cada uno con 3 componentes):

| Paquete | Tareas |
|---|---|
| 1. Gestión | T1 |
| 2. Requisitos y especificación | T2, T3 |
| 3. Diseño | T4, T5 |
| 4. Desarrollo | T6, T7, T8 |
| 5. Pruebas | T9 |
| 6. Documentación y cierre | T10, T11 |

**Cronograma** (11 tareas, Tabla 25, diagrama de Gantt en la Figura 5):

| Tarea | Actividad | Fechas | Días |
|---|---|---|---|
| T1 | Acta de constitución y diagrama de contexto | 8–11 sep | 4 |
| T2 | Levantamiento de requisitos | 8–21 sep | 14 |
| T3 | Especificaciones | 14–25 sep | 12 |
| T4 | Modelo de datos | 21 sep – 2 oct | 12 |
| T5 | Arquitectura y mockups | 24 sep – 7 oct | 14 |
| T6 | Backend de registro (RF-03 a RF-10) | 1–15 oct | 15 |
| T7 | **Motor de horarios (la tarea más larga)** | 7–30 oct | 24 |
| T8 | Arrastrar y soltar y exportar | 19 oct – 5 nov | 18 |
| T9 | Pruebas de integración | 26 oct – 11 nov | 17 |
| T10 | Documentación final | 3–13 nov | 11 |
| T11 | Ajustes y sustentación | 11–17 nov | 7 |

**Hitos:**

- Informe de avance: **17 de octubre**.
- Informe final: **17 de noviembre**.

**Por qué T7 es la más larga:** concentra el riesgo técnico, que son el algoritmo y los 6 validadores. Por eso empieza antes de que termine T6.

**Por qué hay tareas traslapadas:** reducen el tiempo total, a cambio de aceptar que algunas especificaciones se ajusten mientras el diseño avanza.

---

## 8. Diseño (capítulo 5)

### Arquitectura MVC

El sistema corre en dos procesos: el navegador con Angular y el servidor Spring Boot con la API REST y JSON sobre HTTP.

| Capa | Componente | Hace | No debe |
|---|---|---|---|
| Vista | Componentes Angular | Mostrar la cuadrícula y los formularios, capturar el arrastre | Tener reglas de negocio o acceder a la BD |
| Controlador | Controladores REST | Autenticar, validar el formato de la solicitud, convertir DTO, traducir excepciones a códigos HTTP | Tener lógica de negocio o consultar repositorios |
| Modelo | Servicios | Coordinar los casos de uso y las transacciones; decidir si se guarda o se rechaza | Depender de los controladores |
| Modelo | Generador | Proponer una ubicación inicial | Persistir datos por sí mismo |
| Modelo | Validadores | Evaluar una regla y devolver incidencias | Modificar el horario |
| Modelo | Repositorios | Consultar y persistir entidades | Aplicar reglas de negocio |
| Modelo | Base de datos | Garantizar la integridad de aulas y docentes | Depender de la aplicación |

**Recorrido de una solicitud (Figura 11):** vista → HTTP/JSON → controlador → servicio → repositorio → SQL → respuesta JSON.

**Patrón de los validadores:** una interfaz `Validador` con un solo método, que recibe el horario y devuelve una lista de incidencias. Cada regla es una clase. Agregar una regla nueva no obliga a modificar el servicio, lo que da bajo acoplamiento y alta cohesión (RNF-03).

**Una incidencia** tiene código, severidad (ERROR o ADVERTENCIA) y mensaje.

### Diagramas UML (UML 2.5.1)

- **Casos de uso (Figura 7):**
  - 3 actores y 14 casos de uso.
  - Generar, ajustar y aprobar **incluyen** «Validar».
  - Exportar **extiende** «Consultar».
- **Actividades (Figura 8):** dos carriles, Administrador y Sistema.
  - Se verifican los datos mínimos.
  - Se genera y se valida la propuesta.
  - El Administrador ajusta: con error, se restaura la posición; sin error, se guarda.
  - Se aprueba, solo con 0 errores y 0 sesiones sin ubicar.
  - Se exporta.
- **Secuencia (Figura 9), mover una sesión:**
  - La vista envía `PUT` con el día, la hora y el aula.
  - El controlador llama al servicio, y el servicio a los validadores.
  - Si hay ERROR, responde **409 Conflict** y la vista restaura la posición.
  - Si no, guarda y responde **200 OK** con las advertencias.
- **Clases del dominio (Figura 10):**
  - Horario 1–N Grupo.
  - Grupo 1–(1..4) Sesión.
  - Grupo N–1 Asignatura.
  - Grupo N–0..1 Docente.
  - Docente M–N Asignatura, mediante la habilitación.
  - Docente 1–N Restricción.
  - Sesión N–0..1 Aula.
  - Usuario (Estudiante) M–N Grupo, mediante la preselección.
  - Parámetro está aislado.

### Estado del arte (Tabla 27)

- **Even et al.:** demuestran que el problema es NP-completo.
- **Schaerf:** clasifica en horarios escolares, de cursos y de exámenes.
- **Carter y Laporte:** describen la práctica.
- **Ceschia et al. (2023):** ningún método domina en todos los casos.
- Herramientas existentes:
  - **FET:** aplicación de escritorio genérica.
  - **UniTime:** plataforma muy amplia.
  - **Timefold:** motor de optimización en Java que hay que modelar.

**Aporte del proyecto:** no es superar a estas herramientas optimizando. Es **ajuste al contexto**: una sola aplicación web, con las reglas del Programa parametrizables, que valida cada movimiento, explica cada rechazo y deja la decisión a la persona.

### Tecnologías y por qué

Los **criterios** salen de los RNF: integridad (RNF-08), rendimiento (RNF-04 y RNF-05), mantenibilidad (RNF-03), madurez y comunidad, y costo.

**Backend:** se comparó Java + Spring Boot con C# + ASP.NET Core, TypeScript + NestJS y Python + Django. Se eligió **Java + Spring Boot** por tres razones:

- **Tipado estático:** los 6 validadores comparan días, horas, cupos y tipos de aula, y los errores de tipo se detectan al compilar.
- **Cómputo del algoritmo:** la JVM compila en tiempo de ejecución y usa hilos reales. Además, permite sumar **Timefold** después sin reescribir el sistema.
- **Spring Boot trae opiniones definidas:** servidor embebido, inyección de dependencias y una separación por capas que facilita cumplir RNF-03.

El costo es más verbosidad y un arranque más pesado. Se acepta porque el sistema es un servicio continuo.

**La popularidad no decidió la elección:** ASP.NET Core (19,7 %) supera a Spring Boot (14,7 %), y Python (57,9 %) supera a Java (29,4 %). Fuente: encuesta de Stack Overflow de 2025.

**Frontend:** Angular, por decisión del equipo con el profesor (RES-02).

**Base de datos:** la primera versión usaba **MySQL**. El profesor pidió compararlo con alternativas de naturaleza distinta. Se compararon Oracle, PostgreSQL, MySQL, SQL Server, SQLite y MongoDB.

**Lo que hay que saber del resto:**

| Gestor | Por qué no |
|---|---|
| PostgreSQL | Hace la regla de solapes con **una sola sentencia** (restricción de exclusión GiST + btree_gist) y es el más usado (55,6 %). Es la mejor alternativa técnica, y el informe lo reconoce |
| MySQL | Admite tablas de ocupación, pero CHECK solo funciona desde la 8.0.16 (antes se ignoraba en silencio) |
| SQL Server | Admite tablas de ocupación, pero no aporta ventaja y la edición Express es limitada |
| SQLite | Un solo escritor a la vez; sirve para apps embebidas, no para un servicio multiusuario |
| MongoDB | Es documental, sin integridad referencial; el modelo es intrínsecamente relacional |

**Se eligió Oracle Database Free por tres razones:**

- **Consistencia de lectura:** una lectura nunca bloquea a una escritura, y una escritura nunca bloquea a una lectura. Conviene cuando unos consultan mientras otros editan.
- **RNF-08 queda en la BD:** tablas de ocupación con UNIQUE, mantenidas por triggers PL/SQL. Rechaza el solape aunque la solicitud no venga de la API.
- **Costo y crecimiento:** la edición Free no paga licencia y sus límites son 2 núcleos, 2 GB de RAM y 12 GB de datos. Para crecer se pasa a Enterprise del mismo motor, sin cambiar de gestor.

**El costo que se reconoce:** un esquema con más objetos (2 tablas de ocupación y 3 triggers) y una prueba adicional (P-33).

### Modelo de datos

**Tablas:** asignatura, docente, docente_asignatura, restriccion_horario, usuario, aula, horario, grupo, preseleccion, sesion, ocupacion_aula, ocupacion_docente y parametro.

**Modelo entidad-relación (Figura 13):** en notación de **Chen**, con rectángulos (entidades), rombos (relaciones) y óvalos (atributos). Las relaciones M–N HABILITA y PRESELECCIONA se convierten en tablas con llave compuesta.

**Cuatro reglas de paso del modelo E-R al relacional:**

- Cada entidad se convierte en una tabla.
- Una relación 1–N se convierte en una FK en el lado N.
- Una relación M–N se convierte en una tabla propia con PK compuesta.
- Una participación mínima de 0 se convierte en una FK que admite nulos.

**Tres decisiones del modelo:**

- Las horas se guardan como **minutos desde la medianoche** (7:00 = 420) y los días como números de **1 a 6** (lunes a sábado).
- Una **sesión sin ubicar** tiene el día y las horas en **NULL**. Un CHECK obliga a que sean todos nulos o todos llenos.
- **Tablas de ocupación:** cada sesión ubicada inserta una fila por cada **bloque de 1 hora** que ocupa, con UNIQUE (horario, aula, día, bloque) y UNIQUE (horario, docente, día, bloque).
  - Ejemplo: 8–10 ocupa los bloques 8 y 9, y 10–12 ocupa los bloques 10 y 11. No chocan.
  - Una sesión de 9–11 comparte el bloque 9, así que la BD la rechaza.

**Tres triggers:**

- Uno copia el docente del grupo a la sesión.
- Otro propaga el cambio de docente del grupo a sus sesiones.
- El tercero reconstruye la ocupación al insertar o modificar una sesión.

**Redundancia controlada:** la sesión repite el docente del grupo para poder registrar la ocupación del docente sin consultar otra tabla.

**Integridad referencial:**

- Borrar un docente borra sus restricciones.
- Borrar un grupo o un horario borra sus sesiones.
- Borrar un usuario o un grupo borra sus preselecciones.
- Borrar una asignatura que tiene grupos se **rechaza**.

---

## 9. Los validadores (Tabla 34). Hay que sabérselos de memoria

| Validador | Código | Qué detecta | Severidad | RF |
|---|---|---|---|---|
| Choques | CHQ-DOC | Mismo docente, mismo día, rangos que se solapan | ERROR | RF-20 |
| Choques | CHQ-AUL | Misma aula, mismo día, rangos que se solapan | ERROR | RF-21 |
| Franja | FRA-INI | Hora de inicio fuera de las horas válidas del día | ERROR | RF-22 |
| Franja | FRA-PAU | La sesión cruza la pausa de 12 a 13 | ERROR | RF-22 |
| Docente | DOC-RES | La sesión se solapa con una restricción del docente | ERROR | RF-23 |
| Docente | DOC-HAB | El docente no está habilitado para la asignatura | ERROR | RF-24 |
| Cupo | CUP-EXC | El cupo supera el límite con tolerancia (más de 44) | ERROR | RF-25 |
| Aula | AUL-TIP | El tipo de aula no es el que exige la asignatura | ERROR | RF-26 |
| Aula | AUL-CAP | La capacidad del aula es menor que el cupo del grupo | ERROR | RF-26 |
| Consecutividad | CON-DIA | Asignatura de 2 o 3 sesiones con dos en días contiguos | **ADVERTENCIA** | RF-27 |

**Son 6 validadores y 10 códigos. Solo CON-DIA es advertencia.**

**El solape se evalúa con intervalos semiabiertos:** dos sesiones contiguas no chocan. 7–9 y 9–11 conviven; 7–9 y 8–10 chocan.

**Ejemplos del criterio de verificación** (para responder rápido):

- RF-22, franja:
  - Son error: lunes 6–8, 21–23, 11–13 y sábado 12–14.
  - Son válidas: 7–9, 10–12 y 13–15.
- RF-23, restricción del docente de 7 a 16 los lunes:
  - 14–16 el lunes es error.
  - 16–18 el lunes y 14–16 el martes son válidas.
- RF-26, aula:
  - Un aula de computadores con capacidad 30 y un grupo de cupo 35 es error.
  - Con capacidad 35 y cupo 35 es válido.
- RF-27, consecutividad:
  - Lunes y martes da advertencia.
  - Lunes y miércoles no da nada.
  - Las asignaturas de 1 o de 4 sesiones no se evalúan.

---

## 10. El algoritmo de generación

Es un algoritmo **constructivo voraz** que ubica primero las sesiones más difíciles. Se eligió porque es simple de probar y su resultado es explicable: cada sesión sin ubicar tiene un motivo.

**Pasos:**

- Reunir las sesiones sin ubicar del horario en borrador.
- **Ordenarlas de la más restringida a la menos restringida.** Van primero las que exigen un aula especial, luego las de docentes con restricciones y luego las de grupos con mayor cupo.
- Para cada sesión, recorrer los días, las horas válidas y las aulas compatibles en tipo y capacidad.
- Probar los validadores en cada combinación. La primera sin ERROR se queda.
- Si la asignatura tiene 2 o 3 sesiones, **preferir** combinaciones sin advertencia de consecutividad.
- Si ninguna combinación sirve, la sesión queda sin ubicar y **el motivo es el validador que bloqueó la última combinación evaluada**.
- Devolver las sesiones ubicadas, las sesiones sin ubicar con su motivo y las advertencias.

**Costo:** 5 días × 12 horas válidas + sábado × 4 = **64 combinaciones de día y hora por aula**. Ejemplo: 200 sesiones × 20 aulas da 256.000 combinaciones como máximo. Si se alcanza el límite de tiempo de RNF-05, el algoritmo entrega lo que haya ubicado.

**Limitación que se acepta por diseño:** el algoritmo no garantiza la solución óptima, ni siquiera una completa, porque el problema es NP-completo y las decisiones voraces no se revisan. Lo que no logra ubicar lo resuelve la persona con el ajuste manual y la validación inmediata.

---

## 11. Avance, prototipos y conclusiones (capítulos 6 y 7)

**Estado del avance según el informe (Tabla 35):**

| Fase CDIO | Estado en el informe |
|---|---|
| Concebir | Completado, con la limitación de la validación con el cliente |
| Diseñar | Completado. El prototipo es de fidelidad media |
| Implementar | Parcial. El informe dice que la preselección «no está implementada» (**ver sección 12: ya lo está**) |
| Operar | Casos P-01 a P-39 definidos, sin resultados de ejecución en el informe |

**Prototipos (fidelidad, según Rudd et al.):**

- **Baja fidelidad:** bocetos.
- **Fidelidad media:** wireframes. El prototipo en **Figma** es de este tipo: tiene 8 pantallas, sin validaciones ni navegación, porque se agotó el plan gratuito (RES-08).
- **Alta fidelidad:** la aplicación Angular es un **prototipo evolutivo de alta fidelidad**.
- Pantallas que no se prototiparon: aulas, parámetros, exportación y preselección.

**Conclusiones por objetivo:**

- **Requisitos:**
  - Se cumplió: 20 solicitudes dieron 35 RF, 9 RNF y 13 HU, con criterio de verificación y trazabilidad.
  - Limitación: no hubo firma del cliente.
- **Diseño:**
  - Se cumplió: MVC, 5 diagramas UML, modelo E-R y modelo relacional.
  - MySQL se reemplazó por Oracle con tablas de ocupación.
  - El análisis es cualitativo, sin benchmarks propios.
- **Implementación:** el informe documenta el diseño y el plan de pruebas, y no declara resultados que no pueda respaldar.
- **Idea de cierre:** el sistema propone y valida, y la persona decide. Si el algoritmo no basta, la pila permite añadir un motor de optimización sin reescribir.

**Lecturas del curso (14, Tabla 28), en una línea cada una:**

| Lectura | Idea | Dónde se aplica |
|---|---|---|
| Manual de Oslo | Una innovación solo lo es cuando se pone en uso | Por eso el informe no la declara lograda |
| Simon | Problemas mal estructurados: el horario es bien estructurado en lo pequeño (las reglas) y mal estructurado en lo grande (la calidad global) | Justifica la generación semiautomática |
| Bertalanffy | Un sistema es un complejo de elementos en interacción; el choque es una propiedad **constitutiva** de la relación entre sesiones | Por eso se valida contra todo el horario; la retroalimentación ERROR/ADVERTENCIA y la persona que corrige |
| López-Cruz | Lo biopsicosocial es propio de la ingeniería; un sistema de información es un sistema sociotécnico de 5 componentes | Secciones 2.3 y 2.3.1 |
| Berenbach et al. | Requisitos como artefactos en 3 niveles: necesidades, requisitos y especificación | Estructura del capítulo 3 |
| Balmer y Keat | Diseño en 7 pasos: definir el problema, generar alternativas, evaluar, diseño detallado, **defender** (esta sustentación), construir y probar, evaluar el desempeño | Cada paso corresponde a un capítulo del informe |
| Oates | Propósito y productos deben ser coherentes | Objetivos (introducción) → productos (capítulo 6) → contraste (capítulo 7) |

---

## 12. Lo que cambió en la aplicación después del informe

Esto es importante: el profesor puede preguntar por diferencias entre el documento y la aplicación.

| Tema | Informe | Aplicación real |
|---|---|---|
| Preselección (RF-31 a RF-35) | «Especificada y no implementada» (Tabla 35, conclusiones) | **Implementada:** abrir y cerrar, borrador con detección de choques, confirmación, demanda por grupo |
| Arrastrar y soltar | Angular CDK (`cdkDrag`) | **Arrastrar y soltar nativo de HTML5**, sin CDK |
| Versiones | Spring Boot 3.2.x con Java 17 o superior | Spring Boot 4.1.1, Java 25, Angular 22 (standalone, signals) |
| Preselección cerrada | No se menciona | El estudiante sigue viendo su horario confirmado **en modo solo lectura** |
| Exportación de la preselección | No se menciona | El estudiante puede **exportar su preselección a CSV**, con el mismo formato de 7 columnas |
| Seguridad | Login y roles | JWT y BCrypt; HTTP 403 por rol; HTTP 409 en los movimientos rechazados |

**Si preguntan por qué cambió algo:** el informe es una fotografía del 10 de noviembre, y la aplicación siguió evolucionando en la fase de ajustes (T11). Las funciones nuevas están dentro del alcance: RF-32 y RF-30.

**Otros comportamientos de la aplicación que conviene saber explicar:**

- **Aprobar exige 0 ERROR y 0 sesiones sin ubicar.** Las advertencias no bloquean.
- **Un horario aprobado no se puede mover** hasta devolverlo a borrador (RF-28).
- **Un docente nuevo solo aparece como opción** para un grupo de una asignatura en la que esté **habilitado** (DOC-HAB). Si no se le asigna a un grupo, no tiene sesiones en la cuadrícula.
- **DOC-RES** significa que la sesión cae dentro de una restricción de horario del docente.
- **Se pueden poner dos clases el mismo día y a la misma hora** si tienen distinto docente y distinta aula. El sistema marca si el aula está ocupada a esa hora.

---

## 13. Preguntas probables del profesor y cómo responderlas

**¿Qué es un sistema de información y qué lo conforma?**
Son procesos, personas, datos e información, software e infraestructura que operan juntos (López-Cruz). Nombrar los 5 componentes con el ejemplo del horario (Tabla 3). El software es solo uno de ellos.

**¿Qué hace el programa y cómo funciona?**
Seguir el flujo de punta a punta:

- El Administrador registra catálogos y parámetros y crea el horario del semestre.
- Crea los grupos, y el sistema genera sus sesiones sin ubicar.
- El sistema genera la propuesta.
- El Administrador ajusta arrastrando sesiones, y cada movimiento se valida.
- El Administrador abre la preselección, y los estudiantes eligen y confirman grupos.
- El Administrador ve la demanda y aprueba.
- Docentes y estudiantes consultan y exportan el horario.

**¿Cuál es la diferencia entre reglas y restricciones?**

- Las **reglas del negocio** (Tabla 1) las valida el sistema. Pueden ser duras (ERROR) o blandas (ADVERTENCIA).
- Las **restricciones del entorno** (RES-01 a RES-09) limitan al proyecto: plazo, tecnología, alcance, ley de datos.
- Las **restricciones del docente** son un dato: intervalos en los que no puede dictar. Se crean en el formulario del docente, con día, inicio y fin.

**¿De dónde salen los 2 s y los 60 s?**
Son metas de diseño del equipo. Los 2 s se apoyan en Nielsen (los límites de tiempo de respuesta). Los 60 s son configurables con `app.generacion.limite-segundos`. Se verifican con los casos P-29 y P-30.

**¿Por qué Oracle si PostgreSQL lo hace con una sentencia?**
Por la consistencia de lectura, porque la regla queda en la BD con UNIQUE y triggers, y porque la edición Free no tiene costo y escala dentro del mismo motor. Se reconoce el costo: más objetos y la prueba P-33.

**¿Por qué no se tuvieron en cuenta los reglamentos estudiantiles?**
Por el alcance (RES-05): el sistema no hace inscripción formal ni cierre de grupos. La preselección **no es una matrícula** y no descuenta cupo. Los reglamentos de inscripción quedan fuera de la frontera.

**¿Dónde están las fechas o la línea de tiempo?**
En la Tabla 25 (11 tareas con fechas, duración y dependencias) y en el diagrama de Gantt de la Figura 5. Los hitos son el 17 de octubre y el 17 de noviembre.

**¿Cómo se fundamenta lo biopsicosocial?**
Con Engel (1977) y López-Cruz. Recorrer la Tabla 4: cada dimensión tiene una decisión de diseño concreta. Por ejemplo, la pausa del mediodía es biológica, y la validación inmediata reduce el estrés de quien cuadra el horario, que es lo psicológico.

**¿Qué es innovador?**
La preselección (S-20, que surgió de la observación del profesor). El estudiante participa antes de que se apruebe el horario y el Administrador decide con datos de demanda. Según el Manual de Oslo, solo será una innovación cuando se ponga en uso.

**¿Por qué no se busca el horario óptimo?**
Porque el problema es NP-completo (Even et al.) y Simon muestra que la calidad global depende de preferencias que se descubren al ver la propuesta. Por eso el sistema propone y la persona decide.

**¿Cómo se garantiza que no haya choques si dos personas mueven a la vez?**
Con RNF-08: las tablas de ocupación con UNIQUE en Oracle. De dos solicitudes simultáneas para el mismo bloque, una se confirma y la otra falla.

**¿Qué es la trazabilidad?**
Cada solicitud S lleva a uno o más RF/RNF, que llevan a una especificación EF/ENF, que lleva a un caso de prueba P (Tablas 16 y 20 a 22). En el código, el documento de trazabilidad mapea cada RF a sus clases y métodos.

---

## 14. Ruta sugerida para la demo

La idea es mostrar primero que el sistema **detecta los problemas** y luego llegar a un horario donde todo se cumple.

- **Iniciar sesión** como Administrador y mostrar que un Estudiante no ve las funciones de administración (RF-02).
- **Parámetros:** mostrar la franja, la pausa, el cupo de 40 y la tolerancia del 10 % (RF-11, RNF-01).
- **Crear un docente** con una restricción, por ejemplo lunes de 7 a 16, y una habilitación (RF-05 a RF-07).
- **Generar la propuesta** y mostrar las sesiones sin ubicar con su causa (RF-15, RF-16).
- **Provocar errores a propósito:**
  - Mover una sesión al cruce de las 12–13: **FRA-PAU**.
  - Moverla sobre otra del mismo docente: **CHQ-DOC**.
  - Moverla a la restricción del docente: **DOC-RES**.
  - Moverla a un aula común cuando la asignatura pide computadores: **AUL-TIP**.
  - En todos los casos el movimiento se rechaza y se ve el motivo (RF-19).
- **Provocar una advertencia:** poner dos sesiones de la misma asignatura el lunes y el martes. Se guarda y muestra **CON-DIA** (RF-27).
- **Intentar aprobar** con pendientes: se rechaza (RF-28).
- **Corregir hasta tener 0 errores y 0 sesiones sin ubicar** y abrir la preselección (RF-31).
- **Como estudiante:**
  - elegir grupos que chocan y ver el choque;
  - corregir la elección y confirmar;
  - exportar el CSV (RF-32 a RF-34).
- **Como Administrador:**
  - ver la demanda frente al cupo (RF-35);
  - **aprobar**;
  - consultar por docente y exportar el CSV (RF-29, RF-30).
- **Cerrar la preselección** y mostrar que el estudiante sigue viendo su horario en modo solo lectura.
