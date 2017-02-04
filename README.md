# pio-template-fpm
Frequent Pattern Mining

This PredictionIO template is based on FP Growth algorithm described in [MLlib - Frequent Pattern Mining](https://spark.apache.org/docs/1.5.1/mllib-frequent-pattern-mining.html) and in API  [org.apache.spark.mllib.fpm.FPGrowth](https://spark.apache.org/docs/1.5.1/api/scala/index.html#org.apache.spark.mllib.fpm.FPGrowth)

## Deployment
```
pio template get goliasz/pio-template-fpm --version "0.3.1" fpm1
cd fpm1
pio build --verbose
pio app new fpm1 --access-key 1234
sh data/import_test.sh <<APP_ID>>
nano engine.json <-- set APP_NAME to fpm1
pio train
pio deploy --port 8200 &
```
## Test
```
curl -i -X POST http://localhost:8200/queries.json -H "Content-Type: application/json" -d '{"items":["t", "y"]}'
```
Should give in result
```
{"consequentItems":[{"items":["x"],"confidence":1.0},{"items":["z"],"confidence":1.0}]}
```

## License
This Software is licensed under the Apache Software Foundation version 2 licence found here: http://www.apache.org/licenses/LICENSE-2.0

