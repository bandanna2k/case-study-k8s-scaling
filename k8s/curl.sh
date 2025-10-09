
echo "1. Checking kubernetes-bootcamp webserver..."
IP=`minikube ip`
URL=`minikube service -n case-study case-study-service --url`

curl -I -X GET $URL
echo "1. Done"


echo "2. Checking load generator"
curl -s $URL | grep -oP '(?<=Running on: )\S+'

VERTX=`minikube service -n case-study load-generator-service --url`
echo "Load generator @ $VERTX"
echo "2. Done"
