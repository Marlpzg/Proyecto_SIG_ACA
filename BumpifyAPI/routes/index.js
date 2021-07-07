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
var router = express.Router();
var db = require('../utils/conn');
var converter = require('../utils/converter');

const oneDay = 24 * 60 * 60 * 1000;

router.get('/test', function (req, res, next) {
  let client = db.client();

  client.connect(err => {
    if (err) {
      res.json(err).status(500);
    } else {
      const collection = client.db(process.env.DATABASE_NAME_T).collection("shipwrecks");

      collection.find().limit(10).project({ "coordinates": 1 })
        .toArray((err, result) => {

          if (err) res.json(err).status(500);

          res.json({ points: JSON.stringify({ "data": result }) }).status(200);
          client.close();

        });
    }
  });

});

/*
1 - Asalto
2 - Bache
3 - Obstáculo
4 - Asesinato
*/

router.get('/surroundings', function (req, res, next) {

  //console.log(req.headers.location);
  let loc = JSON.parse(req.headers.location);
  let lon = loc.lon;
  let lat = loc.lat;

  let client = db.client();

  client.connect(err => {
    if (err) {
      res.json(err).status(500);
    } else {
      const collection = client.db(process.env.DATABASE_NAME).collection("events");

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

      collection.find(filter1).project({ "_id": 0, "coords": 1, "type": 1, "desc": 1, "date": 1, "votesNum": {$size: "$votes"} })
        .toArray((err, result) => {

          if (err) res.json(err).status(500)
          else {

            let points = result;

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
            collection.find(filter).project({ "_id": 0 })
              .toArray((err, result) => {

                if (err) res.json(err).status(500)
                else {
                  dangerPoints = 0;
                  result.forEach((p, index) => {
                    dangerPoints++;
                    p.votes.forEach(v => {
                      dangerPoints++;
                    })
                  })

                  //console.log(points);

                  res.json({ points: JSON.stringify({ "data": points, "dangerLevel": dangerPoints }) }).status(200);
                  client.close();

                }

              });

          }

        });
    }
  });

});

router.post('/newEvent', function (req, res, next) {

  let client = db.client();

  client.connect(err => {
    if (err) {
      res.json(err).status(500);
    } else {

      let lon = req.body.lat;
      let lat = req.body.lon;
      let nDays = 0;

      const collection = client.db(process.env.DATABASE_NAME).collection("events");
      if (req.body.type == 1 || req.body.type == 4) {
        nDays = 30;
      } else {
        nDays = 1;
      }

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

          if (err) res.json({ res: JSON.stringify({"data": "Error de conexión a la base de datos", "codigo": 500 })}).status(500);
          else {
            let event = {};
            if (result.length > 0) {
              //Update
              event = result[0];

              if (!event.votes.includes(req.body.user) &&
                event.user != req.body.user) {
                event.votes.push(req.body.user);
                collection.updateOne(
                  { "_id": event._id },
                  { $set: { "votes": event.votes, "date": new Date() } })
                  .then(ev => {
                    res.json({ res: JSON.stringify({"data": "¡Gracias por tu reporte!", "codigo": 200 })}).status(200);
                  }).catch(err => {
                    res.json({ res: JSON.stringify({"data": "Ha ocurrido un error al procesar tu reporte", "codigo": 500 })}).status(500);
                  }).finally(() => {
                    client.close();
                  })
              } else {
                res.json({ res: JSON.stringify({"data": "¡Gracias por tu reporte!", "codigo": 200 })}).status(200);
              }

            } else {
              //Create new
              event = {
                user: req.body.user,
                date: new Date(),
                type: req.body.type,
                desc: req.body.desc,
                coords: [lon, lat],
                votes: []
              }

              collection.insertOne(event).then(ev => {
                res.json({ res: JSON.stringify({"data": "¡Gracias por tu reporte!", "codigo": 200 })}).status(200);
              }).catch(err => {
                res.json({ res: JSON.stringify({"data": "Ha ocurrido un error al procesar tu reporte", "codigo": 500 })}).status(500);
              }).finally(() => {
                client.close();
              })

            }
            //res.json({ response: "text" }).status(200);
          }

        });


    }
  });

});

router.get('/', function (req, res, next) {

  res.render('index', { title: 'Bumpify' });

});

module.exports = router;