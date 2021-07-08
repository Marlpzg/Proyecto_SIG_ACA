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

//Variables globales
var express = require('express');
var router = express.Router();
var db = require('../utils/conn');
var converter = require('../utils/converter');

//Cosntante que equivale a 1 día en milisegundos
const oneDay = 24 * 60 * 60 * 1000;

/*
Valores que puede tomar "type" en un evento
1 - Asalto
2 - Bache
3 - Obstáculo
4 - Asesinato
5 - Choque
*/

/**
 * La función de esta ruta obtiene los datos de los alrededores basándose en un punto (latitud y longitud).
 */
router.get('/surroundings', function (req, res, next) {

  //Ubicación recibida como parámetro desde la app.
  //Considerar que MongoDB maneja las coordenadas como [longitud, latitud]
  let loc = JSON.parse(req.headers.location);
  let lon = loc.lon;
  let lat = loc.lat;

  //Nueva instancia del cliente de MongoDB.
  let client = db.client();

  //Intento de conexión.
  client.connect(err => {
    if (err) {
      //Fallo de conexión
      res.json(err).status(500);
    } else {
      //Conexión exitosa

      //Conexión con la colección de events
      const collection = client.db(process.env.DATABASE_NAME).collection("events");

      /*Objeto filter que se utiliza para encontrar los eventos de la base de datos con las siguientes condiciones:
        -Eventos que se encuentren en un radio de 5 kilómetros, y
        -Que coincidan con alguna de las siguientes restricciones:
          -Ser de tipo 1 y haberse enviado hace menos de 30 días, o
          -Ser de tipo 2 y haberse enviado hace menos de 7 días, o
          -Ser de tipo 3 y haberse enviado hace menos de 1 día, o
          -Ser de tipo 4 y haberse enviado hace menos de 30 días, o
          -Ser de tipo 5 y haberse enviado hace menos de 6 horas.
      */
      let filter1 = {
        $and: [
          { coords: { $geoWithin: { $centerSphere: [[lon, lat], converter.km2rad(5)] } } },
          {
            $or: [
              { type: 1, date: { $gte: new Date((new Date().getTime() - (30 * oneDay))) } },
              { type: 2, date: { $gte: new Date((new Date().getTime() - (7 * oneDay))) } },
              { type: 3, date: { $gte: new Date((new Date().getTime() - (oneDay))) } },
              { type: 4, date: { $gte: new Date((new Date().getTime() - (30 * oneDay))) } },
              { type: 5, date: { $gte: new Date((new Date().getTime() - (oneDay/4))) } }
            ]
          }
        ]
      }

      /*Ejecución de la búsqueda aplicando el filtro anterior y retornando objetos con la forma:
        evento = {
          coords: <Arreglo de coordenadas>,
          type: <Tipo de evento>,
          desc: <Descripción del evento>,
          date: <Fecha del evento/último reporte>,
          votesNum: <Cantidad de reportes idénticos de otros usuarios>
        }
      */
      collection.find(filter1).project({ "_id": 0, "coords": 1, "type": 1, "desc": 1, "date": 1, "votesNum": {$size: "$votes"} })
        .toArray((err, result) => {

          if (err) res.json(err).status(500) //Fallo de conexión
          else {
            //Conexión exitosa

            //Asignación del resultado a una variable auxiliar.
            let points = result;

            /*Nuevo objeto filter que se utiliza para encontrar los eventos de la base de datos con las siguientes condiciones:
              -Eventos que se encuentren en un radio de 500 metros, y
              -Que hayan sido enviados hace menos de 30 días, y
              -Que coincidan con alguna de las siguientes restricciones:
                -Ser de tipo 1, o
                -Ser de tipo 4
            */
            let filter = {
              $and: [
                {
                  coords: {
                    $geoWithin: {
                      $centerSphere: [
                        [lon, lat],
                        converter.km2rad(0.5)
                      ]
                    }
                  }
                },
                {
                  $or: [
                    { type: 1 },
                    { type: 4 }
                  ]
                },
                { date: { $gte: new Date((new Date().getTime() - (30 * oneDay))) } }
              ]
            }

            /*Ejecución de la búsqueda aplicando el filtro anterior y retornando objetos con la forma:
              evento = {
                user: <usuario que hace el reporte>,
                date: <Fecha del evento/último reporte>,
                type: <Tipo de evento>,
                desc: <Descripción del evento>,
                coords: <Arreglo de coordenadas>,
                votes: <Arreglo de usuarios que han reportado este mismo evento>
              }
            */
            collection.find(filter).project({ "_id": 0 })
              .toArray((err, result) => {

                if (err) res.json(err).status(500) //Fallo de conexión
                else {
                  //Conexión exitosa
                  //Se calcula el "Índice de peligro" basándose en los reportes en el área de 500 metros alrededor del usuario.
                  dangerPoints = 0;
                  result.forEach((p, index) => {
                    dangerPoints++;
                    p.votes.forEach(v => {
                      dangerPoints++;
                    })
                  })

                  //Se devuelven los puntos a 5 km alrededor del usuario y el índice de peligro en un radio de 500 metros. 
                  res.json({ points: JSON.stringify({ "data": points, "dangerLevel": dangerPoints }) }).status(200);
                  client.close();

                }

              });

          }

        });
    }
  });

});

