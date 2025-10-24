
set -x

MEMORY_AMOUNT=${1:-1048576}

IP=`minikube ip`
URL=`minikube service -n case-study case-study-service --url`

echo "POST /v1/memory"
curl -i -X POST -d '{"bytes": '${MEMORY_AMOUNT}'}' $URL/v1/memory
curl -s $URL/v1/status
echo