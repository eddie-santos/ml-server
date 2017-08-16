# ml-server

Model pipeline is trained and saved `trained-cv-pipeline`, and is loaded as a `CrossValidatorModel`. Endpoint an be hit at `http://localhost:9000/titanic` via a `POST` request (from the project home directory):

```
curl --request POST --header "Content-type: application/json" --data @passengers.json http://localhost:9000/titanic
```