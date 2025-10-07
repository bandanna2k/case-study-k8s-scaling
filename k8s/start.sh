
set -e

source stop.sh
source lint.sh

# Start and wait to be ready
minikube start
minikube addons enable metrics-server
minikube kubectl -- wait pod --all --for=condition=Ready --namespace=kube-system --timeout=60s

# Upload into minikube, the load generator image
minikube image load load-generator

source apply.sh
minikube kubectl -- wait pod --all --for=condition=Ready --namespace=case-study --timeout=120s

source current-state.sh

source curl.sh

set -x