/**
 * La función de esta ruta se encarga de agregar reportes nuevos al sistema.
 */
router.post('/newEvent', function (req, res, next) {

  //Nueva instancia del cliente de MongoDB.
  let client = db.client();

  //Intento de conexión.
  client.connect(err => {
    if (err) {
      //Conexión exitosa
      res.json(err).status(500);
    } else {

      //Conexión exitosa
      //Se invierten los datos de latitud y longitud recibidos ya que MongoDB los controla de forma contraria a OSMDroid
      let lon = req.body.lat;
      let lat = req.body.lon;
      let nDays = 0;

      const collection = client.db(process.env.DATABASE_NAME).collection("events");
      //Definición de los períodos de "validez" del reporte para combinar reportes similares si se encuentran en el tiempo aceptable.
      if (req.body.type == 1 || req.body.type == 4) {
        nDays = 30;
      } else if (req.body.type == 2){
        nDays = 7;
      } else if (req.body.type == 3){
        nDays = 1;
      } else {
        nDays = 0.25;
      }

      /*Filtro que se utiliza para obtener los eventos que cumplan las siguientes condiciones:
        -Encontrarse en un radio de 100 metros del nuevo reporte a ingresar, y
        -Coincidir con el tipo del nuevo reporte, y
        -Encontrarse en el período de validez de ese reporte.
      */
      let filter = {
        $and: [
          {
            coords: {
              $geoWithin: {
                $centerSphere: [
                  [lon, lat],
                  converter.km2rad(0.1)
                ]
              }
            }
          },
          {
            type: req.body.type,
            date: { $gte: new Date((new Date().getTime() - (nDays * oneDay))) }
          }
        ]
      }

      collection.find(filter)
        .toArray((err, result) => {

          if (err) res.json({ res: JSON.stringify({"data": "Error de conexión a la base de datos", "codigo": 500 })}).status(500); //Fallo de conexión.
          else {
            let event = {};
            if (result.length > 0) {
              //Se encontraron eventos similares, por lo que se actualizará la primera coincidencia.
              event = result[0];

              //Si ya existe un "voto" en este evento por parte del usuario no se realiza ninguna acción.
              if (!event.votes.includes(req.body.user) &&
                event.user != req.body.user) {
                event.votes.push(req.body.user);
                //Si no existe un "voto" se agrega el usuario a la lista de votos y se actualiza la fecha.
                collection.updateOne(
                  { "_id": event._id },
                  { $set: { "votes": event.votes, "date": new Date() } })
                  .then(ev => {
                    //Actualización exitosa.
                    res.json({ res: JSON.stringify({"data": "¡Gracias por tu reporte!", "codigo": 200 })}).status(200);
                  }).catch(err => {
                    //Fallo al actualizar.
                    res.json({ res: JSON.stringify({"data": "Ha ocurrido un error al procesar tu reporte", "codigo": 500 })}).status(500);
                  }).finally(() => {
                    client.close();
                  })
              } else {
                res.json({ res: JSON.stringify({"data": "¡Gracias por tu reporte!", "codigo": 200 })}).status(200);
              }

            } else {
              //No se enocntraron eventos similares por lo que se crea uno nuevo llenando el siguiente modelo:
              event = {
                user: req.body.user,
                date: new Date(),
                type: req.body.type,
                desc: req.body.desc,
                coords: [lon, lat],
                votes: []
              }

              //Se ingresa el evento a la base de datos.
              collection.insertOne(event).then(ev => {
                //Evento ingresado.
                res.json({ res: JSON.stringify({"data": "¡Gracias por tu reporte!", "codigo": 200 })}).status(200);
              }).catch(err => {
                //Fallo al ingresar.
                res.json({ res: JSON.stringify({"data": "Ha ocurrido un error al procesar tu reporte", "codigo": 500 })}).status(500);
              }).finally(() => {
                client.close();
              })

            }
          }
        });
    }
  });
});

/**
 * Ruta principal de la API
 */
router.get('/', function (req, res, next) {

  res.render('index', { title: 'Bumpify' });

});

module.exports = router;