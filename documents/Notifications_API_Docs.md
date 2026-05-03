# Documentación del Sistema de Notificaciones

Este documento detalla el nuevo flujo de notificaciones para los usuarios de FlyTrack, incluyendo creación de reservas, cambios de estado en los vuelos, registro y login, así como la consulta y lectura de notificaciones desde el front-end.

## 1. Concepto General y Modelo de Datos

Las notificaciones ahora se asocian tanto a un `Passenger` como a un `User` de manera opcional:
- Al registrar un usuario o hacer login, se generan notificaciones de tipo `REGISTRO` o `LOGIN` asociadas al `User`.
- Al realizar una reserva, se notifica al `User` (dueño de la sesión o reserva) y a cada `Passenger` de forma individual.
- Cuando ocurre un cambio en el estado de un vuelo o puerta de embarque, se notifica a todos los usuarios y pasajeros asociados a las reservas de ese vuelo.
- Todas las notificaciones generadas en la plataforma también disparan un **envío de correo electrónico** en segundo plano mediante `MailService`.

## 2. Endpoints de Notificaciones (Front-end)

Para el cliente/usuario autenticado, se proporcionan los siguientes endpoints principales:

### A. Consultar notificaciones no leídas (`GET /api/v1/notifications/me`)

- **Autorización:** Token JWT de `User`.
- **Acción:** Devuelve una lista de **únicamente** las notificaciones que no han sido leídas por el usuario autenticado, ordenadas desde la más reciente.
- **Respuesta Esperada (`List<NotificationResponseDTO>`):**

```json
[
  {
    "id": 101,
    "message": "Bienvenido a FlyTrack, Juan Pérez!",
    "type": "REGISTRO",
    "notificationTime": "2026-05-02T10:30:00",
    "read": false,
    "flight": null
  },
  {
    "id": 102,
    "message": "Tu reserva para el vuelo AT120 ha sido confirmada.",
    "type": "RESERVA_CREADA",
    "notificationTime": "2026-05-02T10:35:00",
    "read": false,
    "flight": {
      "id": 5,
      "flightNumber": "AT120",
      "origin": "Madrid",
      "destination": "Barcelona"
    }
  }
]
```

### B. Marcar notificación como leída (`PATCH /api/v1/notifications/{id}/read`)

- **Autorización:** Token JWT de `User`.
- **Acción:** Marca una notificación específica (por su `id`) como leída (`is_read = true`). Una vez marcada como leída, no volverá a aparecer en la ruta `GET /api/v1/notifications/me`.
- **Parámetros:** `id` de la notificación en el path de la URL.
- **Respuestas:**
    - `200 OK` si fue exitoso (No content / void).
    - `404 Not Found` si el ID no existe.

## 3. Tipos de Notificaciones (Enum)

La propiedad `type` del DTO devolverá uno de estos valores representativos en el backend:

- `REGISTRO`: Al crear cuenta.
- `LOGIN`: Al iniciar sesión exitosamente en su cuenta.
- `RESERVA_CREADA`: Al concretar satisfactoriamente una reserva de vuelo.
- `CAMBIO_PUERTA`: Si un administrador cambia la puerta de embarque del vuelo.
- `RETRASO`: Si el vuelo sufre un retraso reportado.
- `CANCELACION`: Si el vuelo ha sido cancelado en el sistema.
- `EMBARQUE`: Notificación de inicio de embarque.

> **NOTA PARA EL FRONT-END:** Puedes utilizar el campo `type` para asignar un ícono o color específico (ej. Rojo para `CANCELACION`, Verde para `REGISTRO` y `RESERVA_CREADA`, etc).

