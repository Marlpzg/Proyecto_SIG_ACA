var express = require('express');
var router = express.Router();
var db = require('../utils/conn');

router.get('/test', function (req, res, next) {
  let client = db.client();

  client.connect(err => {
    if (err) res.json(err).status(500);

    const collection = client.db(process.env.DATABASE_NAME_T).collection("shipwrecks");

    collection.find().limit(10)
      .toArray((err, result) => {

        if (err) res.json(err).status(500);

        res.json(result).status(200);
        client.close();

      });
  });

});

router.get('/', function (req, res, next) {
  
  res.render('index', { title: 'Bumpify'});

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