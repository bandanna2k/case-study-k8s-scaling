
IP=`minikube ip`
URL=`minikube service -n case-study kubernetes-bootcamp --url`

curl -I -X GET $URL
curl -s $URL | grep -oP '(?<=Running on: )\S+'

VERTX=`minikube service -n case-study load-generator-service --url`
echo "Load generator @ $VERTX"
