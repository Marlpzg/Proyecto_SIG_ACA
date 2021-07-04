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

      collection.find({ coords: { $geoWithin: { $centerSphere: [[lon, lat], converter.km2rad(5)] } } }).project({ "_id": 0, "coords": 1, "type": 1 })
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
                }
              ]
            }
            collection.find(filter).project({ "_id": 0 })
              .toArray((err, result) => {

                if (err) res.json(err).status(500)
                else {
                  dangerPoints = 0;
                  result.forEach(p => {
                    dangerPoints++;
                    p.votes.forEach(v => {
                      dangerPoints += v[1];
                    })
                  })

                  res.json({ points: JSON.stringify({ "data": points })/*, dangerLevel: dangerPoints */}).status(200);
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

      let lon = req.body.lon;
      let lat =  req.body.lat;

      let event = {
        date: new Date(),
        type: req.body.type,
        desc: req.body.desc,
        coords: [lon, lat],
        votes: []
      }

      const collection = client.db(process.env.DATABASE_NAME).collection("events");
      let filter = {
        $and: [
          {
            coords: {
              $geoWithin: {
                $centerSphere: [
                  [lon, lat],
                  converter.km2rad(0.05)
                ]
              }
            }
          },
          {
            type: req.body.type        
          }
        ]
      }
      console.log(event);
      
      collection.find(filter).project({ "_id": 0 })
        .toArray((err, result) => {

          if (err) res.json(err).status(500)
          else {

            console.log(result);
            //res.json({ response: "text" }).status(200);
            res.json({ points: JSON.stringify({ "data": result })}).status(200);
            client.close();

          }

        });

      
    }
  });

});

router.get('/', function (req, res, next) {

  res.render('index', { title: 'Bumpify' });

});

module.exports = router;

/*dbClient.connect(error => {
    const collection = dbClient.db(process.env.DATABASE_NAME).collection("test");

    collection.insertOne({ test: "Yes" })
      .then(res => {
        console.log("Wena bro.")
        dbClient.close();
      })
      .catch(err => {
        console.log(err)
      });
  });*/