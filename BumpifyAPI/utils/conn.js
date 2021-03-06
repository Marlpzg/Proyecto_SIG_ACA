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
const MongoClient = require('mongodb').MongoClient;
const uri = "mongodb+srv://" + process.env.DATABASE_USER_NAME + ":" + process.env.DATABASE_PASSWORD + "@bumpifydb.tslk6.mongodb.net/" + process.env.DATABASE_NAME + "?retryWrites=true&w=majority";

/**
 * Esta función crea una nueva instancia del cliente de MongoDB, 
 * dicha instancia deberá ser cerrada luego de terminar su ciclo de vida.
 * @returns Instancia de MongoClient.
 */
function client(){
    return new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true });
}

module.exports = {client}