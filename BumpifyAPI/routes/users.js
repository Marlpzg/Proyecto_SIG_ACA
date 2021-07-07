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

      collection.insertOne(user).then(value => {
        res.json({ res: JSON.stringify({"data": "Se ha registrado correctamente", "codigo": 200 })}).status(500);
      }).catch(error => {
        if (error.keyValue.username) {
          res.json({ res: JSON.stringify({"data": "El nombre de usuario que intenta registrar ya está en uso", "codigo": 500 })}).status(500);
        } else if (error.keyValue.email) {
          res.json({ res: JSON.stringify({"data": "La dirección de correo ingresada ya está en uso", "codigo": 500 })}).status(500);
        } else {
          res.json({ res: JSON.stringify({"data": "Ocurrió un error inesperado", "codigo": 500 })}).status(500);
        }

      })
    }
  });
});


router.get('/validate', function (req, res, next) {

  var usuario = req.headers.usuario;
  var password = req.headers.password;

  let client = db.client();
  client.connect(err => {
    if (err) {
      res.json(err).status(500);
    } else {
      const collection = client.db(process.env.DATABASE_NAME).collection("users");
      collection.find({ username: usuario, passwd: password }).project({ "_id": 0})
      .toArray((err, result) => {

        if (err) res.json({ user: JSON.stringify({
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
          //console.log(result[0]);
          res.json({ user: JSON.stringify({ "data": result[0] }) }).status(200);
          client.close();
        }


      });
      /*
      .then((value) => {
        res.status(200).send(
          { user: JSON.stringify(
            {"data": JSON.stringify(value)}
          )}
        )
            
      })*/
    }
  })

})

module.exports = router;
