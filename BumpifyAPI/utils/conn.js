const MongoClient = require('mongodb').MongoClient;
const uri = "mongodb+srv://" + process.env.DATABASE_USER_NAME + ":" + process.env.DATABASE_PASSWORD + "@bumpifydb.tslk6.mongodb.net/" + process.env.DATABASE_NAME + "?retryWrites=true&w=majority";

function client(){
    return new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true });
}

module.exports = {client}