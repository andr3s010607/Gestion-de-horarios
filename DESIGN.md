---
name: Gestión de Horarios
description: Sistema académico cálido y organizado para planificar jornadas semestrales.
colors:
  olive: "#6B705C"
  olive-deep: "#5B604E"
  sand: "#F5EDE3"
  cream: "#FFFDF8"
  terracotta: "#CB997E"
  sage: "#C5C7AD"
  ink: "#393D31"
  muted-ink: "#67695E"
  field-border: "#CEC6B7"
typography:
  display:
    fontFamily: "Cormorant Garamond, Georgia, serif"
    fontSize: "clamp(2.85rem, 5.4vw, 5.6rem)"
    fontWeight: 600
    lineHeight: 0.95
    letterSpacing: "-0.035em"
  body:
    fontFamily: "Manrope, sans-serif"
    fontSize: "0.94rem"
    fontWeight: 500
    lineHeight: 1.5
  label:
    fontFamily: "Manrope, sans-serif"
    fontSize: "0.65rem"
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "0.17em"
rounded:
  field: "0.68rem"
  surface: "1rem"
  card: "1.2rem"
spacing:
  compact: "0.58rem"
  control: "1.5rem"
  section: "2.85rem"
  surface: "4.5rem"
components:
  button-primary:
    backgroundColor: "{colors.olive}"
    textColor: "{colors.cream}"
    rounded: "{rounded.field}"
    padding: "0 1.5rem"
    height: "3.45rem"
  input-field:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    rounded: "{rounded.field}"
    height: "3.2rem"
  elevated-card:
    backgroundColor: "{colors.cream}"
    rounded: "{rounded.card}"
    padding: "clamp(2.5rem, 5vw, 4.5rem)"
---

# Design System: Gestión de Horarios

## Overview

**Creative North Star: "El folio académico vivo"**

La interfaz trata la planificación como una composición editorial: una superficie de papel cálido, jerarquía tipográfica calma y datos académicos que se leen como una agenda cuidadosamente anotada. La naturaleza está presente como cadencia y materialidad, no como decoración literal: el verde oliva organiza, la terracota señala y el crema deja respirar el contenido.

La densidad cambia con la tarea. El acceso es amplio y ceremonial; los horarios, tablas y futuras áreas de operación concentran información mediante reglas suaves, etiquetas pequeñas y color con propósito.

**Key Characteristics:**

- Superficies crema con tinta oliva y líneas sepia discretas.
- Serif editorial para decisiones y momentos de orientación; sans sobria para datos y controles.
- Bloques de horario con color armónico, legibles antes que llamativos.
- Profundidad ambiental y esquinas suavemente redondeadas.

## Colors

El color organiza prioridades y estados académicos; nunca se usa como relleno saturado.

### Primary

- **Oliva de coordinación:** control principal, foco y navegación activa.
- **Terracota de señal:** sesiones alternas, hitos y acentos escasos.

### Secondary

- **Salvia de agenda:** bloques de sesión de prioridad estable y estados correctos.

### Neutral

- **Arena editorial:** fondo dominante y superficie de lectura amplia.
- **Crema de papel:** tarjetas y paneles elevados.
- **Tinta de aula:** texto principal.
- **Tinta atenuada:** apoyo textual, horarios y metadatos.

**The Quiet Accent Rule.** La terracota se reserva para distinguir; el oliva sostiene la acción. Ninguna pantalla debe convertir ambos en fondos predominantes a la vez.

## Typography

**Display Font:** Cormorant Garamond, Georgia, serif.
**Body Font:** Manrope, sans-serif.

**Character:** La serif crea una entrada institucional y humana; la sans mantiene los datos, acciones y formularios nítidos a cualquier escala.

### Hierarchy

- **Display:** se usa para titulares de orientación y no más de una vez por vista principal.
- **Headline:** conduce el foco de un panel o sección operativa.
- **Body:** explica una decisión o instrucción en medidas cortas y aireadas.
- **Label:** identifica campos, etapas y encabezados de calendario con mayúsculas y espaciado amplio.

**The Two Voices Rule.** La serif no compite con tablas, filtros ni acciones; Manrope gobierna todo contenido operativo.

## Layout

El sistema alterna superficies de lectura amplias con paneles de tarea concentrados. En escritorio el acceso usa dos columnas: contexto académico a la izquierda y tarea protegida a la derecha. Bajo 980px se apilan; bajo 620px la agenda se simplifica sin desaparecer.

El espacio se distribuye con grupos compactos dentro de controles y pausas generosas entre secciones. Las grillas de datos deben conservar encabezados, filas y estados visibles antes de añadir ornamento.

## Elevation & Depth

La elevación es ambiental: las tarjetas crema se levantan con sombras suaves y desplazadas, nunca con bordes pesados. El foco de un campo usa un halo oliva tenue, como retroalimentación funcional.

### Shadow Vocabulary

- **Surface lift** (`0 14px 34px rgba(67,62,47,.10)`): vista de agenda contextual.
- **Card lift** (`0 20px 45px rgba(67,62,47,.14)`): tarea primaria que requiere concentración.

**The Paper Lift Rule.** Una sombra solo aparece cuando una superficie se separa funcionalmente de su papel de fondo.

## Shapes

Las superficies de contenido usan esquinas de 16px; controles y botones, esquinas de aproximadamente 11px. Las píldoras solo pertenecen a etiquetas pequeñas o filtros futuros, no a tarjetas ni a navegación estructural.

Las reglas de 1px, los puntos de etapa y los círculos de contorno son geometría de agenda: sirven para ordenar, nunca para simular ilustraciones.

## Components

### Buttons

- **Shape:** rectángulo de bordes suaves (`0.68rem`) y altura de 3.45rem.
- **Primary:** oliva con texto crema; el hover oscurece el oliva y lo eleva apenas.
- **Focus:** anillo terracota semitransparente con desplazamiento visible.

### Cards / Containers

- **Corner Style:** superficies principales de `1rem` a `1.2rem`.
- **Background:** crema sobre arena o beige salvia.
- **Shadow Strategy:** elevación ambiental, sin borde duplicado.
- **Internal Padding:** amplio en tareas de acceso; compacto y reglado en horarios.

### Inputs / Fields

- **Style:** contorno arena atenuado, icono lineal oliva y fondo transparente.
- **Focus:** el contorno cambia a oliva y gana halo de 3px.
- **Error / Disabled:** el error usa fondo terracota pálido; los estados desactivados reducen opacidad, nunca contraste crítico.

### Schedule Blocks

- **Style:** bloques compactos oliva, arena o terracota con texto oscuro.
- **Behavior:** el color identifica familias o estados, mientras nombre, aula y hora preservan la lectura accesible.

## Do's and Don'ts

### Do:

- **Do** usar crema y arena como espacio de respiración antes de sumar paneles.
- **Do** mostrar datos académicos como evidencia visual del propósito de la interfaz.
- **Do** preservar jerarquía, foco visible y etiquetas reales en todos los formularios.

### Don't:

- **Don't** usar gradientes, neones, vidrio decorativo o colores saturados.
- **Don't** convertir ilustraciones botánicas en relleno repetido; la naturaleza se expresa mediante ritmo, paleta y material.
- **Don't** usar la serif para datos densos, tablas, filtros o controles.
