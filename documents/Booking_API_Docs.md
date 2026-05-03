# Documentación del Flujo de Reservas Anidadas (Booking Flow)
Este documento detalla el nuevo flujo para crear una reserva (`Booking`) que contiene múltiples pasajeros (`Passengers`), y donde cada pasajero puede llevar cero o más equipajes (`Baggage`).
## 1. Concepto General y Modelo de Datos
La relación de la base de datos es ahora en cascada:
- **`1 User -> N Bookings (Reservaciones)`** (El dueño de la sesión).
- **`1 Booking -> N Passengers (Pasajeros)`** (Cada pasajero en esa reserva tiene su propio asiento).
- **`1 Passenger -> N Baggages (Mochilas/Equipaje)`** (El equipaje pertenece al pasajero, NO directamente a la reserva).
Al crear una reserva nueva, **el payload acepta toda la estructura de forma anidada**. Todo se inserta en un solo hit de base de datos.
El código de seguimiento (`trackingCode`) del equipaje se genera **automáticamente** por el sistema en el backend.
## 2. Endpoints de Creación de Reserva
### A. Desde el Portal Cliente (`POST /api/v1/bookings/me`)
- **Autorización:** Token JWT de Pasajero.
- **Acción:** Crea la reserva asociada al `User` autenticado en la sesión.
- **Payload esperado:** `BookingPassengerRequestDTO`
### B. Desde el Portal Administrador (`POST /api/v1/bookings`)
- **Autorización:** Token JWT de ADMIN.
- **Acción:** Funciona igual, pero acepta un campo extra opcional `userId` para asignar la reserva a un cliente arbitrario.
## 3. Ejemplo de JSON (Payload para creación con Equipaje incluido)
Para registrar una reserva con pasajeros y su equipaje, el cliente debe enviar un JSON como este:
```json
{
  "flightId": 15,
  "bookingClass": "ECONOMICA",
  "passengers": [
    {
      "firstName": "Juan",
      "lastName": "Pérez",
      "email": "juan.perez@example.com",
      "identityDocument": "123456789",
      "phoneNumber": "555-1234",
      "seatNumber": "12A",
      "baggages": [
        {
          "weight": 15.5
        },
        {
          "weight": 20.0
        }
      ]
    },
    {
      "firstName": "María",
      "lastName": "Gómez",
      "email": "maria.gomez@example.com",
      "identityDocument": "987654321",
      "phoneNumber": "555-4321",
      "seatNumber": "12B",
      "baggages": [] 
    }
  ]
}
```
> **NOTA SOBRE EL EQUIPAJE:** 
> - En el arreglo `baggages`, **sólo** se debe especificar la propiedad `weight` (peso) como flotante/decimal mayor a 0.
> - Si un pasajero no lleva mochilas, simplemente se envía una lista vacía `[]` o no se envía la propiedad.
> - No se envían IDs internos; el backend liga la maleta con el pasajero automáticamente.
## 4. Estructura de DTOs Involucrados
### `PassengerBookingRequestDTO`
```java
@NotBlank private String firstName;
@NotBlank private String lastName;
@NotBlank private String email;
@NotBlank private String identityDocument;
private String phoneNumber;
@Size(max = 10) private String seatNumber; // El asiento va por pasajero
@Valid private List<BaggageRequestDTO> baggages; // <- Lista de maletas anidadas
```
### `BaggageRequestDTO`
```java
@NotNull 
@DecimalMin(value = "0.0", inclusive = false)
private BigDecimal weight; 
// <- Sólo se pide el peso. El status ("REGISTRADO") y el id autogenerado lo maneja el Backend.
```
## 5. Respuestas de API y Consultas
Cuando el cliente llame a `GET /api/v1/bookings/me`, el backend retornará la estructura en forma de árbol, devolviendo la reserva, dentro el listado de pasajeros, y dentro de cada pasajero su listado de equipajes creados (con su ID y peso).

### Ejemplo de Respuesta (`GET /api/v1/bookings/me`)
```json
[
  {
    "id": 1,
    "flight": {
      "id": 15,
      "flightNumber": "AA123",
      "source": "Madrid",
      "destination": "Barcelona"
    },
    "bookingClass": "ECONOMY",
    "status": "CONFIRMADA",
    "bookingDate": "2026-05-02T10:30:00",
    "passengers": [
      {
        "id": 1,
        "firstName": "Juan",
        "lastName": "Pérez",
        "email": "juan.perez@example.com",
        "identityDocument": "123456789",
        "phoneNumber": "555-1234",
        "seatNumber": "12A",
        "baggages": [
          {
            "trackingCode": "TRK-001-ABC123",
            "weight": 15.5
          },
          {
            "trackingCode": "TRK-001-ABC124",
            "weight": 20.0
          }
        ]
      },
      {
        "id": 2,
        "firstName": "María",
        "lastName": "Gómez",
        "email": "maria.gomez@example.com",
        "identityDocument": "987654321",
        "phoneNumber": "555-4321",
        "seatNumber": "12B",
        "baggages": []
      }
    ]
  }
]
```

> **NOTA:** Cada pasajero ahora devuelve un array `baggages` con sus equipajes, mostrando el `trackingCode` (código de rastreo) y su `weight` (peso).
