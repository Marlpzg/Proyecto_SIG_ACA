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

var express = require('express');
var db = require('../utils/conn');
var router = express.Router();

router.get('/', function (req, res, next) {
  res.send('respond with a resource');
});

router.post('/add', function (req, res, next) {
  let client = db.client();

  client.connect(err => {
    if (err) {
      res.json(err).status(500);
    } else {
      let user = {
        name: req.body.nombre,
        lastName: req.body.apellido,
        email: req.body.correo,
        username: req.body.usuario,
        passwd: req.body.password,
        gender: req.body.genero
      }
      const collection = client.db(process.env.DATABASE_NAME).collection("users");
      //collection.createIndex( { "username" : 1 }, { unique : true } );
      //collection.createIndex( { "email" : 1 }, { unique : true } );
      collection.insertOne(user).then(value => {
        res.status(200).json({msg: "Usuario registrado exitosamente."});
      }).catch(error =>{
        if(error.keyValue.username){
          res.status(500).json({msg: "El nombre de usuario ya existe."});
        }else if(error.keyValue.email){
          res.status(500).json({msg: "Esa dirección de correo ya ha sido utilizada."});
        }else{
          res.status(500).json({msg: "Ocurrió un error al ingresar el usuario."});
        }
        
      })
    }
  });
});

router.get('/search', function (req, res, next) {

  var usuario = req.body.usuario;
  var contra = req.body.password;




})

module.exports = router;
