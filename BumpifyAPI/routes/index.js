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
      //collection.createIndex( { "coords" : "2dsphere" } );
      
      collection.find({ coords: { $geoWithin: { $centerSphere: [ [ lon, lat ], converter.km2rad(5) ] } } }).limit(10).project({ "_id": 0, "coords": 1, "type": 1 })
        .toArray((err, result) => {

          if (err) res.json(err).status(500);

          //res.json({ "data": result }).status(200);
          res.json({ points: JSON.stringify({ "data": result }) }).status(200);
          client.close();

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