/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
 */

// Variables globales
var express = require('express');
var db = require('../utils/conn');
var router = express.Router();

/**
 * Ruta para agregar un usuario nuevo al sistema.
 */
router.post('/add', function (req, res, next) {
  
  //Nueva instancia del cliente de MongoDB.
  let client = db.client();

  //Intento de conexión.
  client.connect(err => {
    if (err) {
      //Fallo de conexión
      res.json(err).status(500);
    } else {
      //Conexión exitosa
      //Llenado del modelo del usuario a ingresar.
      let user = {
        name: req.body.nombre,
        lastName: req.body.apellido,
        email: req.body.correo,
        username: req.body.usuario,
        passwd: req.body.password,
        gender: req.body.genero
      }

      //Conexión con la colección de users
      const collection = client.db(process.env.DATABASE_NAME).collection("users");

      //Guardado del usuario en la base de datos
      collection.insertOne(user).then(value => {
        //Guardado exitoso
        res.json({ res: JSON.stringify({"data": "Se ha registrado correctamente", "codigo": 200 })}).status(500);
      }).catch(error => {
        if (error.keyValue.username) {
          //Usuario ya existente
          res.json({ res: JSON.stringify({"data": "El nombre de usuario que intenta registrar ya está en uso", "codigo": 500 })}).status(500);
        } else if (error.keyValue.email) {
          //Correo ya existente
          res.json({ res: JSON.stringify({"data": "La dirección de correo ingresada ya está en uso", "codigo": 500 })}).status(500);
        } else {
          //Otros errores
          res.json({ res: JSON.stringify({"data": "Ocurrió un error inesperado", "codigo": 500 })}).status(500);
        }

      })
    }
  });
});

/**
 * Ruta para verificar el inicio de sesión desde la app.
 */
router.get('/validate', function (req, res, next) {

  var usuario = req.headers.usuario;
  var password = req.headers.password;

  //Nueva instancia del cliente de MongoDB.
  let client = db.client();

  //Intento de conexión.
  client.connect(err => {
    if (err) {
      //Fallo de conexión
      res.json(err).status(500);
    } else {
      //Conexión exitosa
      
      //Conexión con la colección de users
      const collection = client.db(process.env.DATABASE_NAME).collection("users");

      //Búsqueda del usuario con los datos recibidos desde la app.
      collection.find({ username: usuario, passwd: password }).project({ "_id": 0})
      .toArray((err, result) => {

        if (err) res.json({ user: JSON.stringify({
          //Error del servidor
          "data": {
            name:"$",
            lastName:"$",
            email:"$",
            username:"$",
            passwd:"$",
            gender:"$"
          } 
        }) }).status(500);

        if (result.length == 0){
          //Usuario no coincide (Fallo al inciar sesión)
          res.json({ user: JSON.stringify({
            "data": {
              name:"-",
              lastName:"-",
              email:"-",
              username:"-",
              passwd:"-",
              gender:"-"
            } 
          }) }).status(401)
        } else {
          //Sesión inciada, se envían los datos del usuario a la app.
          res.json({ user: JSON.stringify({ "data": result[0] }) }).status(200);
          client.close();
        }


      });
      
    }
  })

})

module.exports = router;
