Sistema de Gestión de Heladería

Descripción:

Sistema de gestión desarrollado en Java para administrar las operaciones de una heladería. Permite gestionar productos, sabores, stock, clientes, empleados, pedidos, ventas y servicios de catering.

El proyecto busca digitalizar las tareas habituales de una heladería, diferenciando las funcionalidades disponibles para el dueño y los cajeros.

Funcionalidades principales:

Gestión de productos: administración de conos, potes y postres helados.

Gestión de sabores: registro de sabores, consulta de stock y control de disponibilidad.

Gestión de bachas: administración de stock y capacidad de las bachas.

Gestión de personas: registro y consulta de clientes y empleados.

Gestión de pedidos: realización de pedidos locales y delivery.

Delivery: cálculo de costos según la distancia de entrega.

Catering: gestión de reservas para eventos.

Ventas: registro de operaciones y manejo de caja.

Persistencia: almacenamiento de información mediante una base de datos MySQL.

Tecnologías utilizadas:

Java

MySQL

JDBC (Java Database Connectivity)

IntelliJ IDEA

Git y GitHub
Pasos

Clonar el repositorio:

git clone https://github.com/quiimi/GestionDeHeladeria.git

Abrir el proyecto desde IntelliJ IDEA.

Verificar que el archivo mysql-connector-j-26.10.0.jar, ubicado en la carpeta lib, esté configurado como dependencia del proyecto.

Configurar los datos de conexión a MySQL en ConeccionDB.java:

URL de conexión.

Usuario.

Contraseña.

Crear o verificar la base de datos configurada en la URL de conexión.

Ejecutar la clase Main.java.

Importante: el conector JDBC está incluido en el proyecto, pero MySQL Server debe estar instalado y configurado en el equipo donde se ejecute la aplicación.

Autores:

Tobias Gonzalez

Quimey Aquino

