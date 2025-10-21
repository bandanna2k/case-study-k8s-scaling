
set -x

echo "1. Checking webserver..."
IP=`minikube ip`
URL=`minikube service -n case-study case-study-service --url`

#curl -s -I -X GET $URL/v1/status

curl -s $URL/v1/status
echo

curl -s $URL/metrics/rps
echo

curl -s -X POST -d "{'bytes': 1048576}" $URL/v1/memory
echo


echo "1. Done"


echo "2. Checking load generator"
curl -s $URL | grep -oP '(?<=Running on: )\S+'

VERTX=`minikube service -n case-study load-generator-service --url`
echo "Load generator @ $VERTX"
echo "2. Done"
