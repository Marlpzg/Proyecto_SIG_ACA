var express = require('express');
var router = express.Router();
var dbClient = require('../utils/conn');

/* GET home page. */
router.get('/', function (req, res, next) {

  dbClient.connect(error => {
    const collection = dbClient.db(process.env.DATABASE_NAME).collection("test");

    collection.insertOne({ test: "Yes" })
      .then(res => {
        console.log("Wena bro.")
        dbClient.close();
      })
      .catch(err => {
        console.log(err)
      });
  });

  res.render('index', { title: 'Express' });
});

module.exports = router;
