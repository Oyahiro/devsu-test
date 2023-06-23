# Devsu Test

Simulación de cuenta bancaria.

## Construcción

Sistema construido con Java 11 y Spring Boot 2.6.7.  
El sistema cuenta con la implementación del estándar JWT.  
Se usó mysql como gestor de base de datos.
Se encuentra adjunto el script inicial de la base de datos  y una colección en postman para probar la API.

## Indicaciones
Al registrar un nuevo empleado el endpoint retorna el nombre de usuario y la contraseña será su número de cédula.  
El nombre de usuario del administrador es *0941106445* y la contraseña es *0941106445*.  
Dentro de la colección de postman se encuentran ejemplos de la ejecución para poder probar el funcionamiento del sistema.  
El ADMIN tiene acceso a todos los endpoints. Los clientes creados siempre tendran role CLIENT por lo que tendran acceso restringido, se recomienda usar el usuario de ADMIN para probar la API.
Si por algun motivo se desea exponer todos los endpoints sin seguridad, descomentar la linea 66 de *SecurityConfiguration*.
