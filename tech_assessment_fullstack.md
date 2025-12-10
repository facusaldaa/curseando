# Prueba Técnica Full-Stack Developer
## Plataforma de Cursos Online "Curseando"

### Descripción del Proyecto

Desarrollar una aplicación web básica que simule una plataforma educativa online con las siguientes funcionalidades principales:

1. **Catálogo de Cursos**: Mostrar una lista de cursos disponibles con información básica
2. **Inscripción a Cursos**: Permitir a los usuarios inscribirse a cursos específicos

### Requisitos Técnicos

#### Backend (Java + Spring Boot)
- **Framework**: Spring Boot 3.x
- **Base de datos**: H2 (en memoria) o PostgreSQL
- **Arquitectura**: API REST / Monolito

#### Frontend (Angular + TypeScript)
- **Framework**: Angular 15+
- **Lenguaje**: TypeScript
- **Diseño**: Diseñarlo usando los estilos de la web de **Coursera**

### Funcionalidades Requeridas

#### 1. Catálogo de Cursos

**Historia de Usuario:**
> Como visitante de la plataforma, quiero ver un catálogo de cursos disponibles para poder explorar las opciones educativas y decidir en cuál inscribirme.

**Criterios de Éxito:**

**CE-1.1**: El sistema debe mostrar una lista de todos los cursos disponibles en la página principal

**CE-1.2**: Cada curso debe mostrar al menos la siguiente información:
   - Título del curso
   - Nombre del instructor
   - Duración estimada
   - Nivel de dificultad (principiante, intermedio, avanzado)
   - Cupos disponibles vs capacidad máxima

**CE-1.3**: El usuario debe poder filtrar los cursos por nivel de dificultad

**CE-1.4**: Al hacer clic en un curso, se debe mostrar información detallada incluyendo:
   - Toda la información de la tarjeta
   - Descripción completa del curso
   - Número actual de estudiantes inscritos
   - Opción para inscribirse (si hay cupos disponibles)

**CE-1.5**: Si un curso está lleno, debe indicarse claramente y no permitir inscripción

**Consideraciones Técnicas:**
- Diseñar el modelo de datos que soporte esta funcionalidad
- Implementar API REST que exponga la información necesaria
- La interfaz debe ser responsiva y mostrar los cursos de forma clara
- Los filtros deben funcionar sin recargar la página

---

#### 2. Inscripción a Cursos

**Historia de Usuario:**
> Como estudiante interesado, quiero inscribirme a un curso para poder acceder al contenido educativo y comenzar mi aprendizaje.

**Criterios de Éxito:**

**CE-2.1**: El sistema debe permitir la inscripción solicitando:
   - Nombre completo del estudiante
   - Email válido del estudiante

**CE-2.2**: El sistema debe validar que:
   - El curso tiene cupos disponibles
   - El email proporcionado es válido
   - El estudiante no está ya inscrito en el mismo curso (no duplicados por email)
   - Todos los campos son obligatorios

**CE-2.3**: Al inscribirse exitosamente:
   - El contador de cupos disponibles debe actualizarse inmediatamente
   - Se debe mostrar un mensaje de confirmación al usuario
   - Los datos de inscripción deben persistir en la base de datos

**CE-2.4**: Si la inscripción falla, se debe mostrar un mensaje de error claro indicando la razón.

**Consideraciones Técnicas:**
- Diseñar el modelo de datos que relacione estudiantes con cursos
- Implementar validaciones tanto en frontend como backend
- Manejar concurrencia
- Persistir correctamente la fecha/hora de inscripción
- Considerar estados de inscripción

### Datos de Prueba Iniciales

El sistema debe iniciarse con al menos 4 cursos precargados que permitan probar todas las funcionalidades. Estos cursos deben tener diferentes niveles de dificultad y diferentes estados de disponibilidad de cupos.

**Sugerencia de datos** (Puede ajustarse):
- Al menos un curso para principiantes, uno intermedio y uno avanzado
- Al menos un curso con cupos casi llenos para probar validaciones
- Información realista de cursos de tecnología

### Entregables

1. **Código fuente** Compartir el repositorio Github con commits descriptivos
2. **README.md** con:
   - Instrucciones de instalación y ejecución
   - Capturas de pantalla
3. **Base de datos** Script de base de datos
4. **Aplicación desplegada** Esto se revisará en la entrevista tecnica

