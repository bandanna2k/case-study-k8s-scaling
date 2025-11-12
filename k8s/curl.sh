
echo "1. Checking webserver..."
IP=`minikube ip`
URL=`minikube service -n case-study case-study-service --url`

#curl -s -I -X GET $URL/v1/status

curl -s $URL/v1/status
echo

curl -s $URL/metrics/rps
echo

echo "DELETE /v1/memory"
curl -i -X DELETE $URL/v1/memory
echo

echo "POST /v1/memory"
curl -i -X POST -d '{"bytes": 1048576}' $URL/v1/memory
curl -s $URL/v1/status
echo


#LOAD_GEN_BASE_URL=`minikube service -n case-study load-generator-service --url`
#echo "RESULTS"
#curl -s -X GET $LOAD_GEN_BASE_URL/v1/results | jq

echo "1. Done"


echo "2. Checking load generator"
curl -s $URL | grep -oP '(?<=Running on: )\S+'

VERTX=`minikube service -n case-study load-generator-service --url`
echo "Load generator @ $VERTX"
echo "2. Done"
