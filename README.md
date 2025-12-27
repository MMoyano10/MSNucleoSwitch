# Microservicio Nucleo - Orquestador del Switch Transaccional

El microservicio **Nucleo** es el cerebro del switch. Coordina la interacción entre los 5 servicios del ecosistema para garantizar que cada transacción se procese, registre y liquide correctamente.

## 🏗️ Arquitectura del Switch (5 Microservicios)

1.  **Nucleo (switch-nucleo-service)**: Orquestador principal, dueño del estado y la idempotencia.
2.  **Directorio (switch-directorio-service)**: Catálogo de instituciones, reglas de enrutamiento y Circuit Breakers.
3.  **Contabilidad (switch-contabilidad-service)**: Libro mayor inmutable, saldos técnicos y validación de fondos.
4.  **Compensación (switch-compensacion-service)**: Cierre de ciclos horarios y cálculo de posiciones netas.
5.  **Devoluciones (switch-devoluciones-service)**: Gestión de excepciones y mapeo de errores ISO 20022.

---

## 🔄 Flujo de Orquestación en Nucleo

Cuando llega una petición a través de `/api/v1/transacciones`, el Nucleo ejecuta los siguientes pasos:

1.  **Idempotencia**: Verifica en `RESPALDO_IDEMPOTENCIA` si el `idInstruccion` ya fue procesado.
2.  **Registro Inicial**: Crea la entrada en `TRANSACCION` con estado `RECEIVED`.
3.  **Consulta a Directorio**: Verifica si el `codigoBicDestino` es válido y si el banco está operativo.
4.  **Autorización Contable**: Solicita al micro de **Contabilidad** la reserva de fondos o el débito en la cuenta técnica.
5.  **Ruteo**: Envía la transacción al banco destino (o conector) y actualiza el estado a `COMPLETED` / `ROUTED`.
6.  **Gestión de Errores**: Si falla cualquier paso, consulta al micro de **Devoluciones** para obtener el código ISO correspondiente y marcar la transacción como `FAILED`.

## 🛠️ Stack Tecnológico

- **Java 21** / **Spring Boot 3.5.9**
- **Spring Data JPA** & **PostgreSQL 17**
- **Lombok** & **Hibernate Validator**
- **Docker** & **SpringDoc (Swagger)**

## ⚙️ Configuración y Ejecución

### Usando Docker Compose
```bash
docker-compose up --build
```

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **PostgreSQL**: `localhost:5432` (Credenciales en `application.properties`)

## 📋 Ejemplo de Uso (REST)

**POST `/api/v1/transacciones`**
```json
{
  "idInstruccion": "550e8400-e29b-41d4-a716-446655440000",
  "idMensaje": "MSG-20251226-001",
  "referenciaRed": "RED-TRAN-001",
  "tipoOperacion": "TRANSFERENCIA",
  "monto": 500.00,
  "moneda": "USD",
  "codigoBicOrigen": "BANCO_A",
  "codigoBicDestino": "BANCO_B"
}
```

---
**Desarrollado para la Arquitectura de Switch Bancario Interbancario**
