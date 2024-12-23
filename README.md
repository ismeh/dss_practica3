# Práctica 3 DSS
Aplicación Android usando Kotlin para integrar aplicación RESTful de comercio electrónico.

Añadiremos [mapas de Google](https://developers.google.com/maps/documentation/android-sdk/start?hl=es-419)
Las imágenes de los iconos se han extraido del sistema de imágenes de Android Studio o bien desde 
[Material Design](https://fonts.google.com/icons)

## Instrucciones de Instalación y Ejecución

1. Descomprime el zip del proyecto

2. Abre el proyecto en Android Studio.

3. Configura tu clave de API de Google Maps:
    - Crea un archivo `secrets.properties` en la raíz del proyecto (si no existe).
    - Añade tu clave de API:
        ```properties
        MAPS_API_KEY=TU_API_KEY
        ```

4. Sincroniza el proyecto con Gradle.

5. Ejecuta la aplicación en un dispositivo o emulador Android.

## Dependencias Usadas

- **Retrofit**: Para realizar solicitudes HTTP.
- **Gson**: Para la serialización y deserialización de JSON.
- **Google Maps API**: Para integrar mapas de Google en la aplicación.

## Estructura de Carpetas y Organización del Código
project-root/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │   	└── dss/
│   │   │   │       	└── practica3/
│   │   │   │           	├── MainActivity.kt
│   │   │   │           	├── api/
│   │   │   │           	│   ├── ApiClient.kt
│   │   │   │           	│   └── ApiService.kt
│   │   │   │           	├── dao/
│   │   │   │           	│   └── CartItemDao.kt
│   │   │   │           	├── database/
│   │   │   │           	│   └── AppDatabase.kt
│   │   │   │           	├── models/
│   │   │   │           	│   ├── CartItem.kt
│   │   │   │           	│   └── Product.kt
│   │   │   │           	├── services/
│   │   │   │           	│   └── CartService.kt
│   │   │   │           	└── ui/
│   │   │   │           	│   └── admin/
│   │   │   │           	│   │   └── AdminAdapter.kt
│   │   │   │           	│   │   └── AdminFragment.kt
│   │   │   │           	│   └── cart/
│   │   │   │           	│   │   └── CartAdapter.kt
│   │   │   │           	│   │   └── CartFragment.kt
│   │   │   │           	│   └── googlemap/
│   │   │   │           	│   │   └── MapsActivity.kt
│   │   │   │           	│   └── home/
│   │   │   │           	│   │   └── HomeFragment.kt
│   │   │   │           	│   │   └── ProductAdapterFragment.kt
│   │   │   │               	├── CartFragment.kt
│   │   │   │               	└── ProductFragment.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── fragment_cart.xml
│   │   │   │   │   └── fragment_product.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   └── xml/
│   │   │   │   	└── file_paths.xml
│   │   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle

- **api/**: Contiene las interfaces para las llamadas a la API.
- **dao/**: Contiene las interfaces de acceso a datos para Room.
- **database/**: Contiene la configuración de la base de datos.
- **models/**: Contiene las clases de datos.
- **services/**: Contiene los servicios de la aplicación.
- **ui/**: Contiene las actividades y fragmentos de la interfaz de usuario.
- **res/**: Contiene los recursos de la aplicación (layouts, drawables, etc.).

## Lista de Endpoints API Utilizados

- **GET /api/products**: Obtiene todos los productos.
- **GET /api/products-by-id**: Obtiene un producto por su ID.
- **GET /api/login**: Inicia sesión con un nombre de usuario y contraseña, devuelve un token.
- **GET /api/checkPrivileges**: Verifica los privilegios de un usuario.
- **GET /api/products/add**: Añade un nuevo producto.
- **GET /api/products/edit**: Edita un producto existente.
- **GET /api/products/delete**: Elimina un producto.
- **GET /api/cart/checkout**: Compra los items del carrito y devuelve un ticket.