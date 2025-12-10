# Backend Design Documentation

This directory contains PlantUML design diagrams for the Curseando backend application.

## Diagrams

### `class-diagram.puml`
Domain model showing entity relationships:
- Course, Student, Enrollment entities
- DifficultyLevel enum
- Relationships and constraints

### `architecture.puml`
Layered architecture diagram showing:
- REST API Layer (Controllers)
- Service Layer (Business Logic)
- Repository Layer (Data Access)
- Domain Model (Entities)
- DTOs and Exception Handling
- Database layer

### `enrollment-sequence.puml`
Sequence diagram for the enrollment flow:
- Complete enrollment process
- Validation steps
- Error handling
- Transaction boundaries

## Viewing PlantUML Diagrams

### VS Code / Cursor
Install the "PlantUML" extension:
- Extension ID: `jebbs.plantuml`
- Press `Alt+D` to preview diagrams

### Online
- Copy `.puml` content to [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/)
- Or use [PlantText](https://www.planttext.com/)

### Command Line
```bash
# Install PlantUML (requires Java)
# macOS: brew install plantuml
# Linux: sudo apt-get install plantuml

# Generate PNG
plantuml class-diagram.puml

# Generate SVG
plantuml -tsvg class-diagram.puml
```

## Design Principles

When updating these diagrams:
1. Keep them synchronized with code changes
2. Document important business rules
3. Show transaction boundaries
4. Include error handling flows
5. Update when adding new features

## Integration with Cursor Rules

The cursor rules (`.cursorrules-backend.mdc`) reference these design files. When implementing features:
1. Check existing diagrams for architecture patterns
2. Update diagrams when making structural changes
3. Use diagrams to understand relationships before coding
