# Bumpify Network
![logo](https://drive.google.com/uc?export=view&id=1o-_Wlg6FM7584zrLvUBCgzCTlioBj48L "Logo Bumpify")
## Acerca de Bumpify
Bumpify es un sistema de Geolocalización e tiempo real, que permite que los usuarios realicen reportes de eventos relacionados a la seguridad como asaltos y asesinatos, y realcionados al sistema vial como choques, obstáculos y baches en el camino.<br/><br/>
![login](https://drive.google.com/uc?export=view&id=1qjbeXv_oiB6M9H5s1YmgHX18lvCkra1M "Log In Bumpify")
![map](https://drive.google.com/uc?export=view&id=1-AZmfs8gzcrcirDW43UxDYQ0PFevNM4q "Bumpify map")<br/>
La información de seguridad permite que Bumpify establezca los niveles de peligro de los alrededores y así los usuarios puedan determinar con cuanta cautela deben movilizarse por una zona específica; mientras que la información vial permite que los usuarios evalúen las rutas que tomarán visualizando el estado actual de la red vial.<br/><br/>
Bumpify es un sistema conformado por un servidor central que contiene una API para comunicarse con la Bumpify App, que permite interactuar con los datos del sistema y presenta de manera gráfica los análisis realizados por la Bumpify API.<br/>
La API es esencial para el correcto funcionamiento de la App, por lo que deberá proveerse de un acceso a la API en un servidor dedicado.
## Configuración
### Levantar Bumpify API
#### Requerimientos
Bumpify API requiere NodeJS, npm y MongoDB para su correcto funcionamiento.<br/>
#### Proceso de configuración
Inicialmente deberá crearse la base de datos en MongoDB y configurar la conexión en el archivo conn.js contenido en la carpeta utils.<br/>
![conn.js](https://drive.google.com/uc?export=view&id=1nRd_TTAbgi5brIP6Rs8Ansfv9tG5t4Iu "Ubicación del archivo conn.js")<br/>
Originalmente esta configuración se ha realizado para conectar a una base de datos de MongoDB Atlas, por lo que deberá modificarse ligeramente si se desea conectar a una instalación de MongoDB.<br/>
![uriconn.js](https://drive.google.com/uc?export=view&id=1a7FnfyxCpl8b2_v1ftyYeoc64DetOEgX "URI a modificar en conn.js")<br/>
Se puede observar que dentro de la URI de la conexión se contienen algunas variables de entorno, el contenido de estas puede visualizarse en el archivo .env en la raíz de BumpifyAPI. Modificar ese archivo a conveniencia.<br/>
![envfile](https://drive.google.com/uc?export=view&id=1pXUXrvI03obYRl-SGIBXfxflLiJvazOd "Variables de entorno a modificar")<br/>
Recordar además que debe establecerse un usuario y contraseña con acceso a la base de datos.<br/>
En un entorno de desarrollo la API deberá levantarse utilizando npm, se recomienda el uso de Nodemon para reiniciar el servidor automáticamente al guardar cambios. En un entorno de producción deberá levantarse la aplicación de otra forma apropiada a elección libre.<br/>
El servidor ejecutándose deberá mostrar la siguiente pantalla principal:<br/>
![mainscreen](https://drive.google.com/uc?export=view&id=1pjmBJFbNIXEvq50G6ZxPUuAFd0k36XrX "Pantalla principal")<br/>
### Instalación de Bumpify App
#### Requerimientos
Bumpify App requiere Android Studio para visualizar el código fuente y realizar modificaciones. Desde la misma plataforma es posible generar una APK para instalar en cualquier dispositivo Android con API 23 o superior.<br/><br/>
**IMPORTANTE:** Bumpify App requiere una instancia de Bumpify API para funcionar.<br/>
#### Proceso de configuración
La única configuración que deberá realizarse dentro de Bumpify App será establecer el servidor al que se conectará. Para eso se debe acceder al archivo Constants.kt dentro de la carpeta utils:<br/>
![constantsloc](https://drive.google.com/uc?export=view&id=1ZPC6lUQIHPodGj_kOd1uBhJ8E0BKDyGk "Directorio de constants")<br/>
Una vez dentro de ese archivo solo deberá reemplazarse el valor de la constante REQ_URL por la dirección del servidor donde esté corriendo la instancia de Bumpify API:<br/>
![apiurl](https://drive.google.com/uc?export=view&id=1QRuTPx8GtYYb6-TnOw2hXPtWQ7YaDJiv "Constante REQ_URL")<br/>
## Manual de Usuario
Puedes echarle un vistazo a nuestro Manual de Usuario para tener una mejor idea de cómo utilizar Bumpify App:
[Manual Bumpify App](https://drive.google.com/file/d/1lEzvY9JP8KIV5Q9lBKDDL-9sqoFXY0YD/view?usp=sharing "Manual de Usuario Bumpify App").<br/>
## Trabajo colaborativo
Si quieres aportar al desarrollo de Bumpify Network puedes crear tu propia Branch en la que trabajar características nuevas o mejorar las ya existentes. También puedes reportar Issues que hayas experimentado con Bumpify App o Bumpify API en este repositorio.<br/><br/>
No te olvides de echar un vistazo a nuestro tablero, ahí agregamos las características en las que estamos trabajando para futuras actualizaciones:<br/>
![tablero](https://drive.google.com/uc?export=view&id=1ZFQTu8GItz2Cv6YTuUoR-DSpSVZa53t6 "Tablero")<br/>
Puedes acceder al tablero siguiendo este [enlace](https://app.gitkraken.com/glo/board/YJscrAVPnQASYX1_ "Tablero de Bumpify").<br/>
### ¡Gracias por colaborar con nosotros!
## Licencia
Copyright (c) Sultral Corp. Todos los derechos reservados.<br/>
Licenciado bajo la [licencia Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0 "Licencia Apache 2.0").
